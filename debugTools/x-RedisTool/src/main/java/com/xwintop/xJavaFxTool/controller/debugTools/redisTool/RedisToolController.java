package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolService;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: RedisToolController
 * @Description: Redis工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:04
 */

@Getter
@Setter
@Slf4j
public class RedisToolController extends RedisToolView {
    private RedisToolService redisToolService = new RedisToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        TreeItem<String> treeItem = new TreeItem<String>("Redis服务器");
        redisServiceTreeView.setRoot(treeItem);
    }

    private void initEvent() {
        redisServiceTreeView.setOnMouseClicked(event -> {
//			System.out.println(event.getTarget().toString());
            if (event.getClickCount() == 2) {
                if (event.getTarget() instanceof TreeItem) {
                    TreeItem<String> eventTarget = (TreeItem<String>) event.getTarget();
                    System.out.println(eventTarget.getValue());
                    return;
                }
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                redisToolService.addServerMenuItem();
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof TreeItem) {
                    // TreeViewSkin<String> eventTarget = (TreeViewSkin<String>) event.getTarget();
                    // if("Redis服务器".equals(eventTarget.getValue())) {
//					redisToolService.addServerMenuItem();
                    // }
                }
            } else if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getTarget() instanceof TreeItem) {
//					TreeItem<String> eventTarget = (TreeItem<String>) event.getTarget();
//					String name = eventTarget.getValue();
//					if (name.startsWith("db")) {
//						RedisUtil redisUtil = redisToolService.getJedisMap().get(eventTarget.getParent().getValue());
//						int id = Integer.parseInt(name.substring(2, name.indexOf("(")));
//						redisUtil.setId(id);
//						Set<String> nodekeys = redisUtil.getListKeys();
//						for (String key : nodekeys) {
//							System.out.println(key);
//						}
//					}
                }
            }
        });
        redisServiceTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observable,
                                        TreeItem<String> oldValue, TreeItem<String> newValue) {
                        String name = newValue.getValue();
                        if (name.startsWith("db")) {
//							RedisUtil redisUtil = redisToolService.getJedisMap().get(newValue.getParent().getValue());
                            int id = Integer.parseInt(name.substring(2, name.indexOf("(")));
//							redisUtil.setId(id);
//							Set<String> nodekeys = redisUtil.getListKeys();
//							for (String key : nodekeys) {
//								System.out.println(key);
//							}
                            redisToolService.addDataServiceTabPane(newValue.getParent().getValue(), id);
                        }
                    }
                });
    }

    private void initService() {
//        try {
//            redisToolService.addServiceAddress("localhost", "localhost", 6379, null);
//        } catch (Exception e) {
//            log.error("初始化redis失败", e);
//        }
        redisServiceTreeView.getRoot().setExpanded(true);
    }

    @FXML
    private void treeLeftAction(ActionEvent event) {
    }

    @FXML
    private void treeRightAction(ActionEvent event) {
    }

    @FXML
    private void treeUpAction(ActionEvent event) {
    }

    @FXML
    private void treeRefurbishAction(ActionEvent event) {
        redisToolService.reloadServiceAddress();
    }
}
