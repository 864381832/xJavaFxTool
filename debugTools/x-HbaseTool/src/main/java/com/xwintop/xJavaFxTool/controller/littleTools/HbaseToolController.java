package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.view.littleTools.HbaseToolView;
import com.xwintop.xJavaFxTool.services.littleTools.HbaseToolService;
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
public class HbaseToolController extends HbaseToolView {
    private HbaseToolService hbaseToolService = new HbaseToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void connectAction(ActionEvent event) {
    }

    @FXML
    private void searchContentAction(ActionEvent event) {
    }

    @FXML
    private void searchDirectoryAction(ActionEvent event) {
    }
}