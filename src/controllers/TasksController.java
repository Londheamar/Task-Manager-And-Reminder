package controllers;

import java.time.LocalDate;
import java.util.List;

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
    	System.out.println(taskName);
    }
   
}