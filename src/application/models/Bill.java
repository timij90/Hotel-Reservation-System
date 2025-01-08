package application.models;

public class Bill {
    private int billId;
    private Reservation booking;
    private double discountPercentage;

    public Bill(int billId, Reservation booking) {
        this.billId = billId;
        this.booking = booking;
        this.discountPercentage = 0.0;
    }

    public int getBillId() { return billId; }
	public void setBillId(int billId) { this.billId = billId; }
	public Reservation getBooking() { return booking; }
	public void setBooking(Reservation booking) { this.booking = booking; }

	public double applyDiscount(double discountPercent) {
        discountPercentage = discountPercent;
        double amountToPay = booking.calculateTotalCost();
        amountToPay -= amountToPay * (discountPercentage / 100);
        
        return amountToPay;
    }
}
