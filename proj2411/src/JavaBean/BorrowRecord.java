package JavaBean;

import java.util.Date;

public class BorrowRecord {
/*	String PatronID;
	String BookID;
	String Email;
	String BorrowDate;
	String ExpRetDate;
	String Status;

	BorrowRecord(){}

	public BorrowRecord(String patronID, String bookID, String email, String borrowDate, String expRetDate, String status) {
		PatronID = patronID;
		BookID = bookID;
		Email = email;
		BorrowDate = borrowDate;
		ExpRetDate = expRetDate;
		Status = status;
	}*/
	private String PatronID;
	private String BookID;
	String Email;
	Date BorrowDate;
	Date ExpRetDate;
	Sts Status;
	
	BorrowRecord(){}

	public BorrowRecord(String patronID, String bookID, String email, Date borrowDate, Date expRetDate, String status) {
		this.PatronID = patronID;
		this.BookID = bookID;
		setEmail(email);
		setBorrowDate(borrowDate);
		setExpRetDate(expRetDate);
		setStatus(status);
	}


	public String getPatronID() {
		return PatronID;
	}

	public String getBookID() {
		return BookID;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public Date getBorrowDate() {
		return BorrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		BorrowDate = borrowDate;
	}

	public Date getExpRetDate() {
		return ExpRetDate;
	}

	public void setExpRetDate(Date expRetDate) {
		ExpRetDate = expRetDate;
	}

	public Sts getStatus() {
		return Status;
	}

	public void setStatus(String status) {Status = Sts.valueOf(status.toUpperCase());}
	

	public enum Sts{INBORROWING,OVERDUE,RETURNED}
}
