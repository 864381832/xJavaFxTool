package com.xwintop.xcore.javafx.dialog;

import com.xwintop.xcore.javafx.FxApp;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;

/**
 * 系统对话框封装
 */
public class FxAlerts {

    public static void info(String title, String message) {
        alert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void warn(String title, String message) {
        alert(Alert.AlertType.WARNING, title, message);
    }

    public static void error(String message) {
        alert(Alert.AlertType.ERROR, "错误", message);
    }

    public static void error(String title, String message) {
        alert(Alert.AlertType.ERROR, title, message);
    }

    public static void error(Window owner, String title, String message) {
        alert(owner, Alert.AlertType.ERROR, title, message);
    }

    public static void error(String title, Throwable throwable) {
        boolean noMessage = StringUtils.isBlank(throwable.getMessage());
        String message = noMessage ? throwable.toString() : throwable.getMessage();
        error(title, message, ExceptionUtils.getStackTrace(throwable));
    }

    public static void error(Window owner, String title, Throwable throwable) {
        boolean noMessage = StringUtils.isBlank(throwable.getMessage());
        String message = noMessage ? throwable.toString() : throwable.getMessage();
        error(owner, title, message, ExceptionUtils.getStackTrace(throwable));
    }

    public static void alert(Alert.AlertType alertType, String title, String message) {
        alert(null, alertType, title, message);
    }

    public static void alert(Window owner, Alert.AlertType alertType, String title, String message) {
        FxApp.runLater(() -> {
            try {
                Alert alert = new Alert(alertType, message, ButtonType.OK);
                alert.setTitle(title);
                alert.setHeaderText(null);

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                FxApp.setupIcon(stage);

                if (owner != null) {
                    stage.initOwner(owner);
                } else {
                    FxApp.setupModality(alert);
                }

                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 打开一个展示了详细错误信息的错误对话框
    public static void error(String title, String message, String details) {
        FxApp.runLater(() -> error0(null, title, message, details));
    }

    // 打开一个展示了详细错误信息的错误对话框
    public static void error(Window owner, String title, String message, String details) {
        FxApp.runLater(() -> error0(owner, title, message, details));
    }

    private static void error0(Window owner, String title, String message, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message.trim());

        TextArea textArea = new TextArea(details);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        FxApp.setupIcon(stage);

        if (owner != null) {
            stage.initOwner(owner);
        } else {
            FxApp.setupModality(alert);
        }

        alert.showAndWait();
    }

    //////////////////////////////////////////////////////////////

    public static boolean confirmOkCancel(String title, String message) {
        return confirm(Alert.AlertType.CONFIRMATION, title, message, ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK;
    }

    public static boolean confirmYesNo(String title, String message) {
        return confirm(Alert.AlertType.WARNING, title, message, ButtonType.YES, ButtonType.NO) == ButtonType.YES;
    }

    public static ButtonType confirmYesNoCancel(String title, String message) {
        return confirm(Alert.AlertType.WARNING, title, message, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }

    public static ButtonType confirm(Alert.AlertType alertType, String title, String message, ButtonType... buttonTypes) {
        try {
            Alert alert = new Alert(alertType, message, buttonTypes);
            alert.setTitle(title);
            alert.setHeaderText(null);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            FxApp.setupIcon(stage);
            FxApp.setupModality(alert);

            Optional<ButtonType> result = alert.showAndWait();
            return result.orElse(ButtonType.CANCEL);
        } catch (Exception e) {
            e.printStackTrace();
            return ButtonType.CANCEL;
        }
    }

}
