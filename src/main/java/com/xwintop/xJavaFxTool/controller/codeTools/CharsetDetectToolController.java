package com.xwintop.xJavaFxTool.controller.codeTools;

import com.xwintop.xJavaFxTool.services.codeTools.CharsetDetectToolService;
import com.xwintop.xJavaFxTool.view.codeTools.CharsetDetectToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: CharsetDetectToolController
 * @Description: 编码检测工具
 * @author: xufeng
 * @date: 2019/4/27 0027 18:13
 */

@Getter
@Setter
@Slf4j
public class CharsetDetectToolController extends CharsetDetectToolView {
    private CharsetDetectToolService charsetDetectToolService = new CharsetDetectToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
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
    private void detectAction(ActionEvent event) throws Exception {
        charsetDetectToolService.detectAction();
    }
}