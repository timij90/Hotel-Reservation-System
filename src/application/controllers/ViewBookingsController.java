package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

import application.database.ReservationDatabase;
import application.models.*;

public class ViewBookingsController  {

    @FXML private TableView<TableRowData> currentBookingsTable;
    @FXML private TableColumn<TableRowData, Integer> bookingIdCol;
    @FXML private TableColumn<TableRowData, String> guestNameCol;
    @FXML private TableColumn<TableRowData, Integer> numDaysCol;
    @FXML private TableColumn<TableRowData, Integer> numRoomsCol;
    @FXML private TableColumn<TableRowData, String> roomTypeCol;
    @FXML private Label numBookingsLabel;
        
    public void initialize() throws SQLException {
    	// Set up table columns
        setUpColumns();
        
        ObservableList<TableRowData> data = FXCollections.observableArrayList();
        
        for(Reservation booking : ReservationDatabase.getAllReservations()) {
        	int bookingId = booking.getBookingId();
        	String guestName = booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName();
        	int numDays = booking.getNumOfDays();
        	int numRooms = booking.getNumOfRooms();
        	String roomType = booking.getRoomType();
    	    
    	    data.add(new TableRowData(bookingId, guestName, roomType, numRooms, numDays));
        }
        int numBookings = ReservationDatabase.getAllReservations().size();
        numBookingsLabel.setText("Number of current bookings are: " + String.valueOf(numBookings) );
        
        currentBookingsTable.setItems(data);
    }
    
    private void setUpColumns() {
    	// Booking ID Column (Integer)
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

    	// Guest Name Column
        guestNameCol.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        
        // Room Type Column
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        
        // Number of Rooms Column
        numRoomsCol.setCellValueFactory(new PropertyValueFactory<>("numRooms"));
        
        // Number of Days Column
        numDaysCol.setCellValueFactory(new PropertyValueFactory<>("numDays"));
    }
}
