package com.xwintop.xJavaFxTool.services.developTools.xTransferTool;

import com.jcraft.jsch.ChannelSftp;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolController;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolDataSourceController;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolTaskViewController;
import com.xwintop.xJavaFxTool.utils.TransferViewUtil;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class TransferToolService {
    private TransferToolController transferToolController;
    private Map<String, Map<String, TaskConfig>> taskConfigFileMap = new ConcurrentHashMap<>();//任务对应配置文件map
    private Map<String, Map<String, DataSourceConfigDruid>> dataSourceConfigFileMap = new ConcurrentHashMap<>();//数据源对应配置文件map
    private Map<String, String> taskConfigFileStringMap = new ConcurrentHashMap<>();//任务对应文件内容

    private Map<String, Tab> taskConfigTabMap = new HashMap<String, Tab>();

    public TransferToolService(TransferToolController transferToolController) {
        this.transferToolController = transferToolController;
    }

    public void reloadTaskConfigFile() throws Exception {
        if (StringUtils.isBlank(transferToolController.getConfigurationPathTextField().getText())) {
            TooltipUtil.showToast("配置目录不能为空！");
            return;
        }
        TreeItem<String> treeItem = transferToolController.getConfigurationTreeView().getRoot();
        treeItem.getChildren().clear();
        if ("127.0.0.1".equals(transferToolController.getHostTextField().getText())) {
            File CONFIG_DIR = new File(transferToolController.getConfigurationPathTextField().getText());
            File[] all = CONFIG_DIR.listFiles();
            for (File file : all) {
                try {
                    if (!file.isFile()) {
                        continue;
                    }
                    String yamlFileString = FileUtils.readFileToString(file, "UTF-8");
                    TransferViewUtil.readConfig(this, treeItem, file.getName(), yamlFileString);
                    taskConfigFileStringMap.put(file.getName(), yamlFileString);
                } catch (Exception e) {
                    log.error("load config [" + file.getPath() + "] error.", e);
                }
            }
            TooltipUtil.showToast("连接成功！！！");
        } else {
            try {
                ChannelSftp channel = TransferViewUtil.getSftpChannel(transferToolController);
                String remotePath = transferToolController.getConfigurationPathTextField().getText();
                remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
                Vector<ChannelSftp.LsEntry> fileList = channel.ls(remotePath);
                for (ChannelSftp.LsEntry file : fileList) {
                    if (file.getAttrs().isDir() || ".".equals(file.getFilename()) || "..".equals(file.getFilename())) {
                        continue;
                    }
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    channel.get(remotePath + file.getFilename(), outputStream);
                    String yamlFileString = outputStream.toString("utf-8");
                    TransferViewUtil.readConfig(this, treeItem, file.getFilename(), yamlFileString);
                    taskConfigFileStringMap.put(file.getFilename(), yamlFileString);
                }
                TransferViewUtil.closeSftpSession(channel);
                TooltipUtil.showToast("连接成功！！！");
            } catch (Exception e) {
                log.error("连接错误：", e);
                TooltipUtil.showToast("连接失败！！！" + e.getMessage());
            }
        }
    }

    public void addTaskConfigTabPane(String fileName, String taskName) {
        String tabName = taskName;
        Tab tab1 = taskConfigTabMap.get(tabName);
        if (tab1 != null) {
            transferToolController.getTaskConfigTabPane().getSelectionModel().select(tab1);
            return;
        }
        final Tab tab = new Tab(tabName);
        tab.setOnClosed(event -> taskConfigTabMap.remove(tab.getText()));
        if (fileName.endsWith("service.yml")) {
            FXMLLoader fXMLLoader = TransferToolTaskViewController.getFXMLLoader();
            try {
                tab.setContent(fXMLLoader.load());
            } catch (IOException e) {
                log.error("加载失败", e);
            }
            TransferToolTaskViewController transferToolTaskViewController = fXMLLoader.getController();
            transferToolTaskViewController.setData(transferToolController, taskConfigFileMap.get(fileName).get(taskName));
            transferToolTaskViewController.setFileName(fileName);
            transferToolTaskViewController.setTabName(tabName);
        } else if (fileName.endsWith("datasource.yml")) {
            FXMLLoader fXMLLoader = TransferToolDataSourceController.getFXMLLoader();
            try {
                tab.setContent(fXMLLoader.load());
            } catch (IOException e) {
                log.error("加载失败", e);
            }
            TransferToolDataSourceController transferToolDataSourceController = fXMLLoader.getController();
            transferToolDataSourceController.setData(transferToolController, dataSourceConfigFileMap.get(fileName).get(taskName));
            transferToolDataSourceController.setFileName(fileName);
            transferToolDataSourceController.setTabName(tabName);
        }
        transferToolController.getTaskConfigTabPane().getTabs().add(tab);
        transferToolController.getTaskConfigTabPane().getSelectionModel().select(tab);
        taskConfigTabMap.put(tabName, tab);
    }

    public void addTaskFileTextArea(String fileName, String value) {
        Tab tab = new Tab(fileName);
        TextArea textArea = new TextArea();
        textArea.setText(value);
        tab.setContent(textArea);
        textArea.setFocusTraversable(true);
        textArea.setOnKeyPressed(event -> {
            if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                TransferToolService.this.saveFileAction(fileName, textArea.getText().trim());
            }
        });
        MenuItem menu_SaveFile = new MenuItem("保存文件");
        menu_SaveFile.setOnAction(event1 -> {
            TransferToolService.this.saveFileAction(fileName, textArea.getText().trim());
        });
        ContextMenu contextMenu = new ContextMenu(menu_SaveFile);
        textArea.setContextMenu(contextMenu);
        transferToolController.getTaskConfigTabPane().getTabs().add(tab);
        transferToolController.getTaskConfigTabPane().getSelectionModel().select(tab);
    }

    private void saveFileAction(String fileName, String yamlFileString) {
        try {
            if (fileName.endsWith("service.yml")) {
                Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                Object config = new Yaml().load(yamlFileString);
                if (config instanceof List) {
                    Iterable<TaskConfig> taskConfigs = (Iterable<TaskConfig>) config;
                    for (TaskConfig taskConfig : taskConfigs) {
                        taskConfigMap.put(taskConfig.getName(), taskConfig);
                    }
                } else {
                    TaskConfig taskConfig = (TaskConfig) config;
                    taskConfigMap.put(taskConfig.getName(), taskConfig);
                }
                taskConfigFileMap.put(fileName, taskConfigMap);
            } else if (fileName.endsWith("datasource.yml")) {
                Map<String, DataSourceConfigDruid> dataSourceConfigMap = new ConcurrentHashMap<>();
                Object config = new Yaml().load(yamlFileString);
                if (config instanceof List) {
                    Iterable<DataSourceConfigDruid> taskConfigs = (Iterable<DataSourceConfigDruid>) config;
                    for (DataSourceConfigDruid taskConfig : taskConfigs) {
                        dataSourceConfigMap.put(taskConfig.getId(), taskConfig);
                    }
                } else {
                    DataSourceConfigDruid taskConfig = (DataSourceConfigDruid) config;
                    dataSourceConfigMap.put(taskConfig.getId(), taskConfig);
                }
                dataSourceConfigFileMap.put(fileName, dataSourceConfigMap);
            }
            TransferViewUtil.saveConfigStringToFile(transferToolController, fileName, yamlFileString);
            taskConfigFileStringMap.put(fileName, yamlFileString);
            TooltipUtil.showToast("保存成功！");
        } catch (Exception e) {
            TooltipUtil.showToast("保存失败！" + e.getMessage());
            log.error("保存失败", e);
        }
    }

    public void removeTaskConfigTabPane(String taskName) {
        Tab tab1 = taskConfigTabMap.get(taskName);
        if (tab1 != null) {
            transferToolController.getTaskConfigTabPane().getTabs().remove(tab1);
        }
    }

}