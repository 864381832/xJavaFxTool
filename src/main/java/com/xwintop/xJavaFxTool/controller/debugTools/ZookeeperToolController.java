package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.services.debugTools.ZookeeperToolService;
import com.xwintop.xJavaFxTool.view.debugTools.ZookeeperToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ZookeeperToolController
 * @Description: Zookeeper工具
 * @author: xufeng
 * @date: 2019/4/8 17:00
 */

@Getter
@Setter
@Slf4j
public class ZookeeperToolController extends ZookeeperToolView {
    private ZookeeperToolService zookeeperToolService = new ZookeeperToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        TreeItem<String> treeItem = new TreeItem<String>("/");
        nodeTreeView.setRoot(treeItem);
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void connectOnAction(ActionEvent event) {
        zookeeperToolService.connectOnAction();
    }

    @FXML
    private void disconnectOnAction(ActionEvent event) {
        zookeeperToolService.disconnectOnAction();
    }

    @FXML
    private void refreshOnAction(ActionEvent event) {
        zookeeperToolService.refreshOnAction();
    }

    @FXML
    private void deleteNodeOnAction(ActionEvent event) {
        zookeeperToolService.disconnectOnAction();
    }

    @FXML
    private void addNodeOnAction(ActionEvent event) {
        zookeeperToolService.addNodeOnAction();
    }

    @FXML
    private void nodeDataSaveOnAction(ActionEvent event) {

    }


}