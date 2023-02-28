package Application;

import DAOs.*;

import JavaBean.*;
import Utils.druidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;
public class AdminPage {
    String adminName;
    String adminID;
    AdminsDAO adminsDAO;
    PatronsDAO patronsDAO;
    BookDAO bookDAO;

    private final String[] options = {
            "Add/Remove/Update/ Admins",
            "Add/Remove/Update/ Books",
            "Add/Remove/Update/ Patrons",
            "Acquiring Report",
            "Quit"
    };
    private final String[] SubOptions = {
            "Add",
            "Remove",
            "Update"};



    public AdminPage(Admins admins) {
        this.adminID = admins.getID();
        this.adminName = admins.getName();
        this.adminsDAO = new AdminsDAO();
        this.bookDAO = new BookDAO();
        this.patronsDAO = new PatronsDAO();
		// TODO Auto-generated constructor stub
	}

	public void mainpage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWelcome Back, admin " + adminName + "!");
        System.out.println("What would you like to do today?");
        while (true) {
            for (int i = 0; i < options.length; i++) {
                System.out.printf("\t%d. %s\n", i + 1, options[i]);
            }
            switch (scanner.next()) {
                case ("1") -> AdminsUML();
                case ("2") -> BookUML();
                case ("3") -> PatronUML();
                case ("4") -> AcquringReport();
                case ("5") -> {
                    Quit();
                    return;
                }
            }
            System.out.println("\nAnything else you want to do today?");
        }

    }
    private void AcquringReport() {
        List<Object[]> analyticReport;
        QueryRunner qr = new QueryRunner();
        String sql = "select Extract(MONTH from BorrowDate),count(*) from BORROWING_RECORD group by Extract(MONTH from BorrowDate) order by Extract(MONTH from BorrowDate)";
        String title;

        try {
            analyticReport = qr.query(druidUtils.getConnection(),sql, new ArrayListHandler());
            System.out.printf("The following is the analysis report of the instances of borrowing book over the past %d months:%n",analyticReport.size());
            title = String.format("%8s|%8s","Month","Books Borrowed per Month");
            System.out.println(title);
            System.out.println("-".repeat(title.length()));
            
            String largestMonth = "";
            int largest = 0;
            for(Object[] months : analyticReport) {
            	if(((BigDecimal)months[1]).intValue() > largest) {
            		largest = ((BigDecimal)months[1]).intValue();
            		largestMonth = String.valueOf(months[0]);
            	}
            }

            for(Object[] objects: analyticReport){
                System.out.printf("%8s|%8s%n",objects[0],objects[1]);
            }

            System.out.println("It seems that "+largestMonth+" would be the busiest month of the library, please considering arranging more staffs during this month");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void PatronUML() {
        Scanner scanner = new Scanner(System.in);
        String[] Criteria = new String[]{"HKID", "Name", "Password", "Email", "ActiveStat"};
        System.out.println("Please choose the intended operation (enters 'quit' to return)");

        for (int i = 0; i < SubOptions.length; i++) {
            System.out.printf("\t%d. %s patrons\n", i + 1, SubOptions[i]);
        }

        switch (scanner.next()) {
            case ("1") ->control.createNewAccount(new String[4], "select * from PATRONS where HKID = ?", patronsDAO, Patrons.class);
            case ("2") -> {
                ArrayList<Patrons> TargetedPatrons = (ArrayList<Patrons>) control.search(Criteria, patronsDAO, Patrons.class);
                Remover(TargetedPatrons, patronsDAO, Patrons.class);
            }
            case ("3") -> {
                ArrayList<Patrons> TargetedPatrons = (ArrayList<Patrons>) control.search(Criteria, patronsDAO, Patrons.class);
                Updater(Criteria, TargetedPatrons, patronsDAO, Patrons.class);
            }
            case ("quit") -> {}
            default -> System.out.println("Invalid option");
        }

    }
    private void AdminsUML() {
        String[] printout;
        String sql;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please choose the intended operation (enters 'quit' to return)");
        System.out.println("\t- 1." + SubOptions[0] + " a new admin");
        System.out.println("\t- 2." + SubOptions[1] + " admin account \"" + adminName + "\"");
        System.out.println("\t- 3." + SubOptions[2] + " admin account \"" + adminName + "\"");
        switch (scanner.next()) {
            case ("1") ->
                    control.createNewAccount(new String[3], "select * from ADMINS where HKID = ? and NAME = ? and Password = ?", adminsDAO, Admins.class);
            case ("2") -> {
                System.out.println("Are you sure to remove the admin account \"" + adminName + "\" ? Y/N");
                if (scanner.next().equals("Y")) {
                    adminsDAO.update("delete from ADMINS where HKID = ?", adminID.toUpperCase());
                    System.out.println("Admin account \"" + adminName + "\"deleted successfully\n");
                }
            }
            case ("3") -> {
                printout = new String[]{"Name", "Password"};
                sql = "update ADMINS set %s where HKID = ?";
                System.out.println("Please enter the new admin name/password, leave blank for the part to remain unchanged");
                String request = control.requestGenerator(printout);

                if (!request.equals("")) {
                    adminsDAO.update(String.format(sql, request), adminID);
                    System.out.println("Admin account \"" + adminName + "\"updated successfully");
                }
            }
            case ("quit") -> {
            }
            default -> System.out.println("Invalid option");
        }
    }
    private void BookUML() {
        String[] Criteria = new String[]{"BookID", "Name", "Category", "Author", "Publisher", "NumAvailable"};
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please choose the intended operation (enters 'quit' to return)");
            for (int i = 0; i < SubOptions.length; i++) {
                System.out.printf("\t%d. %s books\n", i + 1, SubOptions[i]);
            }
            switch (scanner.nextLine()) {
                case ("1") -> bookAdd(Criteria, bookDAO);
                case ("2") -> {
                    ArrayList<Book> TargetedBooks = (ArrayList<Book>) control.search(Criteria, bookDAO, Book.class);
                    Remover(TargetedBooks, bookDAO, Book.class);
                }
                case ("3") -> {
                    ArrayList<Book> TargetedBooks = (ArrayList<Book>) control.search(Criteria, bookDAO, Book.class);
                    Updater(Criteria, TargetedBooks, bookDAO, Book.class);
                }
                case ("quit") -> {
                    return;
                }
                default -> System.out.println("Invalid option");
            }
            scanner.reset();
        }
    }
    private void Updater(String[] Criteria, ArrayList<? extends BeanInterface> TargetedObjects, BasicDAO basicDAO, Class type) {
        if (TargetedObjects.isEmpty()) {
            System.out.println("Sorry, there is no searching result for that fits your criteria");
            return;
        }

        if (Selector(TargetedObjects, type, "update")) return;
        System.out.printf("Please enter the following new information for the selected %s, leave blank for the part to remain unchanged\n", type.getSimpleName());
        String request = control.requestGenerator(Arrays.copyOfRange(Criteria, 1, Criteria.length));

        if (request.equals("")) {
            System.out.println();
            return;
        }
        String sql = type.equals(Book.class) ? "update BOOK_CATALOGUE set %s where BOOKID = ?" : "update PATRONS set %s where HKID = ?";
        for (BeanInterface obj : TargetedObjects) {
            basicDAO.update(String.format(sql, request), obj.getID());
        }
        System.out.printf("%s update Complete\n", type.getSimpleName());
    }
    private boolean Selector(ArrayList<? extends BeanInterface> TargetedObjects, Class type, String operation) {
        String selection;
        Scanner scanner = new Scanner(System.in);
        String typeName;
        
        
        
        System.out.printf("\nWhich of the following %s you want to %s? (enter 'all' to update all)\n", type.getSimpleName(), operation);
        for (int i = 0; i < TargetedObjects.size(); i++) {
            System.out.printf("\t%d. %s\n", i + 1, TargetedObjects.get(i));
        }
        selection = scanner.nextLine();

        if (!selection.equalsIgnoreCase("all")) {
            for (String i : selection.split(" ")) {if (!control.isIndexes(TargetedObjects, i)) {System.out.println("Invalid index\n");return false;}}
            for (BeanInterface b : TargetedObjects) {if (!selection.contains(String.valueOf(TargetedObjects.indexOf(b) + 1))) {TargetedObjects.remove(b);}}

            System.out.printf("Are you sure to %s the following %s? Y/N\n", operation,type.getSimpleName().toLowerCase());
            for (int i = 0; i < TargetedObjects.size(); i++) {
                System.out.printf("\t%d. %s\n", i + 1, TargetedObjects.get(i));
            }
        } else {
            System.out.printf("Are you sure to %s all of them? Y/N\n", operation);
        }

        return !scanner.nextLine().equalsIgnoreCase("y");
    }
    private void Remover(ArrayList<? extends BeanInterface> TargetedObjects, BasicDAO basicDAO, Class type) {
        if (TargetedObjects.isEmpty()) {
            System.out.println("Sorry, there is no searching result for that fits your criteria\n");

        } else {
            if (Selector(TargetedObjects, type, "remove")) return;

            String sql = type.equals(Book.class) ? "delete from BOOK_CATALOGUE where BookID = ?" : "delete from PATRONS where HKID = ?";
            for (BeanInterface b : TargetedObjects) {
                basicDAO.update(sql, b.getID());
            }
            System.out.printf("%s deletion Complete\n", type.getSimpleName());
        }
    }
    
    private void bookAdd(String[] Criteria, BookDAO bookDao) {
        Scanner scanner = new Scanner(System.in);
        String[] newBookInfo = new String[Criteria.length];
        SecureRandom random = new SecureRandom();
        String newBookIndex = String.valueOf(random.nextInt(100000000));

        System.out.println("Please enter the following information for the new book, leave the id blank for generating random id");
        control.infowriter(Criteria, newBookInfo);
        
        if(!newBookInfo[0].equals("") && new BookDAO().getQuery("select * from BOOK_CATALOGUE where BookID = ?", Book.class, newBookInfo[0]) != null) {
        	System.out.println("Sorry, the book id "+newBookInfo[0]+" is already occupied");
        	return;
        }
        
        System.out.println("\nAre you sure to add the following book? Y/N:");
        for (int i = 0; i < Criteria.length; i++) {
            System.out.printf("\t- %s: %s\n", Criteria[i], newBookInfo[i]);
        }

        if (scanner.next().equalsIgnoreCase("Y")) {
            bookDao.update("insert into BOOK_CATALOGUE values(?,?,?,?,?,?)", newBookInfo[0].equals("") ? newBookIndex : newBookInfo[0], newBookInfo[1], newBookInfo[2], newBookInfo[3], newBookInfo[4], newBookInfo[5]);
            System.out.println("The new book is added successfully");
        }

    }
    private void Quit() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to save all the changes record today? Y/N");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("Saving...");
            adminsDAO.commit();
            patronsDAO.commit();
            bookDAO.commit();
            System.out.println("========Saving complete !========");

        } else {
            System.out.println("Exiting without saving...\n");
            adminsDAO.rollback();
            patronsDAO.rollback();
        }
    }

}

