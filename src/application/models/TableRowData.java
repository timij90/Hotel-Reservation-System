package application.models;

import javafx.beans.property.*;

public class TableRowData {
    private final SimpleIntegerProperty bookingId;
    private final SimpleStringProperty guestName;
    private final SimpleStringProperty roomType;
    private final SimpleIntegerProperty numRooms;
    private final SimpleIntegerProperty numDays;

    public TableRowData(int bookingId, String guestName, String roomType, int numRooms, int numDays) {
        this.bookingId = new SimpleIntegerProperty(bookingId);
    	this.guestName = new SimpleStringProperty(guestName);
    	this.roomType = new SimpleStringProperty(roomType);
        this.numRooms = new SimpleIntegerProperty(numRooms);
        this.numDays = new SimpleIntegerProperty(numDays);
    }

	public String getGuestName() { return guestName.get(); }
    public void setGuestName(String guestName) { this.guestName.set(guestName); }
    public int getBookingId() { return bookingId.get(); }
    public void setBookingId(int bookingId) { this.bookingId.set(bookingId); }
    public String getRoomType() { return roomType.get(); }
    public void setRoomType(String roomType) { this.roomType.set(roomType); }
    public int getNumRooms() { return numRooms.get(); }
    public void setNumRooms(int numRooms) { this.numRooms.set(numRooms); }
    public int getNumDays() { return numDays.get(); }
    public void setNumDays(int numDays) { this.numDays.set(numDays); }

    public SimpleStringProperty guestNameProperty() { return guestName; }
    public SimpleIntegerProperty bookingIdProperty() { return bookingId; }
    public SimpleStringProperty roomTypeProperty() { return roomType; }
	public SimpleIntegerProperty numRoomsProperty() { return numRooms; }
	public SimpleIntegerProperty numDaysProperty() { return numDays; }
}
