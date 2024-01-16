package com.xwintop.xcore.javafx.dialog;

import com.xwintop.xcore.javafx.control.DoubleSpinner;
import com.xwintop.xcore.javafx.control.IntegerSpinner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的表单对话框
 */
public class SimpleFormDialog {

    /**
     * 表单字段类型
     */
    public enum FieldType {
        TEXT, PASSWORD, COMBOBOX, CHECKBOX, DATE, TIME, DATETIME, INTEGER, DECIMAL, TEXTAREA, FILE
    }

    /**
     * 表单字段
     */
    @Data
    public static class Field<T> {

        private String label;       // 标签

        private FieldType type;     // 类型

        private T value;            // 值

        private List<T> options;    // 选项

        @SuppressWarnings("unchecked")
        public void setValue(Object value) {
            this.value = (T) value;
        }

        @SafeVarargs
        public Field(FieldType fieldType, String label, T defaultValue, T... options) {
            this.type = fieldType;
            this.label = label;
            this.value = defaultValue;
            this.options = new ArrayList<>();
            if (options != null) {
                Collections.addAll(this.options, options);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////

    private String title;   // 对话框标题

    private boolean ok;     // 用户是否按下了确定按钮

    private final List<Field<?>> fields = new ArrayList<>();

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean showAndWait() {
        new FxDialog<>()
            .setBody(createDialogBody())
            .setResizable(true)
            .setTitle(title)
            .setButtonTypes(ButtonType.OK, ButtonType.CANCEL)
            .setButtonHandler(ButtonType.CANCEL, (event, dialog) -> dialog.close())
            .setButtonHandler(ButtonType.OK, (event, dialog) -> {
                ok = true;
                dialog.close();
            })
            .showAndWait();

        return ok;
    }

    private Parent createDialogBody() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 10, 0, 10));

        AtomicInteger rowCounter = new AtomicInteger();
        fields.forEach(field -> {
            Label label = new Label(field.getLabel());
            label.setMinWidth(Region.USE_PREF_SIZE);

            HBox hBox = new HBox(label);
            hBox.setAlignment(Pos.TOP_LEFT);
            hBox.setPadding(new Insets(4, 0, 0, 0));
            hBox.setFillHeight(true);

            gridPane.add(hBox, 0, rowCounter.incrementAndGet());

            Region fieldValueNode = createField(field);
            if (fieldValueNode != null) {
                gridPane.add(fieldValueNode, 1, rowCounter.get());
                GridPane.setHgrow(fieldValueNode, Priority.ALWAYS);
            }
        });

        return gridPane;
    }

    private Region createField(Field<?> field) {
        switch (field.getType()) {
            case TEXT: {
                TextField textField = new TextField(field.getValue().toString());
                textField.textProperty().addListener((observable, oldValue, newValue) -> field.setValue(newValue));
                return textField;
            }
            case PASSWORD: {
                PasswordField passwordField = new PasswordField();
                passwordField.textProperty().addListener((observable, oldValue, newValue) -> field.setValue(newValue));
                return passwordField;
            }
            case COMBOBOX: {
                ComboBox<Object> comboBox = new ComboBox<>();
                comboBox.getItems().addAll(field.getOptions());
                comboBox.getSelectionModel().select(field.getValue());
                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> field.setValue(newValue));
                return comboBox;
            }
            case CHECKBOX: {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(field.getValue().equals(true));
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> field.setValue(newValue));
                return checkBox;
            }
            case DATE: {
                DatePicker datePicker = new DatePicker();
                datePicker.setValue((LocalDate) field.getValue());
                datePicker.valueProperty().addListener((observable, oldValue, newValue) -> field.setValue(newValue));
                return datePicker;
            }
            case TEXTAREA: {
                TextArea textArea = new TextArea(field.getValue().toString());
                textArea.textProperty().addListener((observable, oldValue, newValue) -> field.setValue(newValue));
                return textArea;
            }
            case INTEGER: {
                return new IntegerSpinner(0, 100, 0, 1); // TODO 如何封装
            }
            case DECIMAL: {
                return new DoubleSpinner(0, 100, 0, 1); // TODO 如何封装
            }
            case FILE: {
                // TODO 实现文件选择
            }
        }
        return null;
    }

    public void addFields(List<Field<?>> fields) {
        this.fields.addAll(fields);
    }

    public void addFields(Field<?>... fields) {
        addFields(Arrays.asList(fields));
    }
}
