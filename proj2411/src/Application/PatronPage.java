package Application;
import DAOs.*;
import JavaBean.*;

import java.util.*;


public class PatronPage {
	Patrons currentAccount;
    
    String[] Criteria = {"BookID","Name","Category","Author","Publisher"};
    ArrayList<String> Request;
    
    String[] options = {"Borrow a book","Return a book","Reserve a book","Quit"};
    
    ArrayList<Book> BorrowedBook;
    ArrayList<Book> ReturnedBook;
    ArrayList<Book> ReservedBook;

    ReserveRecordDAO reserveRecordDAO;
    BorrowRecordDAO borrowRecordDAO;
    BookDAO bookDAO;


    PatronPage(Patrons patron){
    	currentAccount = patron;
    	
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.bookDAO = new BookDAO();
        
        this.BorrowedBook = (ArrayList<Book>) bookDAO.getMultiQuery(
                "select * from BOOK_CATALOGUE where BookID " +
                        "in (select BookID from BORROWING_RECORD where PatronID = ? and Status = ?)",Book.class,patron.getHkid(),BorrowRecord.Sts.INBORROWING.toString());
        
        this.reserveRecordDAO = new ReserveRecordDAO();
        
        this.ReservedBook = (ArrayList<Book>) bookDAO.getMultiQuery(
                "select * from BOOK_CATALOGUE where BookID " +
                        "in (select BookID from RESERVE_RECORD where PatronID = ? and Status = ?)",Book.class,patron.getHkid(),ReserveRecord.Sts.RESERVING.toString());

        this.ReturnedBook = new ArrayList<Book>();

    }

    public void mainpage(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWelcome Back, "+currentAccount.getName()+"!");
        System.out.println("What would you like to do today?");
        while(true){
            for(int i = 0; i<options.length;i++){
                System.out.printf("\t%d. %s\n",i+1,options[i]);
            }
            switch (scanner.next()) {
                case ("1") -> {
                    if(currentAccount.isActivated())BorrowPage();
                    else {
                        System.out.println("You cannot borrow a book as you may possess some books yet to return");
                    }
                }
                case ("2") -> ReturnPage();
                case ("3") -> ReservePage();
                case ("4") -> {Quit();return;}
            }
            System.out.println("\nAnything else you want to do today?");
        }

    }
    private void ReservePage() {
    	Scanner scanner = new Scanner(System.in);
        ArrayList<Book> SearchingResult = (ArrayList<Book>) control.search(Criteria,bookDAO,Book.class);

        System.out.println("The following is the searching result by your request: ");
        control.printBookList(SearchingResult);

        if(SearchingResult.isEmpty()) return;
        
        System.out.println("Please choose the book you want to reserve by entering the result index, separate them with space for multiple choices");
        String selection = scanner.nextLine();
        for(String index : selection.split(" ")) {
            if(control.isIndexes(SearchingResult,index)){
                Book temp = SearchingResult.get(Integer.parseInt(index)-1);
                if(containsBook(temp,BorrowedBook)){
                    System.out.println("Sorry, you have already borrowed this book");
                    return;
                }else if(containsBook(temp,ReservedBook)) {
                    System.out.println("Sorry, you have already reserved this book");
                    return;
                }else{
                    ReservedBook.add(temp);
                }
            }
            else {
                System.out.println("Invalid index: "+index+" is not a valid index");
                return;
            }
        }
        
        if(!selection.isEmpty() && !ReservedBook.isEmpty()) {
        	for (Book book : ReservedBook) {
        		reserveRecordDAO.update("insert into RESERVE_RECORD values(?,?,CURRENT_DATE+1,?)",currentAccount.getHkid(), book.getBookID(), ReserveRecord.Sts.RESERVING.toString());
        	}
            System.out.println("You have successfully reserved the following books:");
            control.printBookList(ReservedBook);
        	System.out.println("Please wait for further notification");
        }
	}

	private void Quit() {
    	Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to save all the borrowing/return record today? Y/N");
        if(scanner.nextLine().equalsIgnoreCase("Y")){
            System.out.println("Saving...");
            borrowRecordDAO.commit();
            bookDAO.commit();
            System.out.println("========Saving complete !========");

        }else {
            System.out.println("Exiting without saving...\n");
            borrowRecordDAO.rollback();
            bookDAO.rollback();
        }
    }
	
    private void BorrowPage() {
    	Scanner scanner = new Scanner(System.in);
        ArrayList<Book> SearchingResult = (ArrayList<Book>) control.search(Criteria,bookDAO,Book.class);

        System.out.println("The following is the searching result by your request: ");
        control.printBookList(SearchingResult);

        if(SearchingResult.isEmpty()) return;
        
        System.out.println("Please choose the book you want to borrow by entering the result index, separate them with space for multiple choices");
        String selection = scanner.nextLine();
        
        for(String index : selection.split(" ")){
            if(control.isIndexes(SearchingResult,index)){
                Book temp = SearchingResult.get(Integer.parseInt(index)-1);
                int position = getQueuePosition(temp);
                
                if (!temp.isAvailable(ReturnReserveQueue(temp))){
                    System.out.println("Sorry, the book \""+temp.getName()+"\" seems to be out of available, please reserve it afterwards");
                    return;
                }else if (containsBook(temp,BorrowedBook)) {
                    System.out.println("Sorry, you have already borrowed this book");
                    return;
                }else if (position > 0) {
                    System.out.printf("Sorry, there are still %d %s front of you queuing for this book%n",position,position > 1 ? "people":"person");
                    return;
                }else{
                    BorrowedBook.add(SearchingResult.get(Integer.parseInt(index)-1));
                }
            }else {
                System.out.println("Invalid index: "+index+" is not a valid index");
                return;
            }

         
        }

        if(!selection.isEmpty() && !BorrowedBook.isEmpty()) {
            for (Book book : BorrowedBook) {
                String BookID = book.getBookID();
                borrowRecordDAO.update("insert into BORROWING_RECORD values(?,?,?,CURRENT_DATE,CURRENT_DATE+15,?)", currentAccount.getHkid(), BookID, currentAccount.getEmail(), BorrowRecord.Sts.INBORROWING.toString());
                bookDAO.update("update BOOK_CATALOGUE set NumAvailable = ? where BookID = ?", book.getNumAvailable() - 1, book.getBookID());
                reserveRecordDAO.update("update RESERVE_RECORD set Status = ? where BookID = ? and PatronID = ?","COLLECTED",book.getBookID(),currentAccount.getHkid());
                ReservedBook.remove(book);
            }
            System.out.println("You have successfully borrowed the following books:");
            control.printBookList(BorrowedBook);
        }
    }
    
    private boolean containsBook(Book temp, List<Book> list) {
    	for(Book book : list) {
    		if(book.getBookID().equals(temp.getBookID())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private void ReturnPage() {
        int i = 0;
    	Scanner scanner = new Scanner(System.in);
        System.out.println("Here is the all the book you have borrowed:");
        control.printBookList(BorrowedBook);
        
        System.out.println("Please choose the book you want to return by entering the result index, separate them with space for multiple choices");
        for(String index : scanner.nextLine().split(" ")){
            if(control.isIndexes(BorrowedBook,index)){
                ReturnedBook.add(BorrowedBook.get(Integer.parseInt(index)-1));
                BorrowedBook.remove(Integer.parseInt(index)-1);
            }
        }
        for(Book book : ReturnedBook){
            borrowRecordDAO.update("update BORROWING_RECORD set Status = ? where BookID = ?",BorrowRecord.Sts.RETURNED.toString(),book.getBookID());
            borrowRecordDAO.update("update BOOK_CATALOGUE set NumAvailable = ? where BookID = ?",book.getNumAvailable()+1,book.getBookID());
        }

        System.out.println("You have successfully Returned the following books:");
        for(Book book : ReturnedBook){
            System.out.println(book.toString());
        }
    }

    private ArrayList<Patrons> ReturnReserveQueue(Book temp){
    	return (ArrayList<Patrons>) new PatronsDAO().getMultiQuery("select * from Patrons where HKID in (select PatronID from RESERVE_RECORD where BookID = ? and Status = 'RESERVING')", Patrons.class, temp.getBookID());
    }
    private int getQueuePosition(Book temp){
    	int i = 0;
    	for(Patrons p : ReturnReserveQueue(temp)) {
    		if(p.getHkid().equals(currentAccount.getHkid())) {
    			return i;
    		}
    		i++;
    	}
    	return -1;
    }
}