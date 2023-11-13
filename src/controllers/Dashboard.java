package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import database.ConnectDb;
import email.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;
import utilities.AlertUtils;
import utilities.FieldValidator;

public class Dashboard {

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
	private CheckBox remindPlatformEmail;

	@FXML
	private CheckBox remindPlatformSMS;

	@FXML
	private CheckBox remindPlatformPN;

	@FXML
	private CheckBox reminderOne;

	@FXML
	private CheckBox reminderTwo;

	@FXML
	private CheckBox reminderThree;

	@FXML
	private CheckBox reminderFour;
	
	@FXML
	private Text completedTasksCount;
	
	@FXML
	private Text pendingTasksCount;
	
	@FXML
	private Text newTasksCount;
	
	@FXML
	private Text totalTasksCount;
	
	@FXML
	private ImageView completedTasksIcon;
	
	@FXML
	private ImageView pendingTasksIcon;
	
	@FXML
	private ImageView newTasksIcon;
	
	@FXML
	private ImageView totalTasksIcon;

	public void initialize() {
		        
		initializeState();
		statusComboBox.setValue("New");

		// Create a custom DateCell factory
		Callback<DatePicker, DateCell> dayCellFactory = datePicker -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);

				// Disable past dates (before today)
				if (item.isBefore(LocalDate.now())) {
					setDisable(true);
					setStyle("-fx-background-color: #ffc0cb;"); // Change the style of disabled dates
				}
			}
		};

		// Set the custom DateCell factory to the DatePicker
		taskDatePickerStart.setDayCellFactory(dayCellFactory);

	}

    private void clearInputFields() {
        taskNameTextField.clear();
        taskDescriptionTextField.clear();
        taskDatePickerStart.setValue(null);
        taskDatePickerEnd.setValue(null);
        
        priorityComboBox.setPromptText("Priority");
        
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

		Boolean emailPlatform = remindPlatformEmail.isSelected();
		Boolean smsPlatform = remindPlatformSMS.isSelected();
		Boolean pushNPlatform = remindPlatformPN.isSelected();

		Boolean reminder1 = reminderOne.isSelected();
		Boolean reminder2 = reminderTwo.isSelected();
		Boolean reminder3 = reminderThree.isSelected();
		Boolean reminder4 = reminderFour.isSelected();

		// Create an array of input fields for validation
		Node[] inputFields = { taskNameTextField, taskDatePickerStart.getEditor(), taskDatePickerEnd.getEditor(),
				priorityComboBox, statusComboBox };

		// Validate input fields and highlight empty ones
		boolean hasEmptyFields = FieldValidator.validateAndHighlightEmptyFields(inputFields);

		// If there are empty fields, display an alert or handle as needed
		if (hasEmptyFields) {
			AlertUtils.showErrorAlert("Validation Error", "Please fill in all required fields.");
			return;
		}

		boolean isDateValid = FieldValidator.validateStartDateBeforeEndDate(taskDatePickerStart, taskDatePickerEnd);

		if (!isDateValid) {
			AlertUtils.showErrorAlert("Date Validation", "Start date must be before or equal to end date.");
			return;
		}

		// Insert data into database
		boolean confirmed = AlertUtils.showConfirmAlert("New task",
				"Please confirm that all the provided information is accurate.", "Confirm");

		if (confirmed) {
			try (Connection conn = ConnectDb.connect()) {
				UserSession userSession = SessionManager.getUserSession();
				String insertQuery = "INSERT INTO tasks "
						+ "(title, description, start_date, end_date, priority, status, user_id, email, sms, pushn, remind_1, remind_2, remind_3, remind_4) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
					preparedStatement.setString(1, title);
					preparedStatement.setString(2, description);
					preparedStatement.setString(3, startDate);
					preparedStatement.setString(4, endDate);
					preparedStatement.setString(5, priority);
					preparedStatement.setString(6, status);
					preparedStatement.setLong(7, userSession.getUserId());
					preparedStatement.setBoolean(8, emailPlatform);
					preparedStatement.setBoolean(9, smsPlatform);
					preparedStatement.setBoolean(10, pushNPlatform);
					preparedStatement.setBoolean(11, reminder1);
					preparedStatement.setBoolean(12, reminder2);
					preparedStatement.setBoolean(13, reminder3);
					preparedStatement.setBoolean(14, reminder4);

					int rowsAffected = preparedStatement.executeUpdate();

					if (rowsAffected > 0) {
						// Data inserted successfully
			            EmailSender.sendEmail(userSession.getUserEmail(), "New task - [TaskManagerAndReminder]", "<h4>Dear Amar,</h4><p>New task scheduled.</p>.");

						AlertUtils.showSuccessAlert("Success", "Task added successfully.");
                         clearInputFields();
                         initializeState();

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

	}
	
	private void initializeState() {
		UserSession userSession = SessionManager.getUserSession();
		
		
		String query1 = "SELECT COUNT(*) FROM tasks where status = 'Completed' AND user_id = " + userSession.getUserId();
        int totalCount1 = fetchDataAndReturnTotalCount(query1);
        completedTasksCount.setText(""+totalCount1);
        
    	Image image1 = new Image(getClass().getResource("/assets/task-completed-1.png").toExternalForm());
        Circle clip1 = new Circle(completedTasksIcon.getFitWidth() / 2, completedTasksIcon.getFitHeight() / 2, completedTasksIcon.getFitWidth() / 2);
        completedTasksIcon.setClip(clip1);
        completedTasksIcon.setImage(image1);
        
        String query2 = "SELECT COUNT(*) FROM tasks where status = 'In-Progress' AND user_id = " + userSession.getUserId();
        int totalCount2 = fetchDataAndReturnTotalCount(query2);
        pendingTasksCount.setText(""+totalCount2);
        
        Image image2 = new Image(getClass().getResource("/assets/task-pending.png").toExternalForm());
        Circle clip2 = new Circle(pendingTasksIcon.getFitWidth() / 2, pendingTasksIcon.getFitHeight() / 2, pendingTasksIcon.getFitWidth() / 2);
        pendingTasksIcon.setClip(clip2);
        pendingTasksIcon.setImage(image2);
        
        String query3 = "SELECT COUNT(*) FROM tasks where status = 'New' AND user_id = " + userSession.getUserId();
        int totalCount3 = fetchDataAndReturnTotalCount(query3);
        newTasksCount.setText(""+totalCount3);
        
        Image image3 = new Image(getClass().getResource("/assets/task-new.png").toExternalForm());
        Circle clip3 = new Circle(newTasksIcon.getFitWidth() / 2, newTasksIcon.getFitHeight() / 2, newTasksIcon.getFitWidth() / 2);
        newTasksIcon.setClip(clip3);
        newTasksIcon.setImage(image3);
        
        String query4 = "SELECT COUNT(*) FROM tasks where user_id = " + userSession.getUserId();
        int totalCount4 = fetchDataAndReturnTotalCount(query4);
        totalTasksCount.setText(""+totalCount4);
        
        Image image4 = new Image(getClass().getResource("/assets/task-new.png").toExternalForm());
        Circle clip4 = new Circle(totalTasksIcon.getFitWidth() / 2, totalTasksIcon.getFitHeight() / 2, totalTasksIcon.getFitWidth() / 2);
        totalTasksIcon.setClip(clip4);
        totalTasksIcon.setImage(image4);
	}

	private int fetchDataAndReturnTotalCount(String query) {
		// Establish a database connection using ConnectDb
		try (Connection conn = ConnectDb.connect()) {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt(1);
			}

			// Close database resources
			resultSet.close();
			preparedStatement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

}