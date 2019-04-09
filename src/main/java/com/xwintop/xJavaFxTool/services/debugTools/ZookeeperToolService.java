package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.ZookeeperToolController;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;
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

    public ZookeeperToolService(ZookeeperToolController zookeeperToolController) {
        this.zookeeperToolController = zookeeperToolController;
    }

    public void connectOnAction() {
        if (zkClient == null) {
            zkClient = new ZkClient(zookeeperToolController.getZkServersTextField().getText().trim());
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
        }
        zookeeperToolController.getNodeTreeView().getRoot().getChildren().clear();
        this.addNodeTree("/", zookeeperToolController.getNodeTreeView().getRoot());
        zookeeperToolController.getNodeTreeView().getRoot().setExpanded(true);
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
            TooltipUtil.showToast("结点不存在");
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
            TooltipUtil.showToast("未选中结点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        if (selectedItem.getChildren().size() > 0) {
            zkClient.deleteRecursive(nodePath);
        }
        zkClient.delete(nodePath);
        selectedItem.getParent().getChildren().remove(selectedItem);
    }

    public void addNodeOnAction() {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中结点");
            return;
        }
        String nodeName = AlertUtil.showInputAlert("请输入结点名称：");
        String nodePath = this.getNodePath(selectedItem);
        zkClient.createPersistent(StringUtils.appendIfMissing(nodePath, "/", "/") + nodeName);
        TreeItem<String> treeItem2 = new TreeItem<>(nodeName);
        selectedItem.getChildren().add(treeItem2);
    }

    public void renameNodeOnAction() {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中结点");
            return;
        }
        String nodeName = AlertUtil.showInputAlert("请输入结点新名称：");
        String nodePath = this.getNodePath(selectedItem);
        zkClient.createPersistent(StringUtils.appendIfMissing(nodePath, "/", "/") + nodeName);
        TreeItem<String> treeItem2 = new TreeItem<>(nodeName);
        selectedItem.getChildren().add(treeItem2);
    }

    public void nodeDataSaveOnAction() {
        TreeItem<String> selectedItem = zookeeperToolController.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            TooltipUtil.showToast("未选中结点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.writeData(nodePath, zookeeperToolController.getNodeDataValueTextArea().getText());
    }
}