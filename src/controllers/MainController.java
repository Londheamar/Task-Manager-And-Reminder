package controllers;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.ConnectDb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utilities.AlertUtils;
import utilities.FieldValidator;

public class MainController {

	@FXML
    private Text loggedUserName;
    
    @FXML
    private Text loggedUserFullName;
    
    @FXML
    private Text loggedUserEmail;
    
    @FXML
    private ImageView profileImage;
    
    @FXML
    private AnchorPane mainBody;
    
    @FXML
    private Button showExtraFieldsBtn;
    
    @FXML
    private Button logoutBtn;
    
    @FXML
    private VBox extraFieldsContainer;
    
    @FXML
    private Text textListAllTask;
    
    @FXML
    private SplitPane splitPaneShowList;
    
    @FXML
    private ComboBox<String> statusComboBox;
    
    @FXML
    private TextField taskNameTextField;

    @FXML
    private TextField taskDescriptionTextField;

    @FXML
    private DatePicker taskDatePickerStart;

    @FXML
    private DatePicker taskDatePickerEnd;

    @FXML
    private ComboBox<String> priorityComboBox;
    
    @FXML
    private ListView<String> taskListView;
    
    
    public void initialize() {
    	
        // Retrieve the user session information from SessionManager
        UserSession userSession = SessionManager.getUserSession();
        if (userSession != null) {
            loggedUserName.setText(userSession.getUsername());
            loggedUserEmail.setText(userSession.getUserEmail());
            loggedUserFullName.setText(userSession.getUserName());
        }
    	
    	// Load profile image 
    	Image image = new Image(getClass().getResource("/assets/default-avtar.png").toExternalForm());
    	// Image image = new Image("/assets/default-avtar.png");
        Circle clip = new Circle(profileImage.getFitWidth() / 2, profileImage.getFitHeight() / 2, profileImage.getFitWidth() / 2);
        profileImage.setClip(clip);
        profileImage.setImage(image);
        
    	//	Load dashboard on login
    	changeMainBodyContent("/views/dashboard.fxml");

    }

    @FXML
    private void toggleExtraFields() {
        // Toggle the visibility of the VBox containing extra fields
        boolean isVisible = extraFieldsContainer.isVisible();
        extraFieldsContainer.setVisible(!isVisible);
        extraFieldsContainer.setManaged(!isVisible);
     // Change the button text based on visibility
        if (!isVisible) {
        	showExtraFieldsBtn.setText("Hide More Fields");
        	showExtraFieldsBtn.getStyleClass().add("danger");
            textListAllTask.setLayoutY(167.0);
            splitPaneShowList.setLayoutY(180.0);
            AnchorPane.setTopAnchor(splitPaneShowList, 175.0);
         } else {
        	showExtraFieldsBtn.setText("More Fields");
        	showExtraFieldsBtn.getStyleClass().add("danger");
        	textListAllTask.setLayoutY(137.0);
        	splitPaneShowList.setLayoutY(150.0);
        	 AnchorPane.setTopAnchor(splitPaneShowList, 145.0);
        }
    }
    
    private void clearInputFields() {
        taskNameTextField.clear();
        taskDescriptionTextField.clear();
        taskDatePickerStart.setValue(null);
        taskDatePickerEnd.setValue(null);
        
        priorityComboBox.setPromptText("Priority");
        statusComboBox.setPromptText("Status");
        
    }

    
    @FXML
    private void addTask(ActionEvent event) {
        // Get user input from JavaFX UI components
        String title = taskNameTextField.getText();
        String description = taskDescriptionTextField.getText();
        String startDate = taskDatePickerStart.getValue() != null ? taskDatePickerStart.getValue().toString() : "";
        String endDate = taskDatePickerEnd.getValue() != null ? taskDatePickerEnd.getValue().toString() : "";
        String priority = priorityComboBox.getValue();
        String status = statusComboBox.getValue();
        
     // Create an array of input fields for validation
        Node[] inputFields = {
            taskNameTextField,
            taskDatePickerStart.getEditor(),
            taskDatePickerEnd.getEditor(),
            priorityComboBox,
            statusComboBox
        };

     // Validate input fields and highlight empty ones
        boolean hasEmptyFields = FieldValidator.validateAndHighlightEmptyFields(inputFields);

        // If there are empty fields, display an alert or handle as needed
        if (hasEmptyFields) {
            AlertUtils.showErrorAlert("Validation Error", "Please fill in all required fields.");
            return;
        }
        
        boolean isDateValid = FieldValidator.validateStartDateBeforeEndDate(taskDatePickerStart, taskDatePickerEnd);
        
        if(!isDateValid) {
            AlertUtils.showErrorAlert("Date Validation", "Start date must be before or equal to end date.");
            return;
        }

        // Insert data into database
        try (Connection conn = ConnectDb.connect()) {
        	UserSession userSession = SessionManager.getUserSession();
        	 String insertQuery = "INSERT INTO tasks (title, description, start_date, end_date, priority, status, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
             try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                 preparedStatement.setString(1, title);
                 preparedStatement.setString(2, description);
                 preparedStatement.setString(3, startDate);
                 preparedStatement.setString(4, endDate);
                 preparedStatement.setString(5, priority);
                 preparedStatement.setString(6, status);
                 preparedStatement.setLong(7, userSession.getUserId());

                 int rowsAffected = preparedStatement.executeUpdate();

                 if (rowsAffected > 0) {
                     // Data inserted successfully
                     AlertUtils.showSuccessAlert("Success", "Task added successfully.");
                     clearInputFields();
  
                 } else {
                     // Data insertion failed
                     AlertUtils.showErrorAlert("Error", "Failed to add task.");
                 }
             }
             
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Database Error", "An error occurred while adding the task.");
        }
    }
   
    
    @FXML
    private void viewChangerInMainBody(ActionEvent e){
    	Button clickedButton = (Button) e.getSource();
        String buttonId = clickedButton.getId();
                
        String[] parts = buttonId.split("_");
       	System.out.println(parts[1]);
        changeMainBodyContent("/views/"+parts[1]+".fxml");
    }
    
    @FXML
    private void logoutHandler(){
    	
    	boolean confirmed = AlertUtils.showConfirmAlert("Logout", "Really do you want to logout.", "Yes");
    	
    	if(confirmed) {
	    	Stage primaryStage = (Stage) logoutBtn.getScene().getWindow();
	        ViewController viewController = new ViewController(primaryStage);
	        viewController.loadView("/views/login.fxml");
    	}
    }
    
    public void changeMainBodyContent(String newFXMLFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(newFXMLFileName));
            Parent newContent = loader.load();
            mainBody.getChildren().setAll(newContent);
//            mainBody.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   

}