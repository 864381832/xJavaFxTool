package com.xwintop.xJavaFxTool.controller.assistTools;

import com.xwintop.xJavaFxTool.view.assistTools.DecompilerWxApkgToolView;
import com.xwintop.xJavaFxTool.services.assistTools.DecompilerWxApkgToolService;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @ClassName: DecompilerWxApkgToolController
 * @Description: 微信小程序反编译工具
 * @author: xufeng
 * @date: 2018/7/4 14:44
 */
@Getter
@Setter
@Slf4j
public class DecompilerWxApkgToolController extends DecompilerWxApkgToolView {
    private DecompilerWxApkgToolService decompilerWxApkgToolService = new DecompilerWxApkgToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(packageFileTextField, FileChooserUtil.FileType.FILE);
        FileChooserUtil.setOnDrag(decompilePathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void packageFileButtonAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            packageFileTextField.setText(file.getPath());
        }
    }

    @FXML
    private void decompilePathButtonAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            decompilePathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void decompileButtonAction(ActionEvent event) throws Exception {
        decompilerWxApkgToolService.decompileButtonAction();
    }
}