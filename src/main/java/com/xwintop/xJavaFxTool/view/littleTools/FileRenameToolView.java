package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class FileRenameToolView implements Initializable {
    @FXML
    protected Button addFileButton;
    @FXML
    protected Button addFolderButton;
    @FXML
    protected Button previewButton;
    @FXML
    protected Button renameButton;
    @FXML
    protected Button addRoleTableButton;
    @FXML
    protected Button removeRuleTableButton;
    @FXML
    protected Button upRuleTableButton;
    @FXML
    protected Button downRuleTableButton;
    @FXML
    protected TableView<Map<String,String>> ruleTableView;
    @FXML
    protected TableColumn<Map<String,String>,String> orderTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> ruleTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> explainTableColumn;
    @FXML
    protected TableView<Map<String,String>> fileInfoTableView;
    @FXML
    protected TableColumn<Map<String,String>,String> statusTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> fileNameTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> newFileNameTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> errorInfoTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> filesPathTableColumn;


    @FXML
    protected TextField filePrefixTextField;
    @FXML
    protected TextField filePostfixTextField;
    @FXML
    protected Spinner<Integer> startNumberOfRenameTab;
    @FXML
    protected TextField fileQueryStringOfRenameTab;
    @FXML
    protected TextField fileReplaceStringOfRenameTab;
    @FXML
    protected TextField filePrefixAddableText;
    @FXML
    protected TextField filePostfixAddableText;
}