package application.models;

public class Room {
    private int roomId;
    private String roomType;
    private double rate;
    private String status;

    public Room(int roomId, String roomType, double rate) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.rate = rate;
        this.status = "available";
    }

	// Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    
    public String getStatus() { return status; }
	public void setStatus(String status) { 	this.status = status; }
}


