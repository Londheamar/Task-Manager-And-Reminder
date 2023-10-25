package utilities;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

public class AlertUtils {

    public static void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String content) {
        showAlert(AlertType.ERROR, title, content);
    }

    public static void showInfoAlert(String title, String content) {
        showAlert(AlertType.INFORMATION, title, content);
    }
    
    public static void showSuccessAlert(String title, String content) {
        showAlert(AlertType.WARNING, title, content);
    }
    
    public static boolean showConfirmAlert(String title, String content, String btnText) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Customize the OK button text to "Confirm"
        ButtonType confirmButton = new ButtonType(btnText, ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(confirmButton, ButtonType.CANCEL);

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // Check the user's response and return true for Confirm, false for Cancel
        return result.isPresent() && result.get() == confirmButton;
    }
}
