package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

import application.database.Database;
import application.database.ReservationDatabase;
import application.models.*;

public class BillController {
	
    @FXML private TextField phoneSearchField;
    @FXML private TextField bookingIdField;
    @FXML private TextField guestNameField;
    @FXML private TextField numRoomsField;
    @FXML private TextField ratesPerNightField;
    @FXML private TextArea roomTypeField;
    @FXML private Label totalCostLabel;
    @FXML private ToggleGroup discountGroup;	
    @FXML private RadioButton rate1;
    @FXML private RadioButton rate2;
    @FXML private RadioButton rate3;
    @FXML private RadioButton rate4;
    @FXML private RadioButton rate5;
	@FXML private Button searchButton;
    
    private Bill bill;
    private Reservation foundReservation;
    
    
    public void initialize() {
    	discountGroup = new ToggleGroup();
    	rate1.setToggleGroup(discountGroup);
    	rate2.setToggleGroup(discountGroup);
    	rate3.setToggleGroup(discountGroup);
    	rate4.setToggleGroup(discountGroup);
    	rate5.setToggleGroup(discountGroup);
    	
        bookingIdField.setDisable(true);
        guestNameField.setDisable(true);
        numRoomsField.setDisable(true);
        ratesPerNightField.setDisable(true);
        // roomTypeField.setDisable(true);
        
        searchButton.setDisable(true); // Disabled by default
		
		// Enable search button if text field is not empty
		phoneSearchField.textProperty().addListener((obv, oldVal, newVal) -> toggleSearchButton());
    }

	@FXML
    void onCalculate(ActionEvent event) {
    	// Display error message if calculate button pressed with empty fields
		displayMessage(foundReservation);
		
    	double selectedDiscount = getSelectedDiscountRate();
    	
    	bill = new Bill(idGen(), foundReservation);
    	double discountedCost = bill.applyDiscount(selectedDiscount);
        String formattedPayment = NumberFormat.getCurrencyInstance(Locale.US).format(discountedCost);

    	totalCostLabel.setText("Total Amount: " + formattedPayment);

    }

    @FXML
    void onCancel(ActionEvent event) {
    	if(showConfirmation("Cancel Bill", "Are you sure?")) { 
    		closeWindow();
            System.out.println("Cancelled bill creation");
    	}
    	return;
    }

    @FXML
    void onSave(ActionEvent event) throws SQLException {
    	// Display error message if save button pressed with empty fields
		displayMessage(foundReservation, bill);

		Database.addBill(bill, foundReservation, getSelectedDiscountRate());
		showAlert("Success", "Bill saved successfully");
		
		clearFields();
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
    	    numRoomsField.setText(String.valueOf(foundReservation.getNumOfRooms()));
    	    roomTypeField.setText(foundReservation.getRoomType());
    	    ratesPerNightField.setText(String.valueOf(foundReservation.getRates()));
    	} else {
    	    showAlert("Search Error", "No reservation found for phone: " + phone);
    	    return;
    	}

    }
    
    private double getSelectedDiscountRate() {
        Toggle selectedToggle = discountGroup.getSelectedToggle();
        if (selectedToggle == null) {
            return 0;  // Default interest rate if none selected
        }

        String selectedText = ((RadioButton) selectedToggle).getText();
        return Double.parseDouble(selectedText.replace("%", ""));
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }
    
    private int idGen() {
        int randomPart = (int) (Math.random() * 1000); // random value between 0 and 999
        int id = (int) (System.currentTimeMillis() % 100000) + randomPart;
        return id;
    }
    
    private void closeWindow() {
        Stage stage = (Stage) phoneSearchField.getScene().getWindow();
        stage.close();
    }
    
    private void clearFields() {
    	phoneSearchField.clear();
        bookingIdField.clear();
        guestNameField.clear();
        numRoomsField.clear();
        ratesPerNightField.clear();
        roomTypeField.clear();
        totalCostLabel.setText("Total Amount:");
        discountGroup.selectToggle(null);
    }
    
    private void toggleSearchButton() {
        searchButton.setDisable(phoneSearchField.getText().isEmpty());
    }
    
    private void displayMessage(Reservation obj) {
    	if(obj == null) {
    		showAlert("Error", "No reservation to bill. Search for the phone.");
    		return;
    	}		
	}
    
    private void displayMessage(Reservation obj1, Bill obj2) {
    	if(obj1 == null || obj2 == null) {
    		showAlert("Error", "Calculate the bill amount.");
    		return;
    	}	
	}

}
