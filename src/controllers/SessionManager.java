package controllers;


public class SessionManager {
    private static UserSession userSession;

    public static void setUserSession(UserSession session) {
        userSession = session;
    }

    public static UserSession getUserSession() {
        return userSession;
    }
}
