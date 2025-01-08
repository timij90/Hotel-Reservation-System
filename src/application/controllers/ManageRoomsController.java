package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import application.database.Database;

public class ManageRoomsController {
	
    @FXML private ComboBox<String> roomTypeComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private RadioButton addRadioButton;
    @FXML private RadioButton removeRadioButton;
    @FXML private ToggleGroup actionGroup;
	@FXML private TextField searchRoomIdField;
	@FXML private Label roomIdLabel;

    public void initialize() {
        // Populate room types in ComboBox
        roomTypeComboBox.getItems().addAll("Single", "Double", "Deluxe", "Penthouse");

        // Configure quantity spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // Group radio buttons
        actionGroup = new ToggleGroup();
        addRadioButton.setToggleGroup(actionGroup);
        removeRadioButton.setToggleGroup(actionGroup);
        
    	setupNumberField(searchRoomIdField);
        
        // Radio buttons actions
        addRadioButton.setOnAction(e -> {
        	roomIdLabel.setVisible(false);
        	searchRoomIdField.setVisible(false);
        	quantitySpinner.setDisable(false);
        });
    
        
        removeRadioButton.setOnAction(e -> {
        	roomIdLabel.setVisible(true);
        	searchRoomIdField.setVisible(true);        	
        	quantitySpinner.setDisable(true);
        });
    }

    @FXML
    private void handleSubmit() throws SQLException {
        String roomType = roomTypeComboBox.getValue();
        int quantity = quantitySpinner.getValue();
        boolean isAdding = addRadioButton.isSelected();

        if (roomType == null) {
            showAlert("Input Error", "Please select a room type.");
            return;
        }

        boolean success = false;
        try(Connection conn = Database.connect()){
        	if (isAdding) {
            	for(int i = 0; i < quantity; i++) {
            		success = Database.addRoom(conn, roomType);
            	}
            } else {
            	int roomId = Integer.parseInt(searchRoomIdField.getText());
                success = Database.removeRoom(conn, roomId);
            }
    	}
        
        if (success) {
            showInformation("Success", "The operation was successful!");
        } else {
            showAlert("Failure", "The operation was unsuccessful.");
        }
        clearFields();
    }
    
    @FXML
    void onCancel(ActionEvent event) {
    	if(showConfirmation("Cancel Operation", "Are you sure?")) { 
    		closeWindow();
            System.out.println("Cancelled the operation");
    	}
    	return;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
    private void showInformation(String title, String message) {
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
    
    private void setupNumberField(TextField field) {
    	field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                field.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    private void clearFields() {
    	roomTypeComboBox.getSelectionModel().clearSelection();;
        actionGroup.selectToggle(null);
        searchRoomIdField.clear();
        quantitySpinner.getValueFactory().setValue(1);
    }
    
    private void closeWindow() {
        Stage stage = (Stage) roomIdLabel.getScene().getWindow();
        stage.close();
    }
}
