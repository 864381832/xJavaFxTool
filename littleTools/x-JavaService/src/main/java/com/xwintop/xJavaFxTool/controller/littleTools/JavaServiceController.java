package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.JavaServiceService;
import com.xwintop.xJavaFxTool.view.littleTools.JavaServiceView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: JavaServiceController
 * @Description: java服务安装工具
 * @author: xufeng
 * @date: 2020/9/22 13:14
 */

@Getter
@Setter
@Slf4j
public class JavaServiceController extends JavaServiceView {
    private JavaServiceService javaServiceService = new JavaServiceService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
//        idTextField.setText("gatewaySpring");
//        nameTextField.setText("gatewaySpring");
//        descriptionTextField.setText("This is gateway service.");
//        executableTextField.setText("C:\\easipass\\jre1.8.0_201\\bin\\java");
//        argumentsTextField.setText("-Dfile.encoding=UTF-8 -jar gatewaySpring.jar --spring.config.location=application.yml");
//        jarPathTextField.setText("C:\\easipass\\");
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(jarPathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void jarPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            jarPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void installAction(ActionEvent event) throws Exception {
        javaServiceService.installAction();
    }

    @FXML
    private void startAction(ActionEvent event) throws Exception {
        javaServiceService.startAction();
    }

    @FXML
    private void restartAction(ActionEvent event) throws Exception {
        javaServiceService.restartAction();
    }

    @FXML
    private void stopAction(ActionEvent event) throws Exception {
        javaServiceService.stopAction();
    }

    @FXML
    private void uninstallAction(ActionEvent event) throws Exception {
        javaServiceService.uninstallAction();
    }

    @FXML
    private void winswLinkOnAction() throws Exception {
        JavaFxSystemUtil.openBrowseURLThrowsException("https://github.com/winsw/winsw");
    }
}