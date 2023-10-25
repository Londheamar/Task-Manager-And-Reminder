package controllers;

public class UserSession {
    private int userId;
    private String username;

    private String name;
    private String email;

    // Constructor
    public UserSession(int userId, String username, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    
    public String getUserEmail() {
    	return this.email;
    }
    
    public String getUserName() {
    	return this.name;
    }
}
