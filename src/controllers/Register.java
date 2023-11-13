package controllers;

import javafx.scene.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.ConnectDb;
import email.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.AlertUtils;
import utilities.PasswordUtils;
import utilities.Validate;

public class Register {
	
	@FXML
	private Button registerBtn;
	
	@FXML
	private TextField regUsername;
	
	@FXML
	private TextField fullName;
	
	@FXML
	private TextField emailId;
	
	@FXML
	private PasswordField regPassword;
	
	@FXML
	private void registerHandler(ActionEvent event) {
		
		String username = regUsername.getText();
		String password = regPassword.getText();
		String name = fullName.getText();
		String email = emailId.getText();
		
		Node[] inputFields = {
				regUsername,
				regPassword,
	            fullName,
	            emailId
	        };
		
		// Validate input fields and highlight empty ones
        boolean hasEmptyFields = Validate.validateAndHighlightEmptyFields(inputFields);
        if (hasEmptyFields) {
            AlertUtils.showErrorAlert("Validation Error", "Please fill in all required fields.");
            return;
        }
        
        // validate email
        boolean isEMailValid = Validate.validateEmail(email);
        if (!isEMailValid) {
            AlertUtils.showErrorAlert("Validation Error", "Enter valid email id.");
            return;
        }


        byte[] salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(password, salt);

        
        try (Connection conn = ConnectDb.connect()) {
        	
        	String checkUsernameQuery = "SELECT COUNT(*) FROM users WHERE username = ? or email=?";
        	boolean usernameExists = false;
        	
        	try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameQuery)) {
        	    checkUsernameStmt.setString(1, username);
        	    checkUsernameStmt.setString(2, email);
        	    ResultSet resultSet = checkUsernameStmt.executeQuery();

        	    if (resultSet.next()) {
        	        int count = resultSet.getInt(1);
        	        if (count > 0) {
        	            usernameExists = true;
        	        }
        	    }
        	}
        	
        	if(!usernameExists) {
            // Insert the task into the database
        	String sql = "INSERT INTO users (fullName, email, username, salt, password) VALUES (?, ?, ?, ?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, username);
                stmt.setBytes(4, salt);
                stmt.setString(5, hashedPassword);

                stmt.executeUpdate();
            }
            
            clearInputFields();
            // Send email

            EmailSender.sendEmail(email, "Registration success - [TaskManagerAndReminder]", "<h4>Dear Amar,</h4><p>Registration done</p>.");
            AlertUtils.showInfoAlert("Success", "Registration success, login to use account.");
            
            loginView();
        	}else {
                AlertUtils.showErrorAlert("Duplication Error", "Username / Email already exists, please use another username.");
        	}
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
    // Method to clear input fields after adding a task
    private void clearInputFields() {
        regUsername.clear();
        regPassword.clear();
        fullName.clear();
        emailId.clear();
        
    }
	
	@FXML
	private void loginView() {
        Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
        ViewController viewController = new ViewController(primaryStage);
        viewController.loadView("/views/login.fxml");
	}
	
}