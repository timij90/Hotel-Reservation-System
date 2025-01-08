package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

import application.database.Database;

public class LoginController {
	
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    
	private Scene preScene;
    
    public void setPreScene(Scene preScene) {
		this.preScene = preScene;
	}
    
    public void initialize() {
        loginButton.setDisable(true); // Disabled by default

    	// Enable login button if text fields are not empty
        usernameField.textProperty().addListener((obs, oldText, newText) -> toggleLoginButton());
        passwordField.textProperty().addListener((obs, oldText, newText) -> toggleLoginButton());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = Database.connect()) {
            boolean isValid = Database.validateAdminCredentials(conn, username, password);
            if (isValid) {
                // Navigate to Admin Console
                navigateToAdminConsole();
            } else {
                showAlert("Invalid Login", "Incorrect username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onReturn(ActionEvent event) {
    	// Return back to Welcome page
    	Stage stage = (Stage) usernameField.getScene().getWindow();
    	stage.setScene(preScene);
    	stage.show();
    }
    
    private void navigateToAdminConsole() {
    	// Redirect to Admin Console
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/AdminConsole.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene adminScene = new Scene(loader.load());
            
            AdminController adminController = loader.getController();
            adminController.setPreScene(usernameField.getScene());
            
            stage.setScene(adminScene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void toggleLoginButton() {
        loginButton.setDisable(usernameField.getText().isEmpty() || passwordField.getText().isEmpty());
    }
}

