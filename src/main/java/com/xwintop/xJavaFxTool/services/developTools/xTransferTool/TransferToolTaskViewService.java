package com.xwintop.xJavaFxTool.services.developTools.xTransferTool;

import com.jcraft.jsch.ChannelSftp;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolController;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolServiceViewController;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolTaskViewController;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class TransferToolTaskViewService {
    private TransferToolTaskViewController transferToolTaskViewController;

    private TransferToolController transferToolController;

    private Map<String, Tab> serviceViewTabMap = new HashMap<>();

    public TransferToolTaskViewService(TransferToolTaskViewController transferToolTaskViewController) {
        this.transferToolTaskViewController = transferToolTaskViewController;
    }

    public void addServiceViewTabPane(Object configObject, int index) {
        String tabName = null;
        if (configObject instanceof ReceiverConfig) {
            tabName = ((ReceiverConfig) configObject).getServiceName();
        } else if (configObject instanceof FilterConfig) {
            tabName = ((FilterConfig) configObject).getServiceName();
        } else if (configObject instanceof SenderConfig) {
            tabName = ((SenderConfig) configObject).getServiceName();
        }
        tabName = tabName + index;
        Tab tab1 = serviceViewTabMap.get(tabName);
        if (tab1 != null) {
            transferToolTaskViewController.getServiceViewTabPane().getSelectionModel().select(tab1);
            return;
        }
        final Tab tab = new Tab(tabName);
        tab.setOnClosed(event -> {
            List<Tab> tabList = new ArrayList<Tab>();
            transferToolTaskViewController.getServiceViewTabPane().getTabs().forEach((Tab tab2) -> {
                if (tab2.getText().startsWith(tab.getText())) {
                    tabList.add(tab2);
                }
            });
            transferToolTaskViewController.getServiceViewTabPane().getTabs().removeAll(tabList);
            serviceViewTabMap.remove(tab.getText());
        });
        FXMLLoader fXMLLoader = TransferToolServiceViewController.getFXMLLoader();
        try {
            tab.setContent(fXMLLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TransferToolServiceViewController transferToolServiceViewController = fXMLLoader.getController();
        transferToolServiceViewController.setData(transferToolTaskViewController, configObject);
        transferToolServiceViewController.setTabName(tabName);
        transferToolTaskViewController.getServiceViewTabPane().getTabs().add(tab);
        transferToolTaskViewController.getServiceViewTabPane().getSelectionModel().select(tab);
        serviceViewTabMap.put(tabName, tab);
    }

    public void saveTaskConfigAction() throws Exception {
        TaskConfig taskConfig = transferToolTaskViewController.getTaskConfig();
        String fileName = transferToolTaskViewController.getFileName();
        String taskConfigName = transferToolTaskViewController.getNameTextField().getText();
        Map<String, TaskConfig> taskConfigMap = transferToolController.getTransferToolService().getTaskConfigFileMap().get(fileName);
        if (!taskConfig.getName().equals(taskConfigName)) {
            String oldTaskConfigName = taskConfig.getName();
            taskConfig.setName(taskConfigName);
            transferToolTaskViewController.setTabName(taskConfigName);
            transferToolController.getTransferToolService().getTaskConfigTabMap().get(oldTaskConfigName).setText(taskConfigName);
            taskConfigMap.put(taskConfigName, taskConfigMap.get(oldTaskConfigName));
            taskConfigMap.remove(oldTaskConfigName);
            transferToolController.getConfigurationTreeView().getRoot().getChildren().forEach(stringTreeItem -> {
                if (fileName.equals(stringTreeItem.getValue())) {
                    stringTreeItem.getChildren().forEach(stringTreeItem1 -> {
                        if (oldTaskConfigName.equals(stringTreeItem1.getValue())) {
                            stringTreeItem1.setValue(taskConfigName);
                        }
                    });
                }
            });
        }
        taskConfig.setIsEnable(transferToolTaskViewController.getIsEnableCheckBox().isSelected());
        String taskType = transferToolTaskViewController.getTaskTypeTextField().getText();
        taskConfig.setTaskType(StringUtils.isBlank(taskType) ? null : taskType);
        taskConfig.setTriggerType(transferToolTaskViewController.getTriggerTypeChoiceBox().getValue());
        taskConfig.setIntervalTime(transferToolTaskViewController.getIntervalTimeSpinner().getValue());
        taskConfig.setExecuteTimes(transferToolTaskViewController.getExecuteTimesSpinner().getValue());
        String triggerCron = transferToolTaskViewController.getTriggerCronTextField().getText();
        taskConfig.setTriggerCron(StringUtils.isBlank(triggerCron) ? null : triggerCron);
        taskConfig.setIsStatefulJob(transferToolTaskViewController.getIsStatefullJobCheckBox().isSelected());
        taskConfig.getProperties().clear();
        transferToolTaskViewController.getPropertiesTableData().forEach(map -> {
            taskConfig.getProperties().put(map.get("key"), map.get("value"));
        });
        if ("127.0.0.1".equals(transferToolController.getHostTextField().getText())) {
            File file = new File(transferToolController.getConfigurationPathTextField().getText(), fileName);
            Yaml yaml = new Yaml();
            Writer writer = new FileWriter(file);
            yaml.dump(taskConfigMap.values().toArray(), writer);
            writer.close();
        } else {
            Yaml yaml = new Yaml();
            byte[] configBytes = yaml.dump(taskConfigMap.values().toArray()).getBytes();
            ChannelSftp channel = transferToolController.getTransferToolService().getSftpChannel();
            String remotePath = transferToolController.getConfigurationPathTextField().getText();
            remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
            channel.put(new ByteArrayInputStream(configBytes), remotePath + fileName);
            transferToolController.getTransferToolService().closeSftpSession(channel);
        }
    }
}