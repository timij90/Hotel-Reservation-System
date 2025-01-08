package application.models;

import java.util.Date;
import java.util.List;

public class Reservation {
    private int bookingId;
    private Date checkInDate;
    private Date checkOutDate;
    private int numRooms;
    private Guest guest;
    private String roomType;
    private List<Room> rooms;

    public Reservation(int bookingId, Date checkInDate, Date checkOutDate, List<Room> rooms) {
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.rooms = rooms;
        this.numRooms = 0;
        this.guest = null;
        this.roomType = "";
    }

	public int getBookingId() { return bookingId; }
	public void setBookingId(int bookingId) { this.bookingId = bookingId; }
	
	public Date getCheckInDate() { return checkInDate; }
	public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
	
	public Date getCheckOutDate() { return checkOutDate; }
	public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
	
	public int getNumOfRooms() { return numRooms; }
	public void setNumRooms(int numRooms) { this.numRooms = numRooms; }
	
	public int getNumOfDays() {	return calculateTotalDays(); }
	public double getRates() { return calculateTotalRates(); }
	
	public void setGuest(Guest guest) { this.guest = guest; }
	public Guest getGuest() {return guest;}
	
	public List<Room> getRooms() { return rooms; }
	 
	public String roomTypes() {
		StringBuilder str = new StringBuilder();
	    int single_cnt = 0;
	    int double_cnt = 0;
	    int deluxe_cnt = 0;
	    int pentHouse_cnt = 0;
	    
	    for(Room room : rooms) {
	        if(room instanceof SingleRoom) { single_cnt++; } 
	        else if(room instanceof DoubleRoom) { double_cnt++; } 
	        else if(room instanceof DeluxeRoom) { deluxe_cnt++; } 
	        else if(room instanceof PentHouse) { pentHouse_cnt++; }
	    }
	    str.append(single_cnt> 0 ? single_cnt +"-Single ":"");
	    str.append(double_cnt> 0 ? double_cnt +"-Double ":"");
	    str.append(deluxe_cnt> 0 ? deluxe_cnt +"-Deluxe ":"");
	    str.append(pentHouse_cnt> 0 ? pentHouse_cnt +"-Pent House":"");

	    return str.toString();
	}
	public String getRoomType() { return roomType; }
	public void setRoomType(String roomType) { this.roomType = roomType; }

	// Calculate cost based on room rate and duration
    public double calculateTotalCost() {
        int days = calculateTotalDays();
        double totalRates = calculateTotalRates();
        
        double totalCost = totalRates * days;
        return totalCost;
    }
    
    private int calculateTotalDays() {
    	long duration = checkOutDate.getTime() - checkInDate.getTime();
        int days = (int) (duration / (1000 * 60 * 60 * 24));
        
        return days;
    }
    
    private double calculateTotalRates() {
        double totalRates = 0;

        for (Room room : rooms) {
                double roomCost = room.getRate();
                totalRates += roomCost;
        }

        return totalRates;
    }    
}
