package models;


import controllers.TasksController;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class TasksListCell extends ListCell<String> {
    private final HBox content;
    private final Text titleText;
    private final Text endDateText;
    private final Text statusText;
    private final Text priority = new Text();
    private final Button viewDetailsButton;
//    private final Region spacer;

    public TasksListCell(TasksController tasksController) {
        content = new HBox(10); // 10 is the spacing between elements
        titleText = new Text();
        endDateText = new Text();
        statusText = new Text();
        viewDetailsButton = new Button("View");
        viewDetailsButton.getStyleClass().add("btn-success"); // Add a CSS class to the button

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

//        Region spacer4 = new Region();
//        HBox.setHgrow(spacer4, Priority.ALWAYS);

        viewDetailsButton.setOnAction(event -> {
            String taskInfo = getItem();
            String[] taskData = taskInfo.split(";");
            if (taskData.length >= 3) {
                String taskId = taskData[0];
                
                Task tasks = TaskDAO.getSingleTask(taskId);
                
                tasksController.onViewDetailsClicked(tasks.toStringArray());
            }
        });

        content.getChildren().addAll(titleText, spacer1, endDateText, spacer2, statusText, spacer3, viewDetailsButton);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            String[] taskData = item.split(";");
            if (taskData.length >= 3) {
                titleText.setText(taskData[1]);
                endDateText.setText(taskData[2]);
                String priorityValue = taskData[3];
                
                priority.setText(priorityValue);
                // Apply CSS styles based on priority level
                if ("High".equalsIgnoreCase(priorityValue)) {
                    priority.getStyleClass().add("high-priority");
                } else if ("Medium".equalsIgnoreCase(priorityValue)) {
                    priority.getStyleClass().add("medium-priority");
                } else if ("Low".equalsIgnoreCase(priorityValue)) {
                    priority.getStyleClass().add("low-priority");
                } else {
                    priority.getStyleClass().add("default-priority");
                }
                
                String statusValue = taskData[4];
                
                statusText.setText(statusValue);
                
             // Apply CSS styles based on priority level
                if ("New".equalsIgnoreCase(statusValue)) {
                	statusText.getStyleClass().add("status-new");
                } else if ("Completed".equalsIgnoreCase(statusValue)) {
                	statusText.getStyleClass().add("status-completed");
                } else if ("In-Progress".equalsIgnoreCase(statusValue)) {
                	statusText.getStyleClass().add("status-progress");
                } else {
                	statusText.getStyleClass().add("default-priority");
                }
                
                
                setGraphic(content);
            }
        }
    }
}
