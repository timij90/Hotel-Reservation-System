package application.models;

public class Guest {
    private int guestId;
    private String title;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String email;

    public Guest(int guestId, String title, String firstName, String lastName, String address, String phone, String email) {
        this.guestId = guestId;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

	// Getters and Setters
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
    	return "Full name: " + title + "" + firstName + " " + lastName 
    			+ "\nPhone: " + phone + "\nEmail: " + email + "\nAddress: " + address;
    }
}

