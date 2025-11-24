package com.xwintop.xJavaFxTool.controller.codeTools;

import com.xwintop.xJavaFxTool.services.codeTools.FileUnicodeTransformationToolService;
import com.xwintop.xJavaFxTool.view.codeTools.FileUnicodeTransformationToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

/**
 * @ClassName: FileUnicodeTransformationToolController
 * @Description: 文件编码转换工具
 * @author: xufeng
 * @date: 2019/10/9 15:01
 */

@Getter
@Setter
@Slf4j
public class FileUnicodeTransformationToolController extends FileUnicodeTransformationToolView {
    private FileUnicodeTransformationToolService fileUnicodeTransformationToolService = new FileUnicodeTransformationToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        oldFileUnicodeComboBox.getItems().add("自动检测");
        oldFileUnicodeComboBox.getItems().addAll(Charset.availableCharsets().keySet());
        oldFileUnicodeComboBox.setValue("UTF-8");
        newFileUnicodeComboBox.getItems().addAll(Charset.availableCharsets().keySet());
        newFileUnicodeComboBox.setValue(Charset.defaultCharset().name());
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(detectPathTextField, FileChooserUtil.FileType.FILE);
    }

    private void initService() {
    }

    @FXML
    private void detectPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            detectPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void transformationAction(ActionEvent event) {
        try {
            fileUnicodeTransformationToolService.transformationAction();
        } catch (Exception e) {
            log.error("文件编码转换失败！", e);
        }
    }
}