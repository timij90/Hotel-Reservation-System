package application.controllers;

import java.sql.SQLException;

import application.database.ReservationDatabase;
import application.models.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.models.Reservation;

public class CancelBookingController {

    @FXML private TextField bookingIdField;
    @FXML private TextArea cancellationReasonField;
    @FXML private TextField guestNameField;
    @FXML private TextField phoneSearchField;
	@FXML private Button searchButton;
    
    private Reservation foundReservation;
    private Scene scene;
    private String title;
    
    public void setPreScene(Scene scene, String title) {
		this.scene = scene;
		this.title = title;
	}
    
    public void initialize() {
    	bookingIdField.setDisable(true);
        guestNameField.setDisable(true);
        
        searchButton.setDisable(true); // Disabled by default
		
		// Enable search button if text field is not empty
		phoneSearchField.textProperty().addListener((obv, oldVal, newVal) -> toggleSearchButton());
    }

    @FXML
    void onBack(ActionEvent event) {
    	// Return to the admin screen
    	Stage stage = (Stage) phoneSearchField.getScene().getWindow();
    	stage.setScene(scene);
    	stage.setTitle(title);
    	stage.show();
    }

    @FXML
    void onCancelBooking(ActionEvent event) {
    	try {
	        if (foundReservation == null) {
	            showAlert("Error", "No booking found to cancel.");
	            return;
	        }

	        if(showConfirmation("Cancel Reservation", "Are you sure?")) {
		        ReservationDatabase.removeReservation(foundReservation);
		        
		        showAlert("Success", "Booking canceled successfully.");
		        clearFields();
		        
		        foundReservation = null; // Reset booking reference
	    	}
	    	return;

	    } catch (Exception e) {
	        showAlert("Error", "An error occurred while canceling the booking.");
	        e.printStackTrace();
	    }

    }

    @FXML
    void onSearch(ActionEvent event) throws SQLException {
    	String phone = phoneSearchField.getText().trim();
    	foundReservation  = ReservationDatabase.searchBookingByPhone(phone);
    	
    	if (foundReservation != null) {
    	    System.out.println("Reservation found: " + foundReservation.getBookingId());
    	    
    	    Guest foundGuest = foundReservation.getGuest();
    	    
    	    bookingIdField.setText(String.valueOf(foundReservation.getBookingId()));
    	    guestNameField.setText(foundGuest.getFirstName() + " " + foundGuest.getLastName());
    	    
    	} else {
    	    showAlert("Search Error", "No reservation found for phone: " + phone);
    	    return;
    	}

    }
    
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
    private void toggleSearchButton() {
        searchButton.setDisable(phoneSearchField.getText().isEmpty());
    }
    
    private void clearFields() {
    	phoneSearchField.clear();
        bookingIdField.clear();
        guestNameField.clear();
    }

}
