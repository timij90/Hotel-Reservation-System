/**********************************************
Final Project
Course:APD545 - Fall
Last Name:Jacobs
First Name:Oluwatimilehin
ID:148981228
Section:ZAA
This assignment represents my own work in accordance with Seneca Academic Policy.
Signature
Date:06/10/2024
**********************************************/

package application;
	
import java.sql.Connection;
import java.sql.SQLException;

import application.database.Database;
import application.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			 // Initialize database
	        try (Connection conn = Database.connect()) {
	            DatabaseInitializer.initializeDatabase(conn);
	            DatabaseInitializer.insertData(conn);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        Font.loadFont(getClass().getResource("fonts/YesevaOne-Regular.ttf").toExternalForm(), 40);
	        
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("views/WelcomeScreen.fxml"));
			Scene scene = new Scene(root);
	        primaryStage.setTitle("Hotel Reservation System");
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setMaximized(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
