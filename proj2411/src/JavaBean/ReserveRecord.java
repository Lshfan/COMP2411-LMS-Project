package JavaBean;

import java.util.Date;

public class ReserveRecord {
    private String PatronID;
    private String BookID;
    private Date ReserveDate;
    private Sts Status;

    public ReserveRecord(){}

    public ReserveRecord(String patronID, String bookID, Date reserveDate, String status) {
        setPatronID(patronID);
        setBookID(bookID);
        setReserveDate(reserveDate);
        setStatus(status);
    }


    public String getPatronID() {
        return PatronID;
    }

    public void setPatronID(String patronID) {
        PatronID = patronID;
    }

    public String getBookID() {
        return BookID;
    }

    public void setBookID(String bookID) {
        BookID = bookID;
    }

    public Date getReserveDate() {
        return ReserveDate;
    }

    public void setReserveDate(Date reserveDate) {
        ReserveDate = reserveDate;
    }

    public Sts getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = Sts.valueOf(status.toUpperCase());
    }

    public enum Sts{RESERVING,EXPIRED,COLLECTED}
}
