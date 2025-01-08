package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.models.DeluxeRoom;
import application.models.DoubleRoom;
import application.models.PentHouse;
import application.models.SingleRoom;

public class RoomDatabase {
	
	public static List<SingleRoom> getAllSingleRooms() throws SQLException{
		List<SingleRoom> singleRooms = new ArrayList<>();
		final String SELECT_QRY = "SELECT * FROM Rooms " +
								  "WHERE room_type = ? " +
								  "AND status = ?"; 
		try(Connection conn = Database.connect()){
			PreparedStatement ps = conn.prepareStatement(SELECT_QRY);
			ps.setString(1, "Single");
			ps.setString(2, "available");
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				singleRooms.add(new SingleRoom(rs.getInt("room_id")));
			}
		}
		
		return singleRooms;
	}

	public static List<DoubleRoom> getAllDoubleRooms() throws SQLException {
		List<DoubleRoom> doubleRooms = new ArrayList<>();
		final String SELECT_QRY = "SELECT * FROM Rooms " +
								  "WHERE room_type = ? " +
								  "AND status = ?"; 
		try(Connection conn = Database.connect()){
			PreparedStatement ps = conn.prepareStatement(SELECT_QRY);
			ps.setString(1, "Double");
			ps.setString(2, "available");
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				doubleRooms.add(new DoubleRoom(rs.getInt("room_id")));
			}
		}
		
		return doubleRooms;
	}

	public static List<DeluxeRoom> getAllDeluxeRooms() throws SQLException {
		List<DeluxeRoom> deluxeRooms = new ArrayList<>();
		final String SELECT_QRY = "SELECT * FROM Rooms " +
								  "WHERE room_type = ? " +
								  "AND status = ?"; 
		try(Connection conn = Database.connect()){
			PreparedStatement ps = conn.prepareStatement(SELECT_QRY);
			ps.setString(1, "Deluxe");
			ps.setString(2, "available");
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				deluxeRooms.add(new DeluxeRoom(rs.getInt("room_id")));
			}
		}
		
		return deluxeRooms;
	}

	public static List<PentHouse> getAllPentHouses() throws SQLException {
		List<PentHouse> pentHouses = new ArrayList<>();
		final String SELECT_QRY = "SELECT * FROM Rooms " +
								  "WHERE room_type = ? " +
								  "AND status = ?"; 
		try(Connection conn = Database.connect()){
			PreparedStatement ps = conn.prepareStatement(SELECT_QRY);
			ps.setString(1, "Penthouse");
			ps.setString(2, "available");
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				pentHouses.add(new PentHouse(rs.getInt("room_id")));
			}
		}
		
		return pentHouses;
	}

}
