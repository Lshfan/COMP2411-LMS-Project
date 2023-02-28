package JavaBean;

import DAOs.*;

import Utils.druidUtils;
import oracle.sql.DATE;

import java.sql.SQLException;
import java.util.*;


import org.apache.commons.dbutils.QueryRunner;


/*public class Book {
    int BookID;
    String Title;
    String Author;
    String Publisher;
    int NumOfCopies;
    boolean isAvailable;
    
    public Book() {};
    
    public Book(int number, String name, String author, String publisher, int quantity){
        this.Title = name;
        this.BookID = number;
        this.Publisher = publisher;
        this.NumOfCopies = quantity;
        this.Author = author;
        this.isAvailable = this.NumOfCopies > 0;
    }
}*/
public class Book implements BeanInterface{
    private String BookID;
    private String Title;
    private String Category;
    private String Author;
    private String Publisher;
    private int NumOfCopies;

    public Book() {};

    public Book(String number, String name, String Category,String author, String publisher, int quantity){
        this.setName(name);
        this.setCategory(Category);
        BookID = number;
        this.setPublisher(publisher);
        this.setNumAvailable(quantity);
        this.setAuthor(author);

    }

	public String getBookID() {
        return BookID;
    }
    public void setBookID(String bookID) {
    	BookID = bookID;
    }

    public String getName() {
        return Title;
    }

    public void setName(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public int getNumAvailable() {
        return NumOfCopies;
    }

    public void setNumAvailable(int numOfCopies) {
        NumOfCopies = numOfCopies;
    }

    public boolean isAvailable(List<?> list) {
    	
        return NumOfCopies > list.size();
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
    @Override
    public String toString(){
        return String.format("%s %s %s %s %s",BookID,Title,Category,Author,Publisher);
    }

    @Override
    public String getID() {
        return getBookID();
    }

}