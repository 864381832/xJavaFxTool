package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.Mp3ConvertToolService;
import com.xwintop.xJavaFxTool.view.littleTools.Mp3ConvertToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: Mp3ConvertToolController
 * @Description: mp3格式转换工具
 * @author: xufeng
 * @date: 2019/8/8 0008 20:41
 */

@Getter
@Setter
@Slf4j
public class Mp3ConvertToolController extends Mp3ConvertToolView {
    private Mp3ConvertToolService mp3ConvertToolService = new Mp3ConvertToolService(this);
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();//表格数据

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(fileNameTableColumn, "fileName", false);
        JavaFxViewUtil.setTableColumnMapValueFactory(absolutePathTableColumn, "absolutePath", false);
        JavaFxViewUtil.setTableColumnMapValueFactory(fileSizeTableColumn, "fileSize", false);
        JavaFxViewUtil.setTableColumnMapValueFactory(convertStatusTableColumn, "convertStatus", false);
        tableViewMain.setItems(tableData);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(outputFolderTextField, FileChooserUtil.FileType.FOLDER);
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Remove = new MenuItem("移除选中行");
                menu_Remove.setOnAction(event1 -> {
                    tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_RemoveAll = new MenuItem("移除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    tableData.clear();
                });
                tableViewMain.setContextMenu(new ContextMenu(menu_Remove, menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void addFileAction(ActionEvent event) throws Exception {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            mp3ConvertToolService.addTableData(file);
        }
    }

    @FXML
    private void addFolderAction(ActionEvent event) throws Exception {
        File folder = FileChooserUtil.chooseDirectory();
        if (folder != null) {
            FileUtils.listFiles(folder, null, false).forEach((File file) -> {
                mp3ConvertToolService.addTableData(file);
            });
        }
    }

    @FXML
    private void convertAction(ActionEvent event) {
        mp3ConvertToolService.convertAction();
    }

    @FXML
    private void outputFolderAction(ActionEvent event) {
        File folder = FileChooserUtil.chooseDirectory();
        if (folder != null) {
            outputFolderTextField.setText(folder.getPath());
        }
    }

    @FXML
    private void openOutputFolderAction(ActionEvent event) {
        if (StringUtils.isNotEmpty(outputFolderTextField.getText())) {
            JavaFxSystemUtil.openDirectory(outputFolderTextField.getText());
        }
    }
}