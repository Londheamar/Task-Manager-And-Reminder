package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import database.ConnectDb;
import email.EmailSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import models.Task;
import models.TaskDAO;
import models.TasksListCell;
import utilities.AlertUtils;
import utilities.FieldValidator;

public class TasksController {
	
	private String taskId;
	
	@FXML
	private AnchorPane taskDetailsView;
	
	@FXML
    private ListView<String> taskListView;
	
	@FXML
	private TextField taskName;
	
	@FXML
	private TextField taskDescription;
	
	@FXML
	private DatePicker taskEndDate;
	
	@FXML
	private DatePicker taskStartDate;
	
    @FXML
    private ComboBox<String> taskPriority;
    
    @FXML
    private ComboBox<String> taskStatus;
	
	
	
	public void initialize() {
		listAllTasks();
	}
	
    private void listAllTasks() {
   	 // Initialize the ListView and populate it with task titles
       ObservableList<String> taskTitles = FXCollections.observableArrayList();
       List<Task> tasks = TaskDAO.getAllTasks();
       for (Task task : tasks) {
       	taskTitles.add(task.getId()+ ";" + task.getTitle() + ";" + task.getEndDate()+";" + task.getPriority()+";" + task.getStatus());
       }

       taskListView.setItems(taskTitles);
       // Set the custom cell factory for the ListView
//       TasksController tasksController = new TasksController();
//       taskListView.setCellFactory(listView -> new TasksListCell(tasksController));
       taskListView.setCellFactory(listView -> new TasksListCell(this));
   }
    

    public void onViewDetailsClicked(String[] taskDetails) {
        // Extract task details from the array
        String taskId = taskDetails[0];
        String title = taskDetails[1];
        String description = taskDetails[2];
        String priority = taskDetails[5];
        String status = taskDetails[6];
        	
        LocalDate endDate = LocalDate.parse(taskDetails[4]);
        LocalDate startDate = LocalDate.parse(taskDetails[3]);

        this.taskId = taskId;
        
        taskName.setText(title);
        taskDescription.setText(description);
        taskEndDate.setValue(endDate);
        taskStartDate.setValue(startDate);
        
        taskStatus.setValue(status);
        taskPriority.setValue(priority);
        
        taskDetailsView.getStyleClass().add("taskDetailsView-active");
    }

    
    @FXML
    private void updateTask() {
    	System.out.println(this.taskId);
    	String endDate = taskEndDate.getValue() != null ? taskEndDate.getValue().toString() : "";
		String priority = taskPriority.getValue();
		String status = taskStatus.getValue();
		
		boolean isDateValid = FieldValidator.validateStartDateBeforeEndDate(taskStartDate, taskEndDate);
		if (!isDateValid) {
			AlertUtils.showErrorAlert("Date Validation", "Start date must be before or equal to end date.");
			return;
		}
		
		boolean confirmed = AlertUtils.showConfirmAlert("New task",
				"Please confirm that all the provided information is accurate.", "Confirm");
		
		if (confirmed) {
			try (Connection conn = ConnectDb.connect()) {
				UserSession userSession = SessionManager.getUserSession();
				String insertQuery = "UPDATE tasks SET"
						+ "end_date = ?, priority = ?, status = ?)"
						+ "WHERE user_id = ? and id = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
					preparedStatement.setString(1, endDate);
					preparedStatement.setString(2, priority);
					preparedStatement.setString(3, status);
					preparedStatement.setLong(4, userSession.getUserId());
					preparedStatement.setString(5, this.taskId);
					
					int rowsAffected = preparedStatement.executeUpdate();

					if (rowsAffected > 0) {
						// Data inserted successfully
						String subject = "Task updated - [TaskManagerAndReminder]";
						String body = "<h4>Dear "+userSession.getUserName()+",</h4><p>Task Updated.</p>.";
						
						if(status == "Completed") {
							subject = "Task Colsed and completed - [TaskManagerAndReminder]";
							body = "<h4>Dear "+userSession.getUserName()+",</h4><p>Task Updated and marked as completed & closed.</p>.";
						}
						
			            EmailSender.sendEmail(userSession.getUserEmail(), subject, body );

						AlertUtils.showSuccessAlert("Success", "Task added successfully.");

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
   
}