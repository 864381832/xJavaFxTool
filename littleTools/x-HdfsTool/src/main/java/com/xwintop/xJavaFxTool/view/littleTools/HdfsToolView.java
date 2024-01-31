package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class HdfsToolView implements Initializable {
    @FXML
    protected TextField hdfsUrlTextField;
    @FXML
    protected TextField userNameTextField;
    @FXML
    protected TextField kerberosTextField;
    @FXML
    protected TableView<Map<String, String>> hadoopConfTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> hadoopConfKeyTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> hadoopConfValueTableColumn;
    @FXML
    protected TableView<Map<String, String>> systemConfTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> systemConfKeyTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> systemConfValueTableColumn;
    @FXML
    protected Button connectButton;
    @FXML
    protected ChoiceBox<String> fileTypeChoiceBox;
    @FXML
    protected Spinner<Integer> fileSizeFromSpinner;
    @FXML
    protected ChoiceBox<String> fileSizeFromChoiceBox;
    @FXML
    protected Spinner<Integer> fileSizeToSpinner;
    @FXML
    protected ChoiceBox<String> fileSizeToChoiceBox;
    @FXML
    protected TextField searchDirectoryTextField;
    @FXML
    protected Button searchDirectoryButton;
    @FXML
    protected TreeView<Map<String, Object>> hdfsListTreeView;
    @FXML
    protected TableView<Map<String, String>> searchResultTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> fileNameTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> absolutePathTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> fileSizeTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> lastModifiedTableColumn;
    @FXML
    protected TextArea fileContentTextArea;
}