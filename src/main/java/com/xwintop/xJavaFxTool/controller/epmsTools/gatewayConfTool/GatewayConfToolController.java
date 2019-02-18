package com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool;

import com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool.GatewayConfToolService;
import com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool.GatewayConfToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class GatewayConfToolController extends GatewayConfToolView {
    private GatewayConfToolService gatewayConfToolService = new GatewayConfToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        TreeItem<String> treeItem = new TreeItem<String>("TaskConfig列表");
        treeItem.setExpanded(true);
        configurationTreeView.setRoot(treeItem);
        configurationPathTextField.setText("E:\\ideaWorkspaces\\gatewaySpring\\configuration");
    }

    private void initEvent() {
        configurationTreeView.setEditable(true);
        configurationTreeView.setCellFactory(TextFieldTreeCell.forTreeView());
        configurationTreeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = configurationTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!selectedItem.getValue().endsWith("service.yml") && !selectedItem.getValue().equals("TaskConfig列表")) {
                    gatewayConfToolService.addTaskConfigTabPane(selectedItem.getParent().getValue(), selectedItem.getValue());
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_UnfoldAll = new MenuItem("展开所有");
                menu_UnfoldAll.setOnAction(event1 -> {
                    configurationTreeView.getRoot().setExpanded(true);
                    configurationTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(true);
                    });
                });
                MenuItem menu_FoldAll = new MenuItem("折叠所有");
                menu_FoldAll.setOnAction(event1 -> {
                    configurationTreeView.getRoot().setExpanded(false);
                    configurationTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(false);
                    });
                });
                ContextMenu contextMenu = new ContextMenu(menu_UnfoldAll, menu_FoldAll);

                if (selectedItem.getValue().endsWith("service.yml")) {
                    MenuItem menu_AddTask = new MenuItem("添加任务");
                    menu_AddTask.setOnAction(event1 -> {
                        TreeItem<String> addItem = new TreeItem<>("newService");
                        selectedItem.getChildren().add(addItem);
                    });
                    contextMenu.getItems().add(menu_AddTask);
                    MenuItem menu_RemoveFile = new MenuItem("删除文件");
                    menu_RemoveFile.setOnAction(event1 -> {
                        selectedItem.getParent().getChildren().remove(selectedItem);
                    });
                    contextMenu.getItems().add(menu_RemoveFile);
                    MenuItem menu_RemoveAll = new MenuItem("删除所有任务");
                    menu_RemoveAll.setOnAction(event1 -> {
                        selectedItem.getChildren().clear();
                    });
                    contextMenu.getItems().add(menu_RemoveAll);
                }
                if (!selectedItem.getValue().endsWith("service.yml") && !selectedItem.getValue().equals("TaskConfig列表")) {
                    MenuItem menu_Copy = new MenuItem("复制选中行");
                    menu_Copy.setOnAction(event1 -> {
                        TreeItem<String> addItem = new TreeItem<>(selectedItem.getValue());
                        selectedItem.getParent().getChildren().add(addItem);
                    });
                    contextMenu.getItems().add(menu_Copy);
                    MenuItem menu_Remove = new MenuItem("删除选中任务");
                    menu_Remove.setOnAction(event1 -> {
                        selectedItem.getParent().getChildren().remove(selectedItem);
                    });
                    contextMenu.getItems().add(menu_Remove);
                }
                configurationTreeView.setContextMenu(contextMenu);
            }
        });
        taskConfigTabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_RemoveAll = new MenuItem("关闭所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    taskConfigTabPane.getTabs().removeAll(taskConfigTabPane.getTabs());
                    gatewayConfToolService.getTaskConfigTabMap().clear();
                });
                taskConfigTabPane.setContextMenu(new ContextMenu(menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void treeRefurbishAction(ActionEvent event) {
        gatewayConfToolService.reloadTaskConfigFile();
    }

    @FXML
    private void connectAction(ActionEvent event) {
    }
}