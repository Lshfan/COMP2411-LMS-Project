package Application;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import DAOs.*;
import JavaBean.*;
import Utils.druidUtils;
import org.apache.commons.dbutils.*;

public class control {
    static Connection connection;
    
    public control(){
    	try {
			databaseInitialize();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
    }

    private static void databaseInitialize() throws SQLException {
        String[] relations = {"PATRONS","BOOK_CATALOGUE","BORROWING_RECORD","RESERVE_RECORD","ADMINS"};
        String[] relationStatements;

        QueryRunner queryRunner = new QueryRunner();
        StringBuilder s = new StringBuilder();

        System.out.println("Initializing...");

        try{
            FileReader reader = new FileReader("src\\SQL_initializer.txt");

            int data = reader.read();
            do {
                s.append((char) data);
                data = reader.read();
            } while (data != -1);
            reader.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        relationStatements = s.toString().split("\n");

        System.out.println("Obtaining connection to database...");
//        connection = DBMSutils.getConnection();
        connection = druidUtils.getConnection();

        System.out.println("Successful connection");

        initializingTables(relations, relationStatements, queryRunner);

        System.out.println("Initialization complete");
        queryRunner.update(connection,"commit");
    }

    public static void mainpage() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        String[] Info;
        String[] printout;
        String input;
        String sql = "select * from %s where HKID = ? and NAME = ? and PASSWORD = ?";
        PatronsDAO register = new PatronsDAO();
        Object account;

        label:
        while(true) {
        	Info = new String[4];
        	printout = new String[]{"1. Sign up", "2. Login"};
            System.out.println("Please choose an option by entering the number (enter 'quit' to exit)");
            for(String c : printout){
                System.out.printf("\t- %s\n",c);
            }

            input = scanner.nextLine();
            switch (input) {
                case "1":
                    //create account
                    createNewAccount(Info, sql, register, Patrons.class);
                    register.commit();
                    break;
                case "2":
                    while (true) {
                        printout = new String[]{"HK id", "Username", "Password", "Admin? Y/N"};

                        System.out.println("Please enter your following information");
                        infowriter(printout, Info);

                        account = Info[3].equalsIgnoreCase("Y") ?
                                new AdminsDAO().getQuery(String.format(sql, "ADMINS"), Admins.class, Info[0].toUpperCase(), Info[1], Info[2]) :
                                new PatronsDAO().getQuery(String.format(sql, "PATRONS"), Patrons.class, Info[0].toUpperCase(), Info[1], Info[2]);

                        if (account == null) {
                            System.out.println("Sorry, invalid username/password, please enter again");
                        } else break;
                    }

                    if (Info[3].equalsIgnoreCase("Y")) {
                        new AdminPage(((Admins) account)).mainpage();
                    } else {
                        new PatronPage((Patrons) account).mainpage();
                    }
                    break;
                case "quit":
                    break label;
            }

        }
        connection.close();
        System.out.println("End of the program");
    }
    
    public static void createNewAccount(String[] Info, String SearchingSql, BasicDAO register, Class clazz) {
        String[] printout = clazz.equals(Patrons.class) ? new String[]{"HK id", "Username", "Password","Email"} : new String[]{"HK id", "Username", "Password"};
        String Table = clazz.equals(Patrons.class) ? "PATRONS" : "ADMINS";
        Object account;
        Scanner scanner = new Scanner(System.in);


        System.out.println("Please enter your following information");
        infowriter(printout, Info);
        account = register.getQuery(String.format(SearchingSql, Table), clazz, Info[0],Info[1],Info[2]);
        if (account != null) {
            System.out.println("Sorry, you already created the account\n");
        } else {
            System.out.print("Please enter the password again: ");

            while (!scanner.nextLine().equals(Info[2])) {
                System.out.print("Unmatched Password, please enter again: ");
            }
            if(clazz.equals(Patrons.class)) {
                register.update(String.format("insert into %s values(?,?,?,?,'T')",Table), Info[0].toUpperCase(), Info[1], Info[2], Info[3]);
            }else {
                register.update(String.format("insert into %s values(?,?,?)",Table), Info[0].toUpperCase(), Info[1], Info[2]);
            }

            System.out.printf("Account %s %s created successfully\n", Info[0], Info[1]);
        }
    }

    public static void infowriter(String[] printout, String[] info){
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < info.length; i++) {
            System.out.printf("\t- %s: ", printout[i]);
            info[i] = scanner.nextLine();
        }
    }

    public static String requestGenerator(String[] Criteria) {
        String value;
        Scanner scanner = new Scanner(System.in);
        StringBuilder builder = new StringBuilder();
        
        for (String c : Criteria) {
            System.out.printf("\t- %s: ", c);
            value = scanner.nextLine();
            if(!value.equals("")){
            	if(!builder.isEmpty()) {builder.append(" and ");}
                builder.append(c.toUpperCase()).append(" = ");
                builder.append(String.format("'%s'", value));
            }
        }
        
        return builder.toString();
    }
    
    public static void printBookList(ArrayList<Book> bookList) {
        if (bookList.isEmpty()) {
            System.out.println("No record of the book that matches the criteria was found...");
            return;
        }
        int i = 0;
        String title = String.format("%4s|%s %s %s %s %s %s","","BookID", "Book Title", "Category", "Author", "Publisher", "No.Copies available");
        System.out.println(title);
        System.out.println("-".repeat(title.length()));
        for (Book book : bookList) {
            System.out.printf("%4s|%s %s %s %s %s %s%n", ++i, book.getBookID(), book.getName(), book.getCategory(), book.getAuthor(), book.getPublisher(), book.getNumAvailable());
        }
    }

    public static List<? extends BeanInterface> search(String[] Criteria, BasicDAO basicDAO,Class clazz) {
        System.out.println("Please enter the request for the books you are looking for, leave blank for the part where you have no requirement");
        String request = requestGenerator(Criteria);
        String table = basicDAO instanceof BookDAO ? "BOOK_CATALOGUE" : "PATRONS";
        String criteria = request.equals("") ? "" : " where "+ request;
        return basicDAO.getMultiQuery(String.format("select * from %s%s",table, criteria),clazz);
    }
    
    public static boolean isIndexes(List targetList, String index) {
        return index.matches("\\d+") && Integer.parseInt(index) >= 1 && Integer.parseInt(index) <= targetList.size();
    }

    private static void initializingTables(String[] relations, String[] relationStatements, QueryRunner queryRunner) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        for(int i = 0; i< relations.length; i++){
            if(!metaData.getTables(null, null, relations[i], null).next()){
                System.out.println("Initializing "+ relations[i]+"...");
                queryRunner.update(connection, relationStatements[i]);
            }
        }

        if(!connection.prepareStatement("select * from ADMINS").executeQuery().next()) {
            queryRunner.update(connection, relationStatements[relationStatements.length-1]);
        }

        String updatingBR = "update BORROWING_RECORD set Status = ? where CURRENT_DATE > ExpRetDate and Status = ?";
        String updatingRR = "update RESERVE_RECORD set Status = ? where CURRENT_DATE > ExpireDate and Status = ?";
        String deactUser = "update PATRONS set ACTIVESTAT = 'F' where HKID in (select PATRONID from BORROWING_RECORD where STATUS = 'OVERDUE')";
        queryRunner.update(connection,updatingBR,BorrowRecord.Sts.OVERDUE.toString(),BorrowRecord.Sts.INBORROWING.toString());
        queryRunner.update(connection,updatingRR,ReserveRecord.Sts.EXPIRED.toString(),ReserveRecord.Sts.RESERVING.toString());
        queryRunner.update(connection,deactUser);
    }

}
