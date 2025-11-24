package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.view.games.ChineseChessView;
import com.xwintop.xJavaFxTool.services.games.ChineseChessService;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Getter
@Setter
@Slf4j
public class ChineseChessController extends ChineseChessView {
    private ChineseChessService chineseChessService = new ChineseChessService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        chineseChessService.initialize();
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void StartButtonOnAction(ActionEvent event) {
        chineseChessService.StartButtonOnAction();
    }

    @FXML
    private void restartButtonOnAction(ActionEvent event) {//重新开始按钮的响应函数
        chineseChessService.initialize();
    }

    @FXML
    private void showRecordButtonOnAction(ActionEvent event) {
        chineseChessService.showRecordButtonOnAction();
    }

    @FXML
    private void saveRecordButtonOnAction(ActionEvent event) throws Exception {
        chineseChessService.saveRecordButtonOnAction();
    }

    @FXML
    private void checkerBoardOnPressed(MouseEvent e) { //画布的鼠标点击响应函数
        chineseChessService.checkerBoardOnPressed(e);
    }
}