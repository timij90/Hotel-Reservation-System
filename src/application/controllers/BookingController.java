package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import application.database.Database;
import application.database.RoomDatabase;
import application.models.*;

public class BookingController {

    @FXML private Label availableRoomsLabel;
    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;
    @FXML private Label daysLabel;
    @FXML private CheckBox deluxeRoomCheck;
    @FXML private CheckBox doubleRoomCheck;
    @FXML private TextField numGuests;
    @FXML private CheckBox penthouseRoomCheck;
    @FXML private Label ratePerDay;
    @FXML private CheckBox singleRoomCheck;
    @FXML private Spinner<Integer> deluxeRoomQuantity;
    @FXML private Spinner<Integer> doubleRoomQuantity;
    @FXML private Spinner<Integer> penthouseRoomQuantity;
    @FXML private Spinner<Integer> singleRoomQuantity;
    
    private Reservation booking;
                
    public void initialize() {
        setupNumberField(numGuests);
        setupSpinner(singleRoomQuantity, singleRoomCheck, "Single");
        setupSpinner(doubleRoomQuantity, doubleRoomCheck, "Double");
        setupSpinner(deluxeRoomQuantity, deluxeRoomCheck, "Deluxe");
        setupSpinner(penthouseRoomQuantity, penthouseRoomCheck, "Pent House");
    }

    private void setupNumberField(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
        	if (!newVal.matches("\\d*") || newVal.startsWith("0")) { 
        		numGuests.setText(oldVal); 
        	}
        });
    }

    private void setupSpinner(Spinner<Integer> spinner, CheckBox checkBox, String roomType) {
        try(Connection conn = Database.connect()) {
            int maxRooms = Database.checkRoomAvailability(conn, roomType);
            maxRooms = Math.max(1, maxRooms); // Ensure minimum is 1
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxRooms, 1));
            spinner.setDisable(true); // Start disabled
            
            checkBox.setOnAction(event -> {
                boolean selected = checkBox.isSelected();
                spinner.setDisable(!selected);
                if (!selected) spinner.getValueFactory().setValue(1); // Reset to minimum value
            });
        } catch (SQLException e) {
            showAlert("Database Error", "Unable to retrieve room availability for " + roomType);
            e.printStackTrace();
        }
    }

    @FXML
    void checkAvailableRooms(ActionEvent event) throws SQLException {
        try {
            int noOfGuests = Integer.parseInt(numGuests.getText());
            LocalDate checkIn = checkInDate.getValue();
            LocalDate checkOut = checkOutDate.getValue();
            
            if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
                showAlert("Invalid Dates", "There was a problem when selecting dates");
                return;
            }

            if (noOfGuests <= 0) throw new NumberFormatException();
            
            // Show room availability dynamically
            boolean isAvailable = showRoomAvailabilityDialog();
            
            if(!isAvailable) {
            	availableRoomsLabel.setText("Sorry, there are no rooms available");
            	availableRoomsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            	return;
            }
            
            String suggestion;
            if (noOfGuests <= 2) suggestion = "We recommend a Single Room for your stay.";
            else if (noOfGuests < 5) suggestion = "We recommend a Double Room or two Single Rooms.";
            else suggestion = "We recommend a combination of Double and Single Rooms.";
            
            availableRoomsLabel.setText(suggestion);
            
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number of guests.");
            numGuests.requestFocus();
            e.printStackTrace();
        }
    }

    @FXML
    void submitBooking(ActionEvent event) throws IOException {
    	try {	
	        LocalDate checkIn = checkInDate.getValue();
	        LocalDate checkOut = checkOutDate.getValue();
	
	        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
	            showAlert("Invalid Dates", "Please select valid check-in and check-out dates.");
	            return;
	        }
	
	        Date bookInDate = Date.from(checkIn.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        Date bookOutDate = Date.from(checkOut.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        
	        List<Room> rooms = setUpRooms();
	
	        booking = new Reservation(idGen(), bookInDate, bookOutDate, rooms);
	        
	        int numRooms = rooms.size();
	        booking.setNumRooms(numRooms);
	        
	    	showAlert("Success", "Booking saved successfully");
	    	
	    	// Load next scene
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/GuestScreen.fxml"));
	    	Stage bookingStage = (Stage) numGuests.getScene().getWindow();
	    	Stage guestStage = new Stage();
	    	Scene newScene = new Scene(loader.load());
	    	
	    	// Pass the database to the controller
	        GuestController guestController = loader.getController();
	        guestController.setUpBooking(booking);
	    	
	        guestStage.setScene(newScene);
	        guestStage.setTitle("Guest Page");
	        
	        bookingStage.close();
	        guestStage.show();
	        
        } catch (Exception e) {
            showAlert("Booking Error", "An error occurred while saving the booking.");
            e.printStackTrace();
        }
    }
    
    @FXML
    void calculateRatePerDay() {
        try {            
            LocalDate checkIn = checkInDate.getValue();
            LocalDate checkOut = checkOutDate.getValue();
            
            if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
	            showAlert("Invalid Dates", "Please select valid check-in and check-out dates.");
	            return;
	        }
            
            Date bookInDate = Date.from(checkIn.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date bookOutDate = Date.from(checkOut.atStartOfDay(ZoneId.systemDefault()).toInstant());
            	            
            List<Room> rooms = setUpRooms();
            
            Reservation booking = new Reservation(idGen(), bookInDate, bookOutDate, rooms);
            
            double rates = booking.getRates();
            int days = booking.getNumOfDays();
            
            String formattedPayment = NumberFormat.getCurrencyInstance(Locale.US).format(rates);
            daysLabel.setText(String.valueOf(days));
            ratePerDay.setText("Rate per day: " + formattedPayment);
          
        } catch (Exception e) {
        	showAlert("Validation Error", "No room is selected");
            ratePerDay.setText("Error calculating rate.");
            e.printStackTrace();
        }
    }
    
    private List<Room> setUpRooms() throws SQLException {
    	List<Room> rooms = new ArrayList<Room>();
    	
        // Parse room quantities from input fields
    	int singleQuantity = singleRoomCheck.isSelected() ? singleRoomQuantity.getValue() : 0;
        int doubleQuantity = doubleRoomCheck.isSelected() ? doubleRoomQuantity.getValue() : 0;
        int deluxeQuantity = deluxeRoomCheck.isSelected() ? deluxeRoomQuantity.getValue() : 0;
        int penthouseQuantity = penthouseRoomCheck.isSelected() ? penthouseRoomQuantity.getValue() : 0;
        
        // Create all rooms in a single call
        rooms = createRooms(singleQuantity, doubleQuantity, deluxeQuantity, penthouseQuantity);
        
        return rooms;
    }
    
    private int idGen() {
        int randomPart = (int) (Math.random() * 1000); // random value between 0 and 999
        int id = (int) (System.currentTimeMillis() % 100000) + randomPart;
        
        return id;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
    private List<Room> createRooms(int singleQuantity, int doubleQuantity, int deluxeQuantity, int penthouseQuantity) throws SQLException {
    	List<Room> rooms = new ArrayList<>();
    	Random rand = new Random();

        if(singleQuantity > 0) {
        	List<SingleRoom> singleRooms = RoomDatabase.getAllSingleRooms();
            if (singleRooms.isEmpty()) throw new SQLException("No single rooms available.");
        	for(int i = 0; i < singleQuantity; i++) {
        		SingleRoom singleRoom = singleRooms.get(rand.nextInt(singleRooms.size()));
        		rooms.add(singleRoom);
        	}
        }
        
        if(doubleQuantity > 0) {
        	List<DoubleRoom> doubleRooms = RoomDatabase.getAllDoubleRooms();
            if (doubleRooms.isEmpty()) throw new SQLException("No double rooms available.");
        	for(int i = 0; i < doubleQuantity; i++) {
        		DoubleRoom doubleRoom = doubleRooms.get(rand.nextInt(doubleRooms.size()));
        		rooms.add(doubleRoom);
        	}
        }
        
        if(deluxeQuantity > 0) {
        	List<DeluxeRoom> deluxeRooms = RoomDatabase.getAllDeluxeRooms();
            if (deluxeRooms.isEmpty()) throw new SQLException("No deluxe rooms available.");
        	for(int i = 0; i < deluxeQuantity; i++) {
        		DeluxeRoom deluxeRoom = deluxeRooms.get(rand.nextInt(deluxeRooms.size()));
        		rooms.add(deluxeRoom);
        	}
        }
        
        if(penthouseQuantity > 0) {
        	List<PentHouse> penthouses = RoomDatabase.getAllPentHouses();
            if (penthouses.isEmpty()) throw new SQLException("No penthouses available.");
        	for(int i = 0; i < penthouseQuantity; i++) {
        		PentHouse penthouse = penthouses.get(rand.nextInt(penthouses.size()));
        		rooms.add(penthouse);
        	}
        }
        
        return rooms;
    }
    
    private boolean showRoomAvailabilityDialog() throws SQLException { 
    	Dialog<Void> dialog = new Dialog<>(); 
    	dialog.setTitle("Room Availability"); 
    	
    	try(Connection conn = Database.connect()){
	    	int availableSingleRooms = Database.checkRoomAvailability(conn, "Single"); 
	    	int availableDoubleRooms = Database.checkRoomAvailability(conn, "Double"); 
	    	int availableDeluxeRooms = Database.checkRoomAvailability(conn, "Deluxe"); 
	    	int availablePenthouseRooms = Database.checkRoomAvailability(conn, "Penthouse");
    	
	    	VBox vbox = new VBox(
	    		    createAvailabilityLabel("Single Rooms", availableSingleRooms),
	    		    createAvailabilityLabel("Double Rooms", availableDoubleRooms),
	    		    createAvailabilityLabel("Deluxe Rooms", availableDeluxeRooms),
	    		    createAvailabilityLabel("Penthouses", availablePenthouseRooms)
	    		);
	    	
	    	vbox.setSpacing(10);
	    	dialog.getDialogPane().setContent(vbox); 
	    	dialog.getDialogPane().getButtonTypes().add(ButtonType.OK); 
	    	dialog.setWidth(400);
	    	
	    	dialog.showAndWait();
	    	
	    	return availableSingleRooms + availableDoubleRooms + availableDeluxeRooms + availablePenthouseRooms > 0;
    	}
    }
    
    private Label createAvailabilityLabel(String roomType, int count) {
        String text = count > 0 ? roomType + ": " + count : "No " + roomType + " available";
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px");
        return label;
    }

}


