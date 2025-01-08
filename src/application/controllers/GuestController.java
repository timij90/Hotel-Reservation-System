package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.sql.SQLException;

import application.database.GuestDatabase;
import application.database.ReservationDatabase;
import application.models.Guest;
import application.models.Reservation;

public class GuestController {

    @FXML private TextField addressField;
    @FXML private RadioButton customRadioButton;
    @FXML private TextField customTitleField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ToggleGroup titleGroup;
    @FXML private RadioButton mrRadioButton;
    @FXML private RadioButton msRadioButton;
    @FXML private TextField phoneField;
    @FXML private Button submitButton;
    
    private Reservation booking;
    
    public void setUpBooking(Reservation booking) {
    	this.booking = booking;
    }
    
    public void initialize() {
    	titleGroup = new ToggleGroup();
    	mrRadioButton.setToggleGroup(titleGroup);
    	msRadioButton.setToggleGroup(titleGroup);
    	customRadioButton.setToggleGroup(titleGroup);
    	
    	customRadioButton.setOnAction(e -> customTitleField.setDisable(false));
    	mrRadioButton.setOnAction(e -> customTitleField.setDisable(true));
    	msRadioButton.setOnAction(e -> customTitleField.setDisable(true));
    	
    	// Add a listener to validate email format
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("@") && newValue.contains(".com")) {
                // Valid email format
                emailField.setStyle("-fx-border-color: green;");
            } else {
                // Invalid email format
                emailField.setStyle("-fx-border-color: red;");
            }
        });
    	
    }

    @FXML
    void onSaveGuest(ActionEvent event) throws SQLException {
    	String title = getSelectedTitle();
    	String firstName = firstNameField.getText();
    	String lastName = lastNameField.getText();
    	String address = addressField.getText();
    	String phone = phoneField.getText();
    	String email = emailField.getText();
    	int guestID = idGen();
    	
    	if(title.isEmpty() || firstName.isEmpty() || lastName.isEmpty() 
    	   || address.isEmpty() || phone.isEmpty() || email.isEmpty()) 
    	{
    		showAlert("Save Error", "All fields are required");
    		return;
    	}
    	
    	if (!isValidEmail()) {
    		showAlert("Save Error", "Enter a valid email");
    		submitButton.requestFocus();
            return;
    	}
    	
    	Guest guest = new Guest(guestID, title, firstName, lastName, address, phone, email);
    	booking.setGuest(guest);
    	
    	ReservationDatabase.addReservation(booking);
    	GuestDatabase.addGuest(guest);
    	
    	showAlert("Success", "Guest saved successfully");
    	closeWindow();
    }
    
    private boolean isValidEmail() {
    	String style = emailField.getStyle();
    	return style.contains("green");
    }
    
    // Get selected payment frequency
    private String getSelectedTitle() {
        Toggle selectedToggle = titleGroup.getSelectedToggle();
        if (selectedToggle == null) {
        	return "No title selected";
        }
        
        String selectedText = ((RadioButton) selectedToggle).getText();
        if (selectedText.equals("Other")) {
            return customTitleField.getText();
        }
        return ((RadioButton) selectedToggle).getText();
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
    
    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
    
}
