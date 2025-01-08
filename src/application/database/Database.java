package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import application.models.Bill;
import application.models.Reservation;
import application.models.Room;

public class Database {
	
	private static final String DB_CONNECTION = Main.class.getResource("database/hotel_reservation.db").toExternalForm();
	private static final String DB_JDBC = "jdbc:sqlite:";
	
	public static Connection connect() throws SQLException {
		return DriverManager.getConnection(DB_JDBC+DB_CONNECTION);
	}
	
	public static boolean validateAdminCredentials(Connection conn, String id, String pass) {
		final String SELECT_QRY = "SELECT * FROM Admins WHERE admin_id = ? AND password = ?";
		try(PreparedStatement ps = conn.prepareStatement(SELECT_QRY)){
			ps.setString(1, id);
			ps.setString(2, pass);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				return true;
		}catch(SQLException ex) {ex.printStackTrace();}
		return false;
	}
	
	public static void insertAdmin(Connection conn, String adminId, String pass) {
		final String INSERT_QRY = "INSERT INTO Admins (admin_id, password) VALUES (?, ?)";
		try(PreparedStatement pstmt = conn.prepareStatement(INSERT_QRY);){
			pstmt.setString(1, adminId);
			pstmt.setString(2, pass);
			
			pstmt.executeUpdate();
			System.out.println("1 row inserted into Admins");
		}catch(SQLException ex) {ex.printStackTrace();}
	}
	
	public static boolean addRoom(Connection conn, String roomType) {
		final String INSERT_QRY = "INSERT INTO Rooms (room_id, room_type, rate) VALUES (?, ?, ?)";
		try(PreparedStatement ps = conn.prepareStatement(INSERT_QRY);){
			ps.setInt(1, idGen()); ps.setString(2, roomType); 
			
			switch(roomType) {
			case "Single": ps.setDouble(3, 100.0);
			case "Double": ps.setDouble(3, 180.0);
			case "Deluxe": ps.setDouble(3, 300.0);
			case "Pent House": ps.setDouble(3, 500.0);
			}
			ps.executeUpdate();
			System.out.println("1 row added into Rooms");
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean removeRoom(Connection conn, int roomId) {
		final String DELETE_QRY = "DELETE FROM Rooms " +
								  "WHERE room_id = ?";
		try(PreparedStatement ps = conn.prepareStatement(DELETE_QRY);){
			ps.setInt(1, roomId);
			ps.executeUpdate();
			
			System.out.println("1 row deleted from Rooms");
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static List<Room> getAllAvailableRooms(Connection conn) {
		final String SELECT_QRY = "SELECT * FROM Rooms "+
								  "WHERE status = ?";
		try(PreparedStatement pstmt = conn.prepareStatement(SELECT_QRY);) {
			pstmt.setString(1, "available");
			List<Room> availableRooms = new ArrayList<>();
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getDouble("rate")
    				 );
    	
		    	room.setStatus(rs.getString("status"));
		        availableRooms.add(room);
			}
	        return availableRooms;
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }

	public static void bookRoom(Connection conn, Reservation reservation) {
		final String INSERT_QRY = "INSERT INTO Booked_Rooms (booking_id, room_id) VALUES (?, ?)";
		try(PreparedStatement ps = conn.prepareStatement(INSERT_QRY);){
			for(Room room : reservation.getRooms()) {
				ps.setInt(1, reservation.getBookingId());
				ps.setInt(2, room.getRoomId());
				
				ps.executeUpdate();
				updateRoomStatus(conn, room.getRoomId(), "booked");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeBookedRoom(Connection conn, Reservation reservation) {
		final String DELETE_QRY = "DELETE FROM Booked_Rooms "+
								  "WHERE booking_id = ?";
		try(PreparedStatement ps = conn.prepareStatement(DELETE_QRY);){
				ps.setInt(1, reservation.getBookingId());
				
				ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateRoomStatus(Connection conn, int roomId, String status ) {
		final String UPDATE_QRY = "UPDATE Rooms " +
						   		  "SET status = ? " +
				                  "WHERE room_id = ?" ;
		try(PreparedStatement ps = conn.prepareStatement(UPDATE_QRY)) {
			ps.setString(1, status);
			ps.setInt(2, roomId);
			
			ps.executeUpdate();
			System.out.println("1 row updated in Rooms");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public static int checkRoomAvailability(Connection conn, String roomType) throws SQLException {
		final String SELECT_QRY = "SELECT COUNT(room_type) FROM Rooms "+
								  "WHERE status = ? "+
				                  "AND room_type = ?";
		try(PreparedStatement ps = conn.prepareStatement(SELECT_QRY)){
			ps.setString(1, "available");
			ps.setString(2, roomType);
			
			ResultSet rs = ps.executeQuery(); if(rs.next()) return rs.getInt(1); 
		}
		return 0;
	}

	public static void addBill(Bill bill, Reservation reservation, double selectedDiscountRate) throws SQLException {
		final String INSERT_QRY = "INSERT INTO Bills (bill_id, total_amount, discounted_amount, discount_percentage, booking_id) "+
						          "VALUES (?, ?, ?, ?, ?)";
		try(Connection conn = Database.connect();
			PreparedStatement ps = conn.prepareStatement(INSERT_QRY)){
			
			int billId = bill.getBillId();
			int bookingId = reservation.getBookingId();
			double totalAmount = reservation.calculateTotalCost();
			double discountedAmount = bill.applyDiscount(selectedDiscountRate);
			
			ps.setInt(1, billId);
			ps.setDouble(2, totalAmount);
			ps.setDouble(3, discountedAmount);
			ps.setDouble(4, selectedDiscountRate);
			ps.setInt(5, bookingId);
			
			ps.executeUpdate();
			
			ReservationDatabase.removeReservation(reservation);
			System.out.println("1 row inserted into Bills");
		}
	}
	
	private static int idGen() {
        int randomPart = (int) (Math.random() * 10000); // random value between 0 and 9999
        int id = (int) (System.currentTimeMillis() % 100000) + randomPart;
        
        return id;
    }
}
