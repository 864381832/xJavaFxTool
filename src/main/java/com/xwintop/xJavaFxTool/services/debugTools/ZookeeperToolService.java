package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.ZookeeperToolController;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @ClassName: ZookeeperToolService
 * @Description: Zookeeper工具
 * @author: xufeng
 * @date: 2019/4/8 17:00
 */

@Getter
@Setter
@Slf4j
public class ZookeeperToolService {
    public ZookeeperToolController zookeeperToolController;

    ZkClient zkClient = null;

    public ZookeeperToolService(ZookeeperToolController zookeeperToolController) {
        this.zookeeperToolController = zookeeperToolController;
    }

    public void connectOnAction() {
        if (zkClient == null) {
            zkClient = new ZkClient(zookeeperToolController.getZkServersTextField().getText().trim());
//            zkClient.setZkSerializer(new ZKStringSerializer());
        }
        zookeeperToolController.getNodeTreeView().getRoot().getChildren().clear();
        this.addNodeTree("/", zookeeperToolController.getNodeTreeView().getRoot());
    }

    private void addNodeTree(String path, TreeItem<String> treeItem) {
        List<String> list = zkClient.getChildren(path);
        for (String name : list) {
            log.info("获取到文件：" + path + "/" + name);
            TreeItem<String> treeItem2 = new TreeItem<>(name);
            treeItem.getChildren().add(treeItem2);
            this.addNodeTree(StringUtils.appendIfMissing(path, "/", "/") + name, treeItem2);
        }
    }

    public void disconnectOnAction() {

    }

    public void refreshOnAction() {

    }

    public void deleteNodeOnAction() {

    }

    public void addNodeOnAction() {

    }

    public void nodeDataSaveOnAction() {

    }
}