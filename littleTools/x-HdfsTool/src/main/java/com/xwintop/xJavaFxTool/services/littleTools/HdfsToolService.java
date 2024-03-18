package com.xwintop.xJavaFxTool.services.littleTools;

import cn.hutool.core.bean.BeanUtil;
import com.xwintop.xJavaFxTool.controller.littleTools.HdfsToolController;
import com.xwintop.xJavaFxTool.utils.HdfsUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

@Getter
@Setter
@Slf4j
public class HdfsToolService {
    private HdfsToolController hdfsToolController;

    private HdfsUtil hdfsUtil;

    public HdfsToolService(HdfsToolController hdfsToolController) {
        this.hdfsToolController = hdfsToolController;
    }

    public void searchContentAction() throws Exception {
//        String path = hdfsToolController.getSearchDirectoryTextField().getText().trim();
//        if (hdfsToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex() == 1) {
//        } else if (hdfsToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex() == 2) {
//        }
//        long fileSizeFrom = hdfsToolController.getFileSizeFromSpinner().getValue();
//        long fileSizeTo = hdfsToolController.getFileSizeToSpinner().getValue();
//        if (fileSizeFrom > 0 || fileSizeTo > 0) {
//            if (fileSizeFrom > 0) {
//                fileSizeFrom = getFileSize(hdfsToolController.getFileSizeFromChoiceBox().getSelectionModel().getSelectedIndex(), fileSizeFrom);
//            } else {
//                fileSizeFrom = -1;
//            }
//            if (fileSizeTo > 0) {
//                fileSizeTo = getFileSize(hdfsToolController.getFileSizeToChoiceBox().getSelectionModel().getSelectedIndex(), fileSizeTo);
//            } else {
//                fileSizeTo = Long.MAX_VALUE;
//            }
//        }

        Configuration hadoopConf = new Configuration();
        if (StringUtils.isNotEmpty(hdfsToolController.getKerberosTextField().getText())) {
            System.setProperty("java.security.krb5.conf", hdfsToolController.getKerberosTextField().getText());//配置krb5.conf的路径
            hadoopConf.set("hadoop.security.authentication", "kerberos"); //配置认证方式，有kerberos和simple两种
        }
        hadoopConf.set("fs.defaultFS", "hdfs://" + hdfsToolController.getHdfsUrlTextField().getText());
        if (StringUtils.isNotEmpty(hdfsToolController.getUserNameTextField().getText())) {
            System.setProperty("HADOOP_USER_NAME", hdfsToolController.getUserNameTextField().getText());
        }
        if (!hdfsToolController.getSystemConfTableData().isEmpty()) {
            for (Map<String, String> systemConfTableDatum : hdfsToolController.getSystemConfTableData()) {
                System.setProperty(systemConfTableDatum.get("systemConfKey"), systemConfTableDatum.get("systemConfValue"));
            }
        }
        if (!hdfsToolController.getHadoopConfTableData().isEmpty()) {
            for (Map<String, String> hadoopConfTableDatum : hdfsToolController.getHadoopConfTableData()) {
                hadoopConf.set(hadoopConfTableDatum.get("hadoopConfKey"), hadoopConfTableDatum.get("hadoopConfValue"));
            }
        }
        if (hdfsUtil != null) {
            hdfsUtil.close();
        }
        hdfsUtil = new HdfsUtil(hadoopConf);
        hdfsToolController.getHdfsListTreeView().getRoot().getChildren().clear();
        addNodeTree("/", hdfsToolController.getHdfsListTreeView().getRoot());
    }

    public void closeHdfs() {
        if (hdfsUtil != null) {
            try {
                hdfsUtil.close();
            } catch (Exception e) {
                log.error("hdfs关闭失败", e);
            }
        }
    }

    private void addNodeTree(String path, TreeItem<Map<String, Object>> treeItem) {
        try {
            hdfsToolController.getSearchResultTableData().clear();
            hdfsToolController.getSearchResultTableView().setId(path);
            FileStatus[] fs = hdfsUtil.getFileList(path);
            for (FileStatus f : fs) {
                String fileName = f.getPath().getName();
                TreeItem<Map<String, Object>> treeItem2 = new TreeItem<>(getTreeItemMap(fileName));
                if (f.isDirectory()) {
                    treeItem2.setGraphic(BeanUtil.toBean(hdfsToolController.getDirectorySvgGlyph(), Node.class));
                } else {
                    treeItem2.setGraphic(BeanUtil.toBean(hdfsToolController.getFileSvgGlyph(), Node.class));
                }
                treeItem2.getValue().put("fileSize", f.getLen());
                treeItem.getChildren().add(treeItem2);
                Map map = new HashMap();
                map.put("fileName", fileName);
                map.put("absolutePath", f.getPath().toUri().getPath());
                map.put("fileSize", (int) Math.ceil(f.getLen() / 1024.0) + "KB");
                map.put("lastModified", new Date(f.getModificationTime()).toLocaleString());
                map.put("isDirectory", "" + f.isDirectory());
                hdfsToolController.getSearchResultTableData().add(map);
            }
        } catch (Exception e) {
            log.error("加载节点失败：", e);
            TooltipUtil.showToast("加载节点失败：" + e.getMessage());
        }
    }

    private long getFileSize(int fileSizeType, long fileSize) {
        if (fileSizeType == 0) {
        } else if (fileSizeType == 1) {
            fileSize = fileSize * 1024;
        } else if (fileSizeType == 2) {
            fileSize = fileSize * 1024 * 1024;
        } else if (fileSizeType == 3) {
            fileSize = fileSize * 1024 * 1024 * 1024;
        }
        return fileSize;
    }

    private String getNodePath(TreeItem<Map<String, Object>> selectedItem) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(selectedItem.getValue());
        TreeItem<Map<String, Object>> indexItem = selectedItem.getParent();
        while (indexItem != null) {
            stringBuffer.insert(0, StringUtils.appendIfMissing(indexItem.getValue().toString(), "/", "/"));
            indexItem = indexItem.getParent();
        }
        return stringBuffer.toString();
    }

    public void nodeSelectionChanged(TreeItem<Map<String, Object>> selectedItem) throws Exception {
        String nodePath = this.getNodePath(selectedItem);
        if (!hdfsUtil.exists(nodePath)) {
            TooltipUtil.showToast("节点不存在");
            return;
        }
        if ("folder".equals(selectedItem.getGraphic().getId())) {
            hdfsToolController.getSearchResultTableView().setVisible(true);
            hdfsToolController.getFileContentTextArea().setVisible(false);
            selectedItem.getChildren().clear();
            this.addNodeTree(nodePath, selectedItem);
        } else {
            hdfsToolController.getSearchResultTableView().setVisible(false);
            hdfsToolController.getFileContentTextArea().setVisible(true);
            if ((long) selectedItem.getValue().get("fileSize") > 4194304) {
                boolean isOk = AlertUtil.confirmOkCancel("提示", "文件内容大于4m，确定要预览吗？");
                if (!isOk) {
                    return;
                }
            }
            hdfsToolController.getFileContentTextArea().setText(new String(hdfsUtil.getFile(nodePath)));
        }
    }

    public void addDirOnAction(TreeItem<Map<String, Object>> selectedItem) {
        String addDir = AlertUtil.showInputAlertDefaultValue("新增文件夹名称", null);
        if (StringUtils.isEmpty(addDir)) {
            TooltipUtil.showToast("文件夹不能为空");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        try {
            if (hdfsUtil.mkdirs(StringUtils.appendIfMissing(nodePath, "/", "/") + addDir)) {
                TooltipUtil.showToast("添加文件夹成功");
                this.nodeSelectionChanged(selectedItem);
            } else {
                TooltipUtil.showToast("添加文件夹失败");
            }
        } catch (Exception e) {
            TooltipUtil.showToast("添加文件夹失败" + e.getMessage());
            log.error("添加文件夹失败：", e);
        }
    }

    public void addFileOnAction(TreeItem<Map<String, Object>> selectedItem) {
        File file = FileChooserUtil.chooseFile();
        if (file == null) {
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        try {
            hdfsUtil.uploadFile(StringUtils.appendIfMissing(nodePath, "/", "/") + file.getName(), file);
            TooltipUtil.showToast("添加文件成功");
            this.nodeSelectionChanged(selectedItem);
        } catch (Exception e) {
            TooltipUtil.showToast("添加文件失败" + e.getMessage());
            log.error("添加文件失败：", e);
        }
    }

    public void addFileOnAction(List<File> files, TreeItem<Map<String, Object>> selectedItem) {
        if (selectedItem == null) {
            selectedItem = hdfsToolController.getHdfsListTreeView().getRoot();
        }
        String nodePath = this.getNodePath(selectedItem);
        try {
            for (File file : files) {
                if (file.isFile()) {
                    hdfsUtil.uploadFile(StringUtils.appendIfMissing(nodePath, "/", "/") + file.getName(), file);
                } else {
                    hdfsUtil.uploadDir(StringUtils.appendIfMissing(nodePath, "/", "/") + file.getName(), file);
                }
                TooltipUtil.showToast("添加文件成功");
            }
            this.nodeSelectionChanged(selectedItem);
        } catch (Exception e) {
            TooltipUtil.showToast("添加文件失败" + e.getMessage());
            log.error("添加文件失败：", e);
        }
    }

    public void addFileOnAction(List<File> files) {
        String nodePath = hdfsToolController.getSearchResultTableView().getId();
        try {
            for (File file : files) {
                String absolutePath = StringUtils.appendIfMissing(nodePath, "/", "/") + file.getName();
                if (file.isFile()) {
                    hdfsUtil.uploadFile(absolutePath, file);
                } else {
                    hdfsUtil.uploadDir(absolutePath, file);
                }
                TooltipUtil.showToast("添加文件成功");
            }
            hdfsToolController.getSearchResultTableData().clear();
            FileStatus[] fs = hdfsUtil.getFileList(nodePath);
            for (FileStatus f : fs) {
                String fileName = f.getPath().getName();
                Map map = new HashMap();
                map.put("fileName", fileName);
                map.put("absolutePath", f.getPath().toUri().getPath());
                map.put("fileSize", (int) Math.ceil(f.getLen() / 1024.0) + "KB");
                map.put("lastModified", new Date(f.getModificationTime()).toLocaleString());
                map.put("isDirectory", "" + f.isDirectory());
                hdfsToolController.getSearchResultTableData().add(map);
            }
        } catch (Exception e) {
            TooltipUtil.showToast("添加文件失败" + e.getMessage());
            log.error("添加文件失败：", e);
        }
    }

    public void renameNodeOnAction(TreeItem<Map<String, Object>> selectedItem) {
        String addDir = AlertUtil.showInputAlertDefaultValue("新文件夹名称", selectedItem.getValue().toString());
        if (StringUtils.isEmpty(addDir)) {
            TooltipUtil.showToast("文件夹不能为空");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        String nodeParentPath = this.getNodePath(selectedItem.getParent());
        try {
            if (hdfsUtil.rename(nodePath, StringUtils.appendIfMissing(nodeParentPath, "/", "/") + addDir)) {
                TooltipUtil.showToast("重命名文件夹成功");
                selectedItem.setValue(HdfsToolService.getTreeItemMap(addDir));
            } else {
                TooltipUtil.showToast("重命名文件夹失败");
            }
        } catch (Exception e) {
            TooltipUtil.showToast("重命名文件夹失败" + e.getMessage());
            log.error("重命名文件夹失败：", e);
        }
    }

    public void downloadFileOnAction(TreeItem<Map<String, Object>> selectedItem) {
        File file = FileChooserUtil.chooseSaveFile(selectedItem.getValue().toString());
        if (file == null) {
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        try {
            hdfsUtil.downloadFile(nodePath, file.getAbsolutePath());
            TooltipUtil.showToast("下载文件成功");
            this.nodeSelectionChanged(selectedItem);
        } catch (Exception e) {
            TooltipUtil.showToast("下载文件失败" + e.getMessage());
            log.error("下载文件失败：", e);
        }
    }

    public void dragDownloadFile() {
//        String nodePath = hdfsToolController.getSearchResultTableView().getId();
        try {
            List<File> files = new ArrayList<>();
            File localFileTmp = new File("./hdfsFileTmp");
            localFileTmp.mkdirs();
            String localFileTmpStr = localFileTmp.getCanonicalPath();
            for (Map<String, String> selectedItem : hdfsToolController.getSearchResultTableView().getSelectionModel().getSelectedItems()) {
                File localFile = new File(localFileTmpStr, selectedItem.get("fileName"));
                hdfsUtil.downloadFile(selectedItem.get("absolutePath"), localFile.getAbsolutePath());
                files.add(localFile);
            }
            Dragboard db = hdfsToolController.getSearchResultTableView().startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putFiles(files);
            db.setContent(content);
        } catch (Exception e) {
            log.error("下载文件失败：", e);
        }
    }

    public void deleteNodeOnAction(TreeItem<Map<String, Object>> selectedItem) {
        boolean isOk = AlertUtil.confirmOkCancel("提示", "确定要删除吗？");
        if (!isOk) {
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        try {
            if (hdfsUtil.deleteFile(nodePath)) {
                TooltipUtil.showToast("删除文件夹成功");
                this.nodeSelectionChanged(selectedItem.getParent());
            } else {
                TooltipUtil.showToast("删除文件夹失败");
            }
        } catch (Exception e) {
            TooltipUtil.showToast("删除文件夹失败" + e.getMessage());
            log.error("删除文件夹失败：", e);
        }
    }

    public static Map<String, Object> getTreeItemMap(String value) {
        Map<String, Object> map = new HashMap<String, Object>() {
            @Override
            public String toString() {
                return this.get("value").toString();
            }
        };
        map.put("value", value);
        return map;
    }

    //获取文件图标
    public static ImageView getFileIconImage(File file) {
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
        BufferedImage bufferedImage = new BufferedImage(
            icon.getIconWidth(),
            icon.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
        return new ImageView(fxImage);
    }
}