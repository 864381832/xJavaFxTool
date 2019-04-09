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
//                MenuItem menu_Rename = new MenuItem("重命名结点");
//                menu_Rename.setOnAction(event1 -> {
//                    zookeeperToolService.renameNodeOnAction();
//                });
//                contextMenu.getItems().add(menu_Rename);
                MenuItem menu_AddNode = new MenuItem("添加子结点");
                menu_AddNode.setOnAction(event1 -> {
                    zookeeperToolService.addNodeOnAction();
                });
                contextMenu.getItems().add(menu_AddNode);
                MenuItem menu_RemoveNode = new MenuItem("删除");
                menu_RemoveNode.setOnAction(event1 -> {
                    zookeeperToolService.deleteNodeOnAction();
                });
                contextMenu.getItems().add(menu_RemoveNode);
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