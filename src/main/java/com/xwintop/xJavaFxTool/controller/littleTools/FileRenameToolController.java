package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.FileRenameToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileRenameToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: FileRenameToolController
 * @Description: 文件重命名工具
 * @author: xufeng
 * @date: 2018/2/5 17:03
 */

@Getter
@Setter
@Slf4j
public class FileRenameToolController extends FileRenameToolView {
    private FileRenameToolService fileRenameToolService = new FileRenameToolService(this);
    private ObservableList<Map<String, String>> ruleTableData = FXCollections.observableArrayList();
    private ObservableList<Map<String, String>> fileInfoTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(orderTableColumn, "order", false);
        JavaFxViewUtil.setTableColumnMapValueFactory(ruleTableColumn, "rule");
        JavaFxViewUtil.setTableColumnMapValueFactory(explainTableColumn, "explain");
        ruleTableView.setItems(ruleTableData);
        JavaFxViewUtil.setTableColumnMapAsCheckBoxValueFactory(statusTableColumn, "status");
        JavaFxViewUtil.setTableColumnMapValueFactory(fileNameTableColumn, "fileName");
        JavaFxViewUtil.setTableColumnMapValueFactory(newFileNameTableColumn, "newFileName");
        JavaFxViewUtil.setTableColumnMapValueFactory(errorInfoTableColumn, "errorInfo");
        JavaFxViewUtil.setTableColumnMapValueFactory(filesPathTableColumn, "filesPath");
        fileInfoTableView.setItems(fileInfoTableData);

        JavaFxViewUtil.setSpinnerValueFactory(startNumberOfRenameTab, 0, Integer.MAX_VALUE, 0);
    }

    private void initEvent() {

    }

    private void initService() {
    }

    @FXML
    private void addFileAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            Map<String, String> dataRow = new HashMap<String, String>();
            dataRow.put("status", "true");
            dataRow.put("fileName", file.getName());
            dataRow.put("newFileName", "");
            dataRow.put("errorInfo", "");
            dataRow.put("filesPath", file.getPath());
            fileInfoTableData.add(dataRow);
        }
    }

    @FXML
    private void addFolderAction(ActionEvent event) {
        File folderFile = FileChooserUtil.chooseDirectory();
        if (folderFile != null) {
            for (File file : FileUtils.listFiles(folderFile, null, false)) {
                Map<String, String> dataRow = new HashMap<String, String>();
                dataRow.put("status", "true");
                dataRow.put("fileName", file.getName());
                dataRow.put("newFileName", "");
                dataRow.put("errorInfo", "");
                dataRow.put("filesPath", file.getPath());
                fileInfoTableData.add(dataRow);
            }
        }
    }

    @FXML
    private void previewAction(ActionEvent event) {
    }

    @FXML
    private void renameAction(ActionEvent event) {
        for (Map<String, String> fileInfoTableDatum : fileInfoTableData) {
            if ("true".equals(fileInfoTableDatum.get("status"))) {
                File file = new File(fileInfoTableDatum.get("filesPath"));
                File newFile = new File(file.getParent(), fileInfoTableDatum.get("newFileName"));
                file.renameTo(newFile);
                fileInfoTableDatum.put("fileName", newFile.getName());
                fileInfoTableDatum.put("filesPath", newFile.getPath());
            }
        }
        fileInfoTableView.refresh();
        TooltipUtil.showToast("重命名成功！");
    }

    @FXML
    private void addRoleTableAction(ActionEvent event) {
        Map<String, String> dataRow = new HashMap<String, String>();
        dataRow.put("order", "true");
        dataRow.put("rule", "true");
        dataRow.put("explain", "true");
        ruleTableData.add(dataRow);
    }

    @FXML
    private void removeRuleTableAction(ActionEvent event) {
        ruleTableData.remove(ruleTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void upRuleTableAction(ActionEvent event) {
    }

    @FXML
    private void downRuleTableAction(ActionEvent event) {
    }

    public void generateRenameDestFilesOfFormat() {
        if (!fileInfoTableData.isEmpty()) {
            int startNumber = startNumberOfRenameTab.getValue();
            String filePrefixString = filePrefixTextField.getText().trim();
            String filePostfixString = filePostfixTextField.getText().trim();
            for (Map<String, String> fileInfoTableDatum : fileInfoTableData) {
                String fileName = fileInfoTableDatum.get("fileName");
                fileInfoTableDatum.put("newFileName", filePrefixString + fileName + (startNumber++) + filePostfixString);
            }
            fileInfoTableView.refresh();
        }
    }

    public void generateRenameDestFilesOfReplace() {
        for (Map<String, String> fileInfoTableDatum : fileInfoTableData) {
            String fileName = fileInfoTableDatum.get("fileName");
            fileInfoTableDatum.put("newFileName", fileName.replaceAll(fileQueryStringOfRenameTab.getText().trim(), fileReplaceStringOfRenameTab.getText().trim()));
        }
        fileInfoTableView.refresh();
    }

    public void generateRenameDestFilesOfAddable() {
        for (Map<String, String> fileInfoTableDatum : fileInfoTableData) {
            String fileName = fileInfoTableDatum.get("fileName");
            fileInfoTableDatum.put("newFileName", filePrefixAddableText.getText().trim() + fileName + filePostfixAddableText.getText().trim());
        }
        fileInfoTableView.refresh();
    }
}