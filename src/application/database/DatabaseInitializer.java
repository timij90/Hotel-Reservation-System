package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseInitializer {
	
	private static HashMap<String, Integer> roomAvailability = new HashMap<>(); 
    private static HashMap<String, Double> roomRates = new HashMap<>();

    // Static block to initialize with default values
    static {
        // Initialize room availability
        roomAvailability.put("Single", 20);    // 20 Single rooms
        roomAvailability.put("Double", 15);    // 15 Double rooms
        roomAvailability.put("Deluxe", 10);    // 10 Deluxe rooms
        roomAvailability.put("Penthouse", 5);  // 5 Penthouse rooms
        
        roomRates.put("Single", 100.0);
        roomRates.put("Double", 180.0);
        roomRates.put("Deluxe", 300.0);
        roomRates.put("Penthouse", 500.0);
    }

    private static final String CREATE_ADMIN_TBL_QRY = "CREATE TABLE IF NOT EXISTS Admins ("
            + "admin_id VARCHAR(15) PRIMARY KEY, "
            + "password TEXT NOT NULL);";

    private static final String CREATE_RESERVATIONS_TBL_QRY = "CREATE TABLE IF NOT EXISTS Reservations ("
            + "booking_id INTEGER PRIMARY KEY, "
            + "check_in_date DATE, "
            + "check_out_date DATE, "
            + "num_of_rooms INTEGER, "
            + "room_type TEXT, "
            + "num_of_days INTEGER, "
            + "guest_id INTEGER, "
            + "FOREIGN KEY (guest_id) REFERENCES Guests(guest_id));";
    
    private static final String CREATE_ROOMS_TBL_QRY = "CREATE TABLE IF NOT EXISTS Rooms ("
            + "room_id INTEGER PRIMARY KEY, "
            + "room_type TEXT, "
            + "status TEXT DEFAULT \"available\", "
            + "rate REAL);";
    
    private static final String CREATE_GUESTS_TBL_QRY = "CREATE TABLE IF NOT EXISTS Guests ("
            + "guest_id INTEGER PRIMARY KEY, "
            + "title TEXT, "
            + "first_name VARCHAR(50), "
            + "last_name VARCHAR(50), "
            + "address VARCHAR(50), "
            + "phone_number VARCHAR(10), "
            + "email TEXT);";
    
    private static final String CREATE_BILLS_TBL_QRY = "CREATE TABLE IF NOT EXISTS Bills ("
            + "bill_id INTEGER PRIMARY KEY, "
            + "total_amount REAL, "
            + "discounted_amount REAL, "
            + "discount_percentage REAL, "
            + "status TEXT DEFAULT \"paid\", "
            + "booking_id INTEGER, "
            + "FOREIGN KEY (booking_id) REFERENCES Reservations(booking_id));";
    
    private static final String CREATE_BOOKEDROOMS_TBL_QRY = "CREATE TABLE IF NOT EXISTS Booked_Rooms ("
            + "booking_id INTEGER, "
            + "room_id INTEGER, "
            + "FOREIGN KEY (booking_id) REFERENCES Reservations(booking_id), "
            + "FOREIGN KEY (room_id) REFERENCES Rooms(room_id));";

    public static void initializeDatabase(Connection conn) throws SQLException {
        try (PreparedStatement pstmt1 = conn.prepareStatement(CREATE_ADMIN_TBL_QRY);
             PreparedStatement pstmt2 = conn.prepareStatement(CREATE_GUESTS_TBL_QRY);
             PreparedStatement pstmt3 = conn.prepareStatement(CREATE_RESERVATIONS_TBL_QRY);
             PreparedStatement pstmt4 = conn.prepareStatement(CREATE_ROOMS_TBL_QRY);
        	 PreparedStatement pstmt5 = conn.prepareStatement(CREATE_BILLS_TBL_QRY);
             PreparedStatement pstmt6 = conn.prepareStatement(CREATE_BOOKEDROOMS_TBL_QRY);) {
            pstmt1.execute();
            pstmt2.execute();
            pstmt3.execute();
            pstmt4.execute();
            pstmt5.execute();
            pstmt6.execute();
        }
    }
    
    public static void insertData(Connection conn) {
        try {
        	conn.createStatement().execute("DELETE FROM Admins;");
        	conn.createStatement().execute("DELETE FROM Booked_Rooms;");
        	conn.createStatement().execute("DELETE FROM Rooms;");
        	conn.createStatement().execute("DELETE FROM Bills;");
        	conn.createStatement().execute("DELETE FROM Reservations;");
        	conn.createStatement().execute("DELETE FROM Guests;");
        	
			// Add admin accounts
            Database.insertAdmin(conn, "admin1", "pass123");
			Database.insertAdmin(conn, "admin2", "pass456");

            // Add sample rooms
            final String INSERT_QRY = "INSERT INTO Rooms (room_id, room_type, rate) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(INSERT_QRY)) {
            	for (String roomType : roomAvailability.keySet()) {
                    int count = roomAvailability.get(roomType);
                    double rate = getRoomRate(roomType);

                    for (int i = 0; i < count; i++) {
                        ps.setInt(1, idGen()); ps.setString(2, roomType); ps.setDouble(3, rate); 
                        ps.executeUpdate();
            			System.out.println("1 row inserted into Rooms");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static double getRoomRate(String roomType) { return roomRates.getOrDefault(roomType, 0.0); }
    
    private static int idGen() {
        int randomPart = (int) (Math.random() * 10000); // random value between 0 and 9999
        int id = (int) (System.currentTimeMillis() % 100000) + randomPart;
        
        return id;
    }
}
