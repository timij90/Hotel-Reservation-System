package application.database;

import application.models.Guest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDatabase {

    public static void addGuest(Guest guest) throws SQLException {
        final String INSERT_QRY = "INSERT INTO Guests (guest_id, title, first_name, last_name, address, phone_number, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(INSERT_QRY)) {
            ps.setInt(1, guest.getGuestId());
            ps.setString(2, guest.getTitle());
            ps.setString(3, guest.getFirstName());
            ps.setString(4, guest.getLastName());
            ps.setString(5, guest.getAddress());
            ps.setString(6, guest.getPhone());
            ps.setString(7, guest.getEmail());
            ps.executeUpdate();
			System.out.println("1 row inserted into Guests");
        }
    }
    
    public static Guest searchGuestById(int guestId) throws SQLException {
        final String SELECT_QRY = "SELECT * FROM Guests WHERE guest_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_QRY)) {
            ps.setInt(1, guestId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("title"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("email")
                );
            }
        }
        return null;
    }

    public static Guest searchGuestByPhone(String phone) throws SQLException {
        String SELECT_QRY = "SELECT * FROM Guests WHERE phone_number = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_QRY)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("title"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("email")
                );
            }
        }
        return null;
    }

    public static List<Guest> getAllGuests() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        final String SELECT_QRY = "SELECT * FROM Guests";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_QRY)) {

            while (rs.next()) {
                guests.add(new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("title"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("email")
                ));
            }
        }
        return guests;
    }
}
