package application.database;

import application.models.Guest;
import application.models.Reservation;
import application.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDatabase {

    public static void addReservation(Reservation reservation) throws SQLException {
        final String INSERT_QRY = "INSERT INTO Reservations (booking_id, check_in_date, check_out_date, num_of_rooms, room_type, num_of_days, guest_id) " 
        				   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(INSERT_QRY)) {
        	
        	java.sql.Date checkInDate = new java.sql.Date(reservation.getCheckInDate().getTime()); 
        	java.sql.Date checkOutDate = new java.sql.Date(reservation.getCheckOutDate().getTime()); 
        	
        	ps.setInt(1, reservation.getBookingId()); 
        	ps.setDate(2, checkInDate); 
        	ps.setDate(3, checkOutDate); 
        	ps.setInt(4, reservation.getNumOfRooms()); 
        	ps.setString(5, reservation.roomTypes()); 
        	ps.setInt(6, reservation.getNumOfDays()); 
        	ps.setInt(7, reservation.getGuest().getGuestId());
        	
            ps.executeUpdate();
            System.out.println("1 row inserted into Reservations");
            
            Database.bookRoom(conn, reservation);
        }
    }
    
    public static void removeReservation(Reservation reservation) throws SQLException {
    	final String DELETE_QRY = "DELETE FROM Reservations "+
    							  "WHERE booking_id = ?";
        try (Connection conn = Database.connect();
        	 PreparedStatement ps = conn.prepareStatement(DELETE_QRY)) {
        	for(Room room : reservation.getRooms()) {
        		Database.updateRoomStatus(conn, room.getRoomId(), "available");
        	}
        	
        	ps.setInt(1, reservation.getBookingId());
        	ps.executeUpdate();
        	System.out.println("1 row deleted from Reservations");
        	
    		Database.removeBookedRoom(conn, reservation);        	
        }
    }
    
    public static Reservation getReservation(int bookingId) throws SQLException {
        final String SELECT_QRY = "SELECT * FROM Reservations WHERE booking_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_QRY)) {
            ps.setInt(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                	Guest guest = GuestDatabase.searchGuestById(rs.getInt("guest_id"));
                    List<Room> rooms = getRoomsByReservation(bookingId); // Fetch associated rooms
                    
                    Reservation reservation =  new Reservation(
						                            rs.getInt("booking_id"),
						                            rs.getDate("check_in_date"),
						                            rs.getDate("check_out_date"),
						                            rooms
						                    );
                    reservation.setGuest(guest);
                    reservation.setNumRooms(rs.getInt("num_of_rooms"));
                    reservation.setRoomType(rs.getString("room_type"));
                    
                    return reservation;
                }
            }
        }
        return null;
    }
    
    public static Reservation searchBookingByPhone(String phone) throws SQLException {
        final String SELECT_QRY = "SELECT r.booking_id, r.room_type, r.num_of_days, r.num_of_rooms, r.check_in_date, r.check_out_date, r.guest_id "
                     + "FROM Reservations r "
                     + "INNER JOIN Guests g ON r.guest_id = g.guest_id "
                     + "WHERE g.phone_number = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_QRY)) {
            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Retrieve guest using phone
                    Guest guest = GuestDatabase.searchGuestByPhone(phone);
                    List<Room> rooms = getRoomsByReservation(rs.getInt("booking_id")); // Fetch associated rooms
                    
                    Reservation reservation =  new Reservation(
						                            rs.getInt("booking_id"),
						                            rs.getDate("check_in_date"),
						                            rs.getDate("check_out_date"),
						                            rooms
                    							 );
                    reservation.setGuest(guest);
                    reservation.setNumRooms(rs.getInt("num_of_rooms"));
                    reservation.setRoomType(rs.getString("room_type"));
                    
                    return reservation;
                }
            }
        }
        return null; // No reservation found
    }
    
    private static List<Room> getRoomsByReservation(int bookingId) throws SQLException {
        final String SELECT_QRY = "SELECT r.room_id, r.room_type, r.status, r.rate " +
                       "FROM Booked_Rooms br " +
                       "JOIN Rooms r ON br.room_id = r.room_id " +
                       "WHERE br.booking_id = ?";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_QRY)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	
                	Room room = new Room(
		                            rs.getInt("room_id"),
		                            rs.getString("room_type"),
		                            rs.getDouble("rate")
                				 );
                	
                	room.setStatus(rs.getString("status"));
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }
    
    public static List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        final String SELECT_QRY = "SELECT * FROM Reservations";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_QRY)) {

            while (rs.next()) {
            	Guest guest = GuestDatabase.searchGuestById(rs.getInt("guest_id"));
                List<Room> rooms = getRoomsByReservation(rs.getInt("booking_id")); // Fetch associated rooms
                
                Reservation reservation =  new Reservation(
					                            rs.getInt("booking_id"),
					                            rs.getDate("check_in_date"),
					                            rs.getDate("check_out_date"),
					                            rooms
					                    	 );
                reservation.setGuest(guest);
                reservation.setNumRooms(rs.getInt("num_of_rooms"));
                reservation.setRoomType(rs.getString("room_type"));
                
                reservations.add(reservation);
            }
        }
        return reservations;
    }
}
