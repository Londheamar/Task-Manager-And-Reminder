package models;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import controllers.SessionManager;
import controllers.UserSession;
import database.ConnectDb;

public class TaskDAO {

    public static List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        UserSession userSession = SessionManager.getUserSession();
        try (Connection conn = ConnectDb.connect()) {
            String sql = "SELECT * FROM tasks where user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userSession.getUserId());
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStartDate(rs.getString("start_date"));
                task.setEndDate(rs.getString("end_date"));
                task.setPriority(rs.getString("priority"));
                task.setStatus(rs.getString("status"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors or display an error message
        }

        return tasks;
    }
    
    
    public static Task getSingleTask(String task_id) {
        Task task = null;
        UserSession userSession = SessionManager.getUserSession();
        try (Connection conn = ConnectDb.connect()) {
            String sql = "SELECT * FROM tasks WHERE id = ? and user_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, task_id); // Set the task_id as a parameter
            stmt.setLong(2, userSession.getUserId()); // Set the task_id as a parameter

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStartDate(rs.getString("start_date"));
                task.setEndDate(rs.getString("end_date"));
                task.setPriority(rs.getString("priority"));
                task.setStatus(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors or display an error message
        }

        return task;
    }
}
