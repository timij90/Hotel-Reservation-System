package application.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MockDatabase {
    private static HashMap<String, Admin> admins = new HashMap<>();
    private static HashMap<Integer, Guest> guests = new HashMap<>();
    private static HashMap<Integer, Bill> bills = new HashMap<>();
    private static HashMap<Integer, Reservation> reservations = new HashMap<>();
    private static HashMap<String, Integer> roomAvailability = new HashMap<>(); 
    private static HashMap<String, Double> roomRates = new HashMap<>();

    // Static block to initialize with default values
    static {
        admins.put("admin1", new Admin("admin1", "pass123"));
        admins.put("admin2", new Admin("admin2", "pass456"));
        
        // Initialize room availability
        roomAvailability.put("Single", 50);    // 50 Single rooms
        roomAvailability.put("Double", 30);    // 30 Double rooms
        roomAvailability.put("Deluxe", 20);    // 20 Deluxe rooms
        roomAvailability.put("Pent House", 5);  // 5 Penthouse rooms
        
        roomRates.put("Single", 100.0);
        roomRates.put("Double", 180.0);
        roomRates.put("Deluxe", 300.0);
        roomRates.put("Pent House", 500.0);
    }

    public static HashMap<String, Admin> getAdmins() { return admins; }
	public static void setAdmins(HashMap<String, Admin> admins) { 
		MockDatabase.admins = admins;
	}
	public static HashMap<Integer, Guest> getGuests() { return guests; }
	public static void setGuests(HashMap<Integer, Guest> guests) {
		MockDatabase.guests = guests;
	}
	public static HashMap<Integer, Bill> getBills() { return bills; }
	public static void setBills(HashMap<Integer, Bill> bills) {
		MockDatabase.bills = bills;
	}
	public static HashMap<Integer, Reservation> getReservations() { return reservations; }
	public static void setReservations(HashMap<Integer, Reservation> reservations) {
		MockDatabase.reservations = reservations;
	}
	public static void addGuest(Guest guest) { guests.put(guest.getGuestId(), guest); }
    public static Guest getGuest(int guestId) { return guests.get(guestId); }
    
    public static void addBill(Bill bill) { bills.put(bill.getBillId(), bill); }
    public static Bill getBill(int billId) { return bills.get(billId); }

    public static void addReservation(Reservation reservation) { reservations.put(reservation.getBookingId(), reservation); }
    public static Reservation getReservation(int bookingId) { return reservations.get(bookingId); }
    
    public static HashMap<String, Integer> getRoomAvailability() { return roomAvailability; }
    public static void setRoomAvailability(HashMap<String, Integer> availability) { roomAvailability = availability; }
    
    public static double getRoomRate(String roomType) { return roomRates.getOrDefault(roomType, 0.0); }

    public static Reservation searchBookingByPhone(String phone) {
        for (Reservation reservation : reservations.values()) {
            if (reservation.getGuest().getPhone().equals(phone)) {
                return reservation;
            }
        }
        return null;
    }
    
    public static Guest searchGuestByPhone(String phone) {
        for (Guest guest : guests.values()) {
            if (guest.getPhone().equals(phone)) {
                return guest;
            }
        }
        return null;
    }

    public static boolean validateAdminCredentials(String username, String password) {
        Admin admin = admins.get(username);
        return admin != null && admin.getPassword().equals(password);
    }
    
    public static boolean bookRoom(String roomType, int quantity) {
        int available = roomAvailability.getOrDefault(roomType, 0);
        if (available >= quantity) {
            roomAvailability.put(roomType, available - quantity);
            return true;
        }
        return false; // Not enough rooms available
    }

    public static void cancelRoomBooking(String roomType, int quantity) {
        int available = roomAvailability.getOrDefault(roomType, 0);
        roomAvailability.put(roomType, available + quantity);
    }
    
    public static List<Room> getAllAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        int roomId = 0;

        for (String roomType : roomAvailability.keySet()) {
            int count = roomAvailability.get(roomType);
            double rate = getRoomRate(roomType);

            for (int i = 0; i < count; i++) {
                availableRooms.add(new Room(roomId != 0 ? roomId : idGen(), roomType, rate));
            }
        }
        return availableRooms;
    }

    public static int checkRoomAvailability(String roomType) {
        return MockDatabase.getRoomAvailability().getOrDefault(roomType, 0);
    }
    
    private static int idGen() {
        int randomPart = (int) (Math.random() * 1000); // random value between 0 and 999
        int id = (int) (System.currentTimeMillis() % 100000) + randomPart;
        
        return id;
    }

}

