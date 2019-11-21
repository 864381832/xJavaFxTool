package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class RegexTesterView implements Initializable {
    @FXML
    protected TextField regexTextField;
    @FXML
    protected Button regulatButton;
    @FXML
    protected TableView<Map<String, String>> examplesTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> examplesTableColumn0;
    @FXML
    protected TableColumn<Map<String, String>, String> examplesTableColumn1;
    @FXML
    protected TextArea sourceTextArea;
    @FXML
    protected TextArea matchTextArea;
    @FXML
    protected TableView<Map<String, String>> matchTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn0;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn1;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn2;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn3;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn4;
    @FXML
    protected Button resetButton;
    @FXML
    protected Button aboutRegularButton;
    @FXML
    protected CheckBox ignoreCaseCheckBox;
    @FXML
    protected TextField replaceTextField;
    @FXML
    protected CheckBox isReplaceCheckBox;
}
