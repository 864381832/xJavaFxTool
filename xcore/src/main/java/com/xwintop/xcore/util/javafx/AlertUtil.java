package com.xwintop.xcore.util.javafx;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.label;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.vbox;

import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxDialog;
import com.xwintop.xcore.javafx.helper.LayoutHelper;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Deprecated
public class AlertUtil {

    public static final int LABEL_MAX_WIDTH = 300;

    /**
     * 信息提示框
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static void showInfoAlert(String message) {
        showInfoAlert("提示", message);
    }

    /**
     * 信息提示框
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static void showInfoAlert(String title, String message) {
        new FxDialog<>()
            .setOwner(FxApp.primaryStage)
            .setTitle(title)
            .setBody(vbox(20, 0, Pos.CENTER, label(message, LABEL_MAX_WIDTH)))
            .setButtonTypes(ButtonType.OK)
            .setButtonHandler(ButtonType.OK, (actionEvent, stage) -> stage.close())
            .showAndWait();
    }

    /**
     * 确定提示框
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static boolean confirmYesNo(String title, String message) {
        return confirm(title, message, ButtonType.YES, ButtonType.NO) == ButtonType.YES;
    }

    /**
     * 确定提示框
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static boolean confirmOkCancel(String title, String message) {
        return confirm(title, message, ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * 确定提示框
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static ButtonType confirmYesNoCancel(String title, String message) {
        return confirm(title, message, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }

    /**
     * 确定提示框
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static ButtonType confirm(
        String title, String message, ButtonType positiveButtonType, ButtonType... negativeButtonTypes
    ) {

        // 构造 buttonTypes
        ButtonType[] buttonTypes = new ButtonType[
            (negativeButtonTypes == null ? 1 : negativeButtonTypes.length) + 1];

        buttonTypes[0] = positiveButtonType;

        if (negativeButtonTypes != null) {
            System.arraycopy(negativeButtonTypes, 0, buttonTypes, 1, negativeButtonTypes.length);
        }

        // 构造对话框
        FxDialog<Object> dialog = new FxDialog<>()
            .setTitle(title)
            .setButtonTypes(buttonTypes)
            .setOwner(FxApp.primaryStage)
            .setBody(vbox(10, 0, label(message)));

        ButtonType[] result = new ButtonType[]{ButtonType.CANCEL};

        dialog.setButtonHandler(positiveButtonType, (actionEvent, stage) -> {
            result[0] = positiveButtonType;
            stage.close();
        });

        if (negativeButtonTypes != null) {
            for (ButtonType negativeButtonType : negativeButtonTypes) {
                dialog.setButtonHandler(negativeButtonType, (actionEvent, stage) -> {
                    result[0] = negativeButtonType;
                    stage.close();
                });
            }
        }

        // 显示对话框
        dialog.showAndWait();
        return result[0];
    }

    /**
     * 输入提示框，如果点击确定则返回文本框内容，点击取消或关闭则返回 null
     */
    public static String showInputAlertDefaultValue(String message, String defaultValue) {
        String[] result = new String[]{null};

        TextField textField = LayoutHelper.textField(defaultValue, 200);
        VBox body = vbox(10, 10, label(message, LABEL_MAX_WIDTH), textField);

        new FxDialog<>()
            .setOwner(FxApp.primaryStage)
            .setBody(body)
            .setTitle("提示")
            .setButtonTypes(ButtonType.OK, ButtonType.CANCEL)
            .setButtonHandler(ButtonType.OK, (actionEvent, stage) -> {
                result[0] = textField.getText();
                stage.close();
            })
            .setButtonHandler(ButtonType.CANCEL, (actionEvent, stage) -> stage.close())
            .showAndWait();

        return result[0];
    }

    //////////////////////////////////////////////////////////////

    /**
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.dialog.FxAlerts}
     */
    public static boolean showConfirmAlert(String message) {
        VBox vBox = new VBox(15);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        Label textArea = new Label(message);
        textArea.setFont(Font.font(18));
        vBox.getChildren().add(textArea);
        Button button = new Button("确定");
        button.setFont(new Font(16));
        vBox.getChildren().add(button);
        Stage newStage = JavaFxViewUtil.getNewStageNull("提示", null, vBox, -1, -1, false, false, false);
        newStage.initModality(Modality.APPLICATION_MODAL);
        AtomicBoolean isOk = new AtomicBoolean(false);
        button.setOnMouseClicked(event -> {
            isOk.set(true);
            newStage.close();
        });
        newStage.showAndWait();
        return isOk.get();
    }

    @Deprecated
    public static String showInputAlert(String message) {
        return showInputAlertDefaultValue(message, null);
    }

    public static String[] showInputAlert(String message, String... names) {
        return showInputAlertMore(message, names);
    }

    public static String[] showInputAlertMore(String message, String... names) {
        return showInputAlertMore(message, names, new String[names.length]);
    }

    public static String[] showInputAlertMore(String message, String[] names, String[] defaultValue) {
        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        TextField[] textFields = new TextField[names.length];
        for (int i = 0; i < names.length; i++) {
            TextField textField = new TextField();
            textField.setText(defaultValue[i]);
            textField.setMinWidth(100);
            textField.prefColumnCountProperty().bind(textField.textProperty().length());
            GridPane.setHgrow(textField, Priority.ALWAYS);
            page1Grid.add(new Label(names[i]), 0, i);
            page1Grid.add(textField, 1, i);
            textFields[i] = textField;
        }

        Alert alert = new Alert(Alert.AlertType.NONE, null, new ButtonType("取消", ButtonBar.ButtonData.NO),
            new ButtonType("确定", ButtonBar.ButtonData.YES));
        alert.setTitle(message);
        alert.setGraphic(page1Grid);
        alert.setWidth(200);
        Optional<ButtonType> _buttonType = alert.showAndWait();
        // 根据点击结果返回
        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
            String[] stringS = new String[names.length];
            for (int i = 0; i < textFields.length; i++) {
                stringS[i] = textFields[i].getText();
            }
            return stringS;
        }
        return null;
    }
}
