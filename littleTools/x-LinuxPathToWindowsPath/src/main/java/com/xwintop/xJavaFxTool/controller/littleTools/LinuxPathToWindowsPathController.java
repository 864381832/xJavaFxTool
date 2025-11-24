package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import cn.hutool.system.SystemUtil;
import org.apache.commons.io.FileUtils;

import com.xwintop.xcore.util.javafx.AlertUtil;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * @ClassName: LinuxPathToWindowsPathController
 * @Description: 路径转换工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:09
 */

public class LinuxPathToWindowsPathController implements Initializable {
    @FXML
    private TextField textFieldLinuxPath;
    @FXML
    private ChoiceBox<String> choiceBoxChooseLUN;
    @FXML
    private Button buttonCreateWindowsPath;
    @FXML
    private TextField textFieldWindowsPath;
    @FXML
    private Button buttonCreateLinuxPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        if (SystemUtil.getOsInfo().isWindows()) {
            ObservableList<String> observableList = choiceBoxChooseLUN.getItems();
            for (char c = 'A'; c <= 'Z'; c++) {
                String dirName = c + ":/";
                File win = new File(dirName);
                if (win.exists()) {
                    observableList.add(dirName);
                }
            }
        } else {
            choiceBoxChooseLUN.getItems().add("/");
        }
        choiceBoxChooseLUN.setValue(choiceBoxChooseLUN.getItems().get(0));
    }

    private void initEvent() {
        textFieldLinuxPath.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    createWindowsPath(null);
                }
            }
        });
    }

    @FXML
    private void createWindowsPath(ActionEvent event) {
        try {
            String folderPath = choiceBoxChooseLUN.getValue() + textFieldLinuxPath.getText();
            FileUtils.forceMkdir(new File(folderPath));
            textFieldWindowsPath.setText(folderPath);
        } catch (Exception e) {
            AlertUtil.showInfoAlert("转换异常，请检查路径。");
        }
    }

    @FXML
    private void createLinuxPath(ActionEvent event) {
    }

}
