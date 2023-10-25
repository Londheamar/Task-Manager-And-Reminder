package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.ConnectDb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.AlertUtils;
import utilities.FieldValidator;
import utilities.PasswordUtils;

public class Login {
	
	@FXML
	private Button loginBtn;
	
	@FXML
	private Button registerBtn;
	
	@FXML
	private TextField loginUsername;
	
	@FXML
	private PasswordField loginPassword;
	
	@FXML
	private void loginHandler(ActionEvent event) {
	    String username = loginUsername.getText();
	    String enteredPassword = loginPassword.getText();
	    int userId = 0;
	    
	    Node[] inputFields = {
	    		loginUsername,
	            loginPassword,
	        };

	     // Validate input fields and highlight empty ones
	        boolean hasEmptyFields = FieldValidator.validateAndHighlightEmptyFields(inputFields);

	        // If there are empty fields, display an alert or handle as needed
	        if (hasEmptyFields) {
	            AlertUtils.showErrorAlert("Validation Error", "Please fill in all required fields.");
	            return;
	        }

	    try (Connection conn = ConnectDb.connect()) {

	        // Query the database to retrieve the user's hashed password and salt
	        String getUserInfoQuery = "SELECT * FROM users WHERE username = ?";
	        
	        try (PreparedStatement getUserInfoStmt = conn.prepareStatement(getUserInfoQuery)) {
	            getUserInfoStmt.setString(1, username);
	            ResultSet resultSet = getUserInfoStmt.executeQuery();

	            if (resultSet.next()) {
	                userId = resultSet.getInt("id");
	                String storedPassword = resultSet.getString("password");

	                byte[] salt = resultSet.getBytes("salt");

	                // Verify the entered password against the stored password
	                if (PasswordUtils.verifyPassword(enteredPassword, storedPassword, salt)) {
	                	
	                	String name = resultSet.getString("fullName");
	                	String email = resultSet.getString("email");
	                	
	                    UserSession userSession = new UserSession(userId, username, name, email);
	                    
	                    SessionManager.setUserSession(userSession);
	                	AlertUtils.showInfoAlert("Success", "Login success");
	                	
	                	Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
	                    ViewController viewController = new ViewController(primaryStage);
	                    viewController.loadView("/views/MainView.fxml");
	                	

	                } else {
	                	AlertUtils.showErrorAlert("Auth error", "Username or password not correct, please check and try again.");
	                }
	            } else {
	            	AlertUtils.showErrorAlert("Not found", "Account not found.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	@FXML
	private void registerView(ActionEvent event) {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        ViewController viewController = new ViewController(primaryStage);
        viewController.loadView("/views/register.fxml");
	}
	
}