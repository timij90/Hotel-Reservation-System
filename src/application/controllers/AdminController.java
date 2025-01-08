package application.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import application.models.Room;
import application.database.Database;
import application.database.GuestDatabase;
import application.models.Guest;

public class AdminController {
	
	
	@FXML private TextField phoneSearchField;
	@FXML private TextArea guestInformation;
	@FXML private Button searchButton;
	
	private Scene preScene;
	private final String TITLE = "Admin Dashboard";
	
	public void setPreScene(Scene preScene) {
		this.preScene = preScene;
	}
	
	public void initialize() {
		searchButton.setDisable(true); // Disabled by default
		
		// Enable search button if text field is not empty
		phoneSearchField.textProperty().addListener((obv, oldVal, newVal) -> toggleSearchButton());
		
		guestInformation = new TextArea();
	}
	
	@FXML
    private void bookRoom() throws IOException {
        // Load booking page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/BookingScreen.fxml"));
        Stage stage = new Stage();
        Scene newScene = new Scene(loader.load());
        
        stage.setScene(newScene);
        stage.setTitle("Booking Page");
        stage.show();
        stage.setMaximized(true);
    }
	
	@FXML
    private void billService() throws IOException {
        // Load billing page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/BillScreen.fxml"));
        Stage stage = new Stage();
        Scene newScene = new Scene(loader.load());
        
        stage.setScene(newScene);
        stage.setTitle("Billing Page");
        stage.show();
        stage.setMaximized(true);
    }

    @FXML
    private void viewCurrentBookings() throws IOException {
        // Load bookings from database and display
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/ViewBookingsScreen.fxml"));
        Stage stage = new Stage();
        Scene newScene = new Scene(loader.load());
        
        stage.setScene(newScene);
        stage.setTitle("Current bookings Page");
        stage.show();
        stage.setMaximized(true);
    }
    
    @FXML
    private void viewAvailableRooms() throws IOException{
    	List<Room> availableRooms = new ArrayList<>();
    	
        // Load rooms from database and display
    	try(Connection conn = Database.connect()){
            availableRooms = Database.getAllAvailableRooms(conn);
    	} catch (SQLException e) {
			e.printStackTrace();
		}
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/ViewRoomsScreen.fxml"));
        Stage stage = new Stage();
        Scene newScene = new Scene(loader.load());
        
        ViewRoomsController viewRoomsController = loader.getController();
        viewRoomsController.populateAvailableRooms(availableRooms);
        
        stage.setScene(newScene);
        stage.setTitle("Available Rooms");
        stage.show();
        stage.setMaximized(true);
    }

    @FXML
    private void searchGuest() throws SQLException {
        // Search guest in database
    	String searchedPhone = phoneSearchField.getText();
    	Guest foundGuest = GuestDatabase.searchGuestByPhone(searchedPhone);
    	
    	if(foundGuest != null) {
	    	guestInformation.setText(foundGuest.toString());
	    	guestInformation.setStyle("-fx-font-size: 20px;");
	    	guestInformation.setEditable(false);
	    	
	    	Stage guestInfoStage = new Stage();
	    	Scene newScene = new Scene(guestInformation, 400, 300);
	    	guestInfoStage.setScene(newScene);
	    	guestInfoStage.setTitle("Guest Information");
	    	guestInfoStage.show();
    	}else {
    		showAlert("Search Error", "No guest with phone number: " + searchedPhone);
    		return;
    	}
    	
    }
    
    @FXML
    private void cancelBooking() throws IOException {
        // Load booking cancellation page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/BookingCancellation.fxml"));
        Stage stage = (Stage) phoneSearchField.getScene().getWindow();
        Scene newScene = new Scene(loader.load());
        
        CancelBookingController cancelController = loader.getController();
        cancelController.setPreScene(phoneSearchField.getScene(), TITLE);
        
        stage.setScene(newScene);
        stage.setTitle("Booking Cancellation Page");
        stage.show();
        stage.setMaximized(true);
    }
    
    @FXML
    private void handleAdditionOrRemoval() throws IOException{
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/ManageRoomsScreen.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        
        stage.setScene(scene);
        stage.setTitle("Manage Room Availability");
        stage.show();
        stage.setMaximized(true);
    }

    @FXML
    private void logout() throws IOException{
        // Return to the login screen
    	Stage stage = (Stage) phoneSearchField.getScene().getWindow();
    	stage.setScene(preScene);
    	stage.show();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
    private void toggleSearchButton() {
        searchButton.setDisable(phoneSearchField.getText().isEmpty());
    }
}
