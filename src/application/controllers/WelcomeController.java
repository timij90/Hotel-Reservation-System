package application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class WelcomeController {

    @FXML
    private Button adminLoginButton;

    @FXML
    private Button startBookingButton;

    // Single handler method for both buttons
    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            Scene newScene = null;
            
            // Check which button was clicked and load the appropriate screen
            if (event.getSource() == adminLoginButton) {
            	navigateToLoginScreen(newScene);            
            } else if (event.getSource() == startBookingButton) {
            	navigateToBookingScreen(newScene);            
            } else {
                return; // Exit if no recognized button was clicked
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void navigateToLoginScreen(Scene loginScene) {
    	// Redirect to Admin Console
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/LoginScreen.fxml"));
            Stage stage = (Stage) adminLoginButton.getScene().getWindow();
            loginScene = new Scene(loader.load());
            
            LoginController loginController = loader.getController();
            loginController.setPreScene(adminLoginButton.getScene());
            
            stage.setScene(loginScene);
            stage.show();
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void navigateToBookingScreen(Scene bookingScene) {
    	// Redirect to Admin Console
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/BookingScreen.fxml"));
            Stage stage = new Stage();
            bookingScene = new Scene(loader.load());
            
            stage.setScene(bookingScene);
            stage.show();
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
