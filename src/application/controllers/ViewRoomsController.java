package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

import application.models.*;

public class ViewRoomsController {
	
	@FXML private TableView<Room> availableRoomsTable;
    @FXML private TableColumn<Room, Integer> roomIdCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> roomRateCol;
    @FXML private TableColumn<Room, String> roomStatusCol;
    @FXML private Label numRoomsLabel;
        
    public void initialize() {
    	// Set up table columns
        setUpColumns();        
    }
    
    private void setUpColumns() {
        roomIdCol.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        roomRateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));
    }
    
    public void populateAvailableRooms(List<Room> availableRooms) {
        availableRoomsTable.getItems().clear(); // Clear current entries
        availableRoomsTable.getItems().addAll(availableRooms); // Add updated list
        numRoomsLabel.setText("Number of Rooms: " + availableRooms.size());
    }

}
