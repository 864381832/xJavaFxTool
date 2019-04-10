package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.ZookeeperToolController;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, IZkChildListener> childListeners = new HashMap<>();
    private Map<String, IZkDataListener> dataListeners = new HashMap<>();

    public ZookeeperToolService(ZookeeperToolController zookeeperToolController) {
        this.zookeeperToolController = zookeeperToolController;
    }

    public void connectOnAction() {
        if (zkClient == null) {
            try {
                zkClient = new ZkClient(zookeeperToolController.getZkServersTextField().getText().trim(), zookeeperToolController.getConnectionTimeoutSpinner().getValue());
            } catch (Exception e) {
                TooltipUtil.showToast("连接失败！！！");
                zkClient = null;
            }
            zkClient.setZkSerializer(new ZkSerializer() {
                @Override
                public byte[] serialize(Object data) throws ZkMarshallingError {
                    try {
                        return String.valueOf(data).getBytes("utf-8");
                    } catch (UnsupportedEncodingException var3) {
                        throw new ZkMarshallingError(var3.getMessage());
                    }
                }

                @Override
                public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                    try {
                        return new String(bytes, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new ZkMarshallingError(e.getMessage());
                    }
                }
            });
//            zkClient.subscribeStateChanges(new IZkStateListener() {
//                @Override
//                public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
//                    log.info("连接状态", state);
//                }
//
//                @Override
//                public void handleNewSession() throws Exception {
//                    log.info("handleNewSession");
//                }
//
//                @Override
//                public void handleSessionEstablishmentError(Throwable error) throws Exception {
//                    log.warn("handleSessionEstablishmentError:", error);
//                }
//            });
        }
        zookeeperToolController.getNodeTreeView().getRoot().getChildren().clear();
        this.addNodeTree("/", zookeeperToolController.getNodeTreeView().getRoot());
        zookeeperToolController.getNodeTreeView().getRoot().setExpanded(true);
    }

    private void addNodeTree(String path, TreeItem<String> treeItem) {
        List<String> list = zkClient.getChildren(path);
        for (String name : list) {
            TreeItem<String> treeItem2 = new TreeItem<>(name);
            treeItem.getChildren().add(treeItem2);
            this.addNodeTree(StringUtils.appendIfMissing(path, "/", "/") + name, treeItem2);
        }
    }

    private String getNodePath(TreeItem<String> selectedItem) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(selectedItem.getValue());
        TreeItem<String> indexItem = selectedItem.getParent();
        while (indexItem != null) {
            stringBuffer.insert(0, StringUtils.appendIfMissing(indexItem.getValue(), "/", "/"));
            indexItem = indexItem.getParent();
        }
        return stringBuffer.toString();
    }

    public void nodeSelectionChanged(TreeItem<String> selectedItem) {
        String nodePath = this.getNodePath(selectedItem);
        if (!zkClient.exists(nodePath)) {
            TooltipUtil.showToast("节点不存在");
            return;
        }
        zookeeperToolController.getNodeDataValueTextArea().setText(zkClient.readData(nodePath));
        Map.Entry<List<ACL>, Stat> aclsEntry = zkClient.getAcl(nodePath);
        Stat stat = aclsEntry.getValue();
        zookeeperToolController.getA_VERSIONTextField().setText("" + stat.getAversion());
        zookeeperToolController.getC_TIMETextField().setText(DateFormatUtils.format(stat.getCtime(), "yyyy-MM-dd'T'HH:mm:ss.SSS z"));
        zookeeperToolController.getC_VERSIONTextField().setText("" + stat.getCversion());
        zookeeperToolController.getCZXIDTextField().setText("0x" + Long.toHexString(stat.getCzxid()));
        zookeeperToolController.getDATA_LENGTHTextField().setText("" + stat.getDataLength());
        zookeeperToolController.getEPHEMERAL_OWNERTextField().setText("0x" + Long.toHexString(stat.getEphemeralOwner()));
        zookeeperToolController.getM_TIMETextField().setText(DateFormatUtils.format(stat.getMtime(), "yyyy-MM-dd'T'HH:mm:ss.SSS z"));
        zookeeperToolController.getMZXIDTextField().setText("0x" + Long.toHexString(stat.getMzxid()));
        zookeeperToolController.getNUM_CHILDRENTextField().setText("" + stat.getNumChildren());
        zookeeperToolController.getPZXIDTextField().setText("0x" + Long.toHexString(stat.getPzxid()));
        zookeeperToolController.getVERSIONTextField().setText("" + stat.getVersion());

        List<ACL> acls = aclsEntry.getKey();
        for (ACL acl : acls) {
            Map<String, String> aclMap = new LinkedHashMap<String, String>();
            zookeeperToolController.getAclSchemeTextField().setText(acl.getId().getScheme());
            zookeeperToolController.getAclIdTextField().setText(acl.getId().getId());
            StringBuilder sb = new StringBuilder();
            int perms = acl.getPerms();
            if ((perms & Perms.READ) == Perms.READ) {
                sb.append("Read");
            }
            if ((perms & Perms.WRITE) == Perms.WRITE) {
                sb.append(", Write");
            }
            if ((perms & Perms.CREATE) == Perms.CREATE) {
                sb.append(", Create");
            }
            if ((perms & Perms.DELETE) == Perms.DELETE) {
                sb.append(", Delete");
            }
            if ((perms & Perms.ADMIN) == Perms.ADMIN) {
                sb.append(", Admin");
            }
            zookeeperToolController.getAclPermissionsTextField().setText(sb.toString());
        }
    }

    public void disconnectOnAction() {
        if (zkClient != null) {
            zkClient.close();
            zkClient = null;
        }
        zookeeperToolController.getNodeTreeView().getRoot().getChildren().clear();
    }

    public void refreshOnAction() {
        if (zkClient == null) {
            TooltipUtil.showToast("zookeeper未连接");
            return;
        }
        connectOnAction();
    }

    public void deleteNodeOnAction() {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.deleteRecursive(nodePath);
        selectedItem.getParent().getChildren().remove(selectedItem);
    }

    public void addNodeOnAction() {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中节点");
            return;
        }
        String nodeName = AlertUtil.showInputAlert("请输入节点名称：");
        if (StringUtils.isEmpty(nodeName)) {
            TooltipUtil.showToast("节点名不能为空！");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.createPersistent(StringUtils.appendIfMissing(nodePath, "/", "/") + nodeName);
        TreeItem<String> treeItem2 = new TreeItem<>(nodeName);
        selectedItem.getChildren().add(treeItem2);
    }

    public void renameNodeOnAction(boolean isCopy) {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中节点");
            return;
        }
        String nodeName = AlertUtil.showInputAlert("请输入节点新名称：");
        if (StringUtils.isEmpty(nodeName)) {
            TooltipUtil.showToast("节点名不能为空！");
            return;
        }
