package utilities;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Validate {
	
    public static boolean validateAndHighlightEmptyFields(Node[] fields) {
        boolean hasEmptyFields = false;

        for (Node field : fields) {
            if (field instanceof TextField) {
                String text = ((TextField) field).getText().trim();
                if (text.isEmpty()) {
                    highlightEmptyField(field);
                    hasEmptyFields = true;
                } else {
                    removeHighlight(field);
                }
            }else if (field instanceof PasswordField) {
                String text = ((PasswordField) field).getText().trim();
                if (text.isEmpty()) {
                    highlightEmptyField(field);
                    hasEmptyFields = true;
                } else {
                    removeHighlight(field);
                }
            } else if (field instanceof DatePicker) {
                String text = ((DatePicker) field).getEditor().getText().trim();
                if (text.isEmpty()) {
                    highlightEmptyField(((DatePicker) field).getEditor());
                    hasEmptyFields = true;
                } else {
                    removeHighlight(((DatePicker) field).getEditor());
                }
            } else if (field instanceof ComboBox) {
                if (((ComboBox<?>) field).getValue() == null) {
                    highlightEmptyField(field);
                    hasEmptyFields = true;
                } else {
                    removeHighlight(field);
                }
            }
        }

        return hasEmptyFields;
    }
    
    public static boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private static void highlightEmptyField(Node field) {
        field.getStyleClass().add("highlight-empty-field");
    }

    private static void removeHighlight(Node field) {
        field.getStyleClass().remove("highlight-empty-field");
    }
}
