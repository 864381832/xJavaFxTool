package com.xwintop.xJavaFxTool.controller.debugTools;

import com.easipass.gateway.entity.TaskConfig;
import com.xwintop.xJavaFxTool.services.debugTools.ZookeeperToolService;
import com.xwintop.xJavaFxTool.view.debugTools.ZookeeperToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.net.URL;
import java.util.Date;
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
        nodeTreeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = nodeTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                zookeeperToolService.nodeSelectionChanged(selectedItem);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_UnfoldAll = new MenuItem("展开所有");
                menu_UnfoldAll.setOnAction(event1 -> {
                    nodeTreeView.getRoot().setExpanded(true);
                    nodeTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(true);
                    });
                });
                MenuItem menu_FoldAll = new MenuItem("折叠所有");
                menu_FoldAll.setOnAction(event1 -> {
                    nodeTreeView.getRoot().setExpanded(false);
                    nodeTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(false);
                    });
                });
                ContextMenu contextMenu = new ContextMenu(menu_UnfoldAll, menu_FoldAll);
                if (selectedItem.getValue().endsWith("service.yml")) {
                    MenuItem menu_AddTask = new MenuItem("添加任务");
                    menu_AddTask.setOnAction(event1 -> {
                        String taskConfigName = "taskConfig" + DateFormatUtils.format(new Date(), "MMddHHmm");
                        TreeItem<String> addItem = new TreeItem<>(taskConfigName);
                        selectedItem.getChildren().add(addItem);
                        TaskConfig taskConfig = new TaskConfig();
                        taskConfig.setName(taskConfigName);
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
                    MenuItem menu_SaveFile = new MenuItem("保存文件");
                    contextMenu.getItems().add(menu_SaveFile);
                }
                nodeTreeView.setContextMenu(contextMenu);
            }
        });
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
        zookeeperToolService.deleteNodeOnAction();
    }

    @FXML
    private void addNodeOnAction(ActionEvent event) {
        zookeeperToolService.addNodeOnAction();
    }

    @FXML
    private void nodeDataSaveOnAction(ActionEvent event) {
        zookeeperToolService.nodeDataSaveOnAction();
    }


}