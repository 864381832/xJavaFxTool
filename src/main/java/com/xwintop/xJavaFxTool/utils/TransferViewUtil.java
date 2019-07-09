package com.xwintop.xJavaFxTool.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolController;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolService;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TransferViewUtil {
    public static String getYamlString(String flowStyle, Object object) {
        DumperOptions dumperOptions = new DumperOptions();
        if ("FLOW".equals(flowStyle)) {
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        } else if ("BLOCK".equals(flowStyle)) {
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        }
        Yaml yaml = new Yaml(dumperOptions);
        return yaml.dump(object);
    }

    //保存配置
    public static String saveConfig(TransferToolController transferToolController, String fileName, Object object) throws Exception {
        String configYamlString = TransferViewUtil.getYamlString(transferToolController.getFlowStyleChoiceBox().getValue().toString(), object);
        TransferViewUtil.saveConfigStringToFile(transferToolController, fileName, configYamlString);
        return configYamlString;
    }

    public static void saveConfigStringToFile(TransferToolController transferToolController, String fileName, String configYamlString) throws Exception {
        if ("127.0.0.1".equals(transferToolController.getHostTextField().getText())) {
            File file = new File(transferToolController.getConfigurationPathTextField().getText(), fileName);
            FileUtils.writeStringToFile(file, configYamlString, "utf-8");
        } else {
            byte[] configBytes = configYamlString.getBytes("utf-8");
            ChannelSftp channel = TransferViewUtil.getSftpChannel(transferToolController);
            String remotePath = transferToolController.getConfigurationPathTextField().getText();
            remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
            channel.put(new ByteArrayInputStream(configBytes), remotePath + fileName);
            TransferViewUtil.closeSftpSession(channel);
        }
    }

    //加载配置
    public static void readConfig(TransferToolService transferToolService, TreeItem<String> treeItem, String fileName, String yamlFileString) {
        TreeItem<String> treeItem2 = new TreeItem<String>(fileName);
        treeItem.getChildren().add(treeItem2);
        if (fileName.endsWith("service.yml")) {
            Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
            Yaml yaml = new Yaml();
            Object config = yaml.load(yamlFileString);
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
            transferToolService.getTaskConfigFileMap().put(fileName, taskConfigMap);
        } else if (fileName.endsWith("datasource.yml")) {
            Map<String, DataSourceConfigDruid> dataSourceConfigMap = new ConcurrentHashMap<>();
            Yaml yaml = new Yaml();
            Object config = yaml.load(yamlFileString);
            if (config instanceof List) {
                Iterable<DataSourceConfigDruid> taskConfigs = (Iterable<DataSourceConfigDruid>) config;
                for (DataSourceConfigDruid taskConfig : taskConfigs) {
                    TreeItem<String> treeItem3 = new TreeItem<String>(taskConfig.getId());
                    treeItem2.getChildren().add(treeItem3);
                    dataSourceConfigMap.put(taskConfig.getId(), taskConfig);
                }
            } else {
                DataSourceConfigDruid taskConfig = (DataSourceConfigDruid) config;
                TreeItem<String> treeItem3 = new TreeItem<String>(taskConfig.getId());
                treeItem2.getChildren().add(treeItem3);
                dataSourceConfigMap.put(taskConfig.getId(), taskConfig);
            }
            transferToolService.getDataSourceConfigFileMap().put(fileName, dataSourceConfigMap);
        }
    }

    public static ChannelSftp getSftpChannel(TransferToolController transferToolController) throws Exception {
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

    public static void closeSftpSession(Channel channel) {
        try {
            channel.disconnect();
            channel.getSession().disconnect();
        } catch (Exception e) {
            log.error("关闭连接失败：", e);
        }
    }
}
