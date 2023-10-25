module TaskManagerAndReminder {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires javafx.base;
	requires java.base;
	requires javafx.graphics;
	
	exports controllers;
	
	opens application to javafx.graphics, javafx.fxml;
	opens controllers to javafx.fxml;
}