//        String nodePath = this.getNodePath(selectedItem);
        String nodeParent = this.getNodePath(selectedItem.getParent());
        String nodeParentPath = StringUtils.appendIfMissing(nodeParent, "/", "/");
        copyNode(nodeParentPath + selectedItem.getValue(), nodeParentPath + nodeName);
        if (isCopy) {
            TreeItem<String> selectedItem2 = new TreeItem<>(nodeName);
            addNodeTree(nodeParentPath + nodeName, selectedItem2);
            selectedItem.getParent().getChildren().add(selectedItem2);
        } else {
            zkClient.deleteRecursive(nodeParentPath + selectedItem.getValue());
            selectedItem.setValue(nodeName);
        }
    }

    private void copyNode(String path, String copyPath) {
        zkClient.createPersistent(copyPath, zkClient.readData(path), zkClient.getAcl(path).getKey());
        List<String> list = zkClient.getChildren(path);
        for (String name : list) {
            copyNode(StringUtils.appendIfMissing(path, "/", "/") + name, StringUtils.appendIfMissing(copyPath, "/", "/") + name);
        }
    }

    public void nodeDataSaveOnAction() {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.writeData(nodePath, zookeeperToolController.getNodeDataValueTextArea().getText());
    }

    public void addNodeNotify() {//添加节点通知
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        if (childListeners.containsKey(nodePath)) {
            TooltipUtil.showToast("该节点已经添加通知！");
            return;
        }
        IZkChildListener childListener = (parentPath, currentChilds) -> TooltipUtil.showToast("节点Child改变了", "Path:" + parentPath + "\r\n 子节点：" + currentChilds.toString());
        zkClient.subscribeChildChanges(nodePath, childListener);
        childListeners.put(nodePath, childListener);
        IZkDataListener dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                TooltipUtil.showToast("节点Data改变了", "Path:" + dataPath + "\r\n 新数据：" + data.toString());
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                TooltipUtil.showToast("节点删除了", "Path:" + dataPath);
            }
        };
        zkClient.subscribeDataChanges(nodePath, dataListener);
        dataListeners.put(nodePath, dataListener);
        TooltipUtil.showToast("该节点添加通知成功！");
    }

    public void removeNodeNotify() {//移除节点通知
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.unsubscribeChildChanges(nodePath, childListeners.remove(nodePath));
        zkClient.unsubscribeDataChanges(nodePath, dataListeners.remove(nodePath));
        TooltipUtil.showToast("该节点通知成功移除！");
    }
}