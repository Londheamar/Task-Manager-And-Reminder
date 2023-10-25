package utilities;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class FieldValidator {
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
    
    public static boolean validateStartDateBeforeEndDate(DatePicker startDatePicker, DatePicker endDatePicker) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            highlightEmptyField(startDatePicker.getEditor());
            highlightEmptyField(endDatePicker.getEditor());
            return false;
        } else {
            removeHighlight(startDatePicker.getEditor());
            removeHighlight(endDatePicker.getEditor());
            return true;
        }
    }
    

    private static void highlightEmptyField(Node field) {
        field.getStyleClass().add("highlight-empty-field");
    }

    private static void removeHighlight(Node field) {
        field.getStyleClass().remove("highlight-empty-field");
    }
}
