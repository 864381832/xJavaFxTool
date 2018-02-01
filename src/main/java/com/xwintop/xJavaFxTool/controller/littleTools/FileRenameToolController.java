package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileRenameToolView;
import com.xwintop.xJavaFxTool.services.littleTools.FileRenameToolService;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Getter
@Setter
@Log4j
public class FileRenameToolController extends FileRenameToolView {
    private FileRenameToolService fileRenameToolService = new FileRenameToolService(this);
    private ObservableList<Map<String, String>> ruleTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(orderTableColumn, "key",false);
        JavaFxViewUtil.setTableColumnMapValueFactory(ruleTableColumn, "value");
        JavaFxViewUtil.setTableColumnMapValueFactory(explainTableColumn, "value");
        ruleTableView.setItems(ruleTableData);
    }

    private void initEvent() {

    }

    private void initService() {
    }

    @FXML
    private void addFileAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
//            textFieldCopyFileOriginalPath.setText(file.getPath());
        }
    }

    @FXML
    private void addFolderAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
//            textFieldCopyFileTargetPath.setText(file.getPath());
        }
    }

    @FXML
    private void previewAction(ActionEvent event) {
    }

    @FXML
    private void renameAction(ActionEvent event) {
    }

    @FXML
    private void addRoleTableAction(ActionEvent event) {
    }

    @FXML
    private void removeRuleTableAction(ActionEvent event) {
    }

    @FXML
    private void upRuleTableAction(ActionEvent event) {
    }

    @FXML
    private void downRuleTableAction(ActionEvent event) {
    }
}