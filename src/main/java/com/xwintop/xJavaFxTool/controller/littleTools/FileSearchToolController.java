package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.FileSearchToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileSearchToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: FileSearchToolController
 * @Description: 文件搜索工具
 * @author: xufeng
 * @date: 2019/7/18 10:21
 */

@Getter
@Setter
@Slf4j
public class FileSearchToolController extends FileSearchToolView {
    private FileSearchToolService fileSearchToolService = new FileSearchToolService(this);
    private ObservableList<Map<String, String>> searchResultTableData = FXCollections.observableArrayList();

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
        JavaFxViewUtil.setTableColumnMapValueFactory(lastModifiedTableColumn, "lastModified", false);
        searchResultTableVIew.setItems(searchResultTableData);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(searchDirectoryTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    public void searchContentAction() {

    }

    @FXML
    private void refreshIndexAction(ActionEvent event) {
    }

    @FXML
    private void searchDirectoryAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            searchDirectoryTextField.setText(file.getPath());
        }
    }
}