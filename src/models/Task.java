package models;

public class Task {
    private int task_id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String priority;
    private String status;

    // Constructors
    public Task() {
        // Default constructor
    }

    public Task(String title, String description, String startDate, String endDate, String priority, String status) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.status = status;
    }

    // Getters and setters
    public int getId() {
        return task_id;
    }

    public void setId(int id) {
        this.task_id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Other methods (if needed)
    
    public String[] toStringArray() {
        String[] taskDetails = new String[7];
        taskDetails[0] = String.valueOf(task_id);
        taskDetails[1] = title;
        taskDetails[2] = description;
        taskDetails[3] = startDate;
        taskDetails[4] = endDate;
        taskDetails[5] = priority;
        taskDetails[6] = status;
        return taskDetails;
    }
}
