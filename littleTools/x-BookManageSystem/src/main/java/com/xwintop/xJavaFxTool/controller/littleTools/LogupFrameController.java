package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.BookManageSystemMain;
import com.xwintop.xJavaFxTool.utils.SimpleTools;
import com.xwintop.xJavaFxTool.view.littleTools.LogupFrameView;
import javafx.event.ActionEvent;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class LogupFrameController extends LogupFrameView {
    private SimpleTools simpleTools = new SimpleTools();

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 给组件添加图标
        Labeled[] labeleds = new Labeled[]{systemLabel, userNameLabel, passwordLabel, logupButton, resetButton};
        String[] imagePaths = new String[]{"/BookManageSystem/images/logo.png", "/BookManageSystem/images/userName.png", "/BookManageSystem/images/password.png", "/BookManageSystem/images/login.png", "/BookManageSystem/images/reset.png"};
        simpleTools.setLabeledImage(labeleds, imagePaths);
        userNameTextField.setText("admin");
        passwordTextField.setText("123456");
    }

    // “登录”按钮的事件监听器方法
    public void logupButtonEvent(ActionEvent event) {
        // 关闭登录界面
        stage.close();
        // 判断用户名和密码是否匹配
        boolean isOK = simpleTools.isLogIn(userNameTextField, passwordTextField, "admin", "123456");
        if (isOK) {
            // 如果登录成功则跳转到主界面
            new BookManageSystemMain().initMainFrame();
        }
    }

    // “重置”按钮的事件监听器方法
    public void resetButtonEvent(ActionEvent event) {
        // 重置将清空文本框
        userNameTextField.setText("");
        passwordTextField.setText("");
    }
}
