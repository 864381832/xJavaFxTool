package com.xwintop.xJavaFxTool.services.developTools.xTransferTool;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolController;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolTaskViewController;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class TransferToolService {
    private TransferToolController transferToolController;
    private Map<String, Map<String, TaskConfig>> taskConfigFileMap = new ConcurrentHashMap<>();//任务对应配置文件map

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
            File[] all = CONFIG_DIR.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith("service.yml")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File file : all) {
                try {
                    Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                    TreeItem<String> treeItem2 = new TreeItem<String>(file.getName());
                    treeItem.getChildren().add(treeItem2);
                    Yaml yaml = new Yaml();
                    Object config = yaml.load(new FileInputStream(file));
                    if (config instanceof List) {
                        Iterable<TaskConfig> taskConfigs = (Iterable<TaskConfig>) config;
                        for (TaskConfig taskConfig : taskConfigs) {
                            TreeItem<String> treeItem3 = new TreeItem<String>(taskConfig.getName());
                            treeItem2.getChildren().add(treeItem3);
                            taskConfigMap.put(taskConfig.getName(), taskConfig);
                        }
                    } else {
                        TaskConfig taskConfig = (TaskConfig) config;
                        TreeItem<String> treeItem3 = new TreeItem<String>(taskConfig.getName());
                        treeItem2.getChildren().add(treeItem3);
                        taskConfigMap.put(taskConfig.getName(), taskConfig);
                    }
                    taskConfigFileMap.put(file.getName(), taskConfigMap);
                } catch (Exception e) {
                    log.error("load config [" + file.getPath() + "] error.", e);
                }
            }
        } else {
            try {
                ChannelSftp channel = this.getSftpChannel();
                TooltipUtil.showToast("连接成功！！！");
                String remotePath = transferToolController.getConfigurationPathTextField().getText();
                remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
                Vector<ChannelSftp.LsEntry> fileList = channel.ls(remotePath);
                for (ChannelSftp.LsEntry file : fileList) {
                    if (file.getFilename().endsWith("service.yml")) {
                        try {
                            Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                            TreeItem<String> treeItem2 = new TreeItem<String>(file.getFilename());
                            treeItem.getChildren().add(treeItem2);
                            Yaml yaml = new Yaml();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            channel.get(remotePath + file.getFilename(), outputStream);
                            Object config = yaml.load(new ByteArrayInputStream(outputStream.toByteArray()));
                            if (config instanceof List) {
                                Iterable<TaskConfig> taskConfigs = (Iterable<TaskConfig>) config;
                                for (TaskConfig taskConfig : taskConfigs) {
                                    TreeItem<String> treeItem3 = new TreeItem<String>(taskConfig.getName());
                                    treeItem2.getChildren().add(treeItem3);
                                    taskConfigMap.put(taskConfig.getName(), taskConfig);
                                }
                            } else {
                                TaskConfig taskConfig = (TaskConfig) config;
                                TreeItem<String> treeItem3 = new TreeItem<String>(taskConfig.getName());
                                treeItem2.getChildren().add(treeItem3);
                                taskConfigMap.put(taskConfig.getName(), taskConfig);
                            }
                            taskConfigFileMap.put(file.getFilename(), taskConfigMap);
                        } catch (Exception e) {
                            log.error("load config [" + file.getFilename() + "] error.", e);
                        }
                    }
                }
                this.closeSftpSession(channel);
            } catch (Exception e) {
                log.error("连接错误：", e);
            }
        }
    }

    public ChannelSftp getSftpChannel() throws Exception {
        JSch jSch = new JSch(); //创建JSch对象
        Session session = jSch.getSession(transferToolController.getUsernameTextField().getText(), transferToolController.getHostTextField().getText(), Integer.valueOf(transferToolController.getPortTextField().getText()));//根据用户名，主机ip和端口获取一个Session对象
        session.setPassword(transferToolController.getPasswordTextField().getText());//设置密码
        Properties sftpConfig = new Properties();
        sftpConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sftpConfig);//为Session对象设置properties
        session.connect();//通过Session建立连接
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }

    public void closeSftpSession(Channel channel) {
        try {
            channel.disconnect();
            channel.getSession().disconnect();
        } catch (Exception e) {
            log.error("关闭连接失败：", e);
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
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                List<Tab> tabList = new ArrayList<Tab>();
                transferToolController.getTaskConfigTabPane().getTabs().forEach((Tab tab2) -> {
                    if (tab2.getText().startsWith(tabName)) {
                        tabList.add(tab2);
                    }
                });
                transferToolController.getTaskConfigTabPane().getTabs().removeAll(tabList);
                taskConfigTabMap.remove(tab.getText());
            }
        });
        FXMLLoader fXMLLoader = TransferToolTaskViewController.getFXMLLoader();
        try {
            tab.setContent(fXMLLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TransferToolTaskViewController transferToolTaskViewController = fXMLLoader.getController();
        transferToolTaskViewController.setData(transferToolController, taskConfigFileMap.get(fileName).get(taskName));
        transferToolTaskViewController.setFileName(fileName);
        transferToolTaskViewController.setTabName(tabName);
        transferToolController.getTaskConfigTabPane().getTabs().add(tab);
        transferToolController.getTaskConfigTabPane().getSelectionModel().select(tab);
        taskConfigTabMap.put(tabName, tab);
    }

}