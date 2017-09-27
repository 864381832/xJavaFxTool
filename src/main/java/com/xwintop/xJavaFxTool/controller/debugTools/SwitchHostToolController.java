package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.view.debugTools.SwitchHostToolView;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

public class SwitchHostToolController extends SwitchHostToolView {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        TreeItem<String> treeItem = new TreeItem<String>("Hosts");
        treeItem.setExpanded(true);
        hostFileTreeView.setRoot(treeItem);
        TreeItem<String> commonHostTreeItem = new TreeItem<String>("公共Host");
        TreeItem<String> systemHostTreeItem = new TreeItem<String>("系统当前Host");
        TreeItem<String> localHostTreeItem = new TreeItem<String>("本地方案");
        localHostTreeItem.setExpanded(true);
        TreeItem<String> localHostTreeItem1 = new TreeItem<String>("方案一");
        TreeItem<String> localHostTreeItem2 = new TreeItem<String>("方案二");
        localHostTreeItem.getChildren().add(localHostTreeItem1);
        localHostTreeItem.getChildren().add(localHostTreeItem2);
        TreeItem<String> webTreeItem = new TreeItem<String>("在线方案");
        webTreeItem.setExpanded(true);
        treeItem.getChildren().add(commonHostTreeItem);
        treeItem.getChildren().add(systemHostTreeItem);
        treeItem.getChildren().add(localHostTreeItem);
        treeItem.getChildren().add(webTreeItem);
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void addAction(ActionEvent event) {
    }

    @FXML
    private void reloadAction(ActionEvent event) {
    }

    @FXML
    private void editAction(ActionEvent event) {
    }

    @FXML
    private void deleteAction(ActionEvent event) {
    }
}