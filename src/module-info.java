module Project_HotelReservation {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
	opens application.controllers to javafx.fxml;
	opens application.models to javafx.base;
}
