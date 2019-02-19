package com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool;

import com.easipass.gateway.entity.TaskConfig;
import com.easipass.gateway.filter.bean.FilterConfig;
import com.easipass.gateway.receiver.entity.ReceiverConfig;
import com.easipass.gateway.route.entity.SenderConfig;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolController;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolServiceViewController;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolTaskViewController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@Getter
@Setter
@Slf4j
public class GatewayConfToolTaskViewService {
    private GatewayConfToolTaskViewController gatewayConfToolTaskViewController;

    private GatewayConfToolController gatewayConfToolController;

    private Map<String, Tab> serviceViewTabMap = new HashMap<String, Tab>();

    public GatewayConfToolTaskViewService(GatewayConfToolTaskViewController gatewayConfToolTaskViewController) {
        this.gatewayConfToolTaskViewController = gatewayConfToolTaskViewController;
    }

    public void addServiceViewTabPane(Object configObject, int index) {
//    public void addServiceViewTabPane(String serviceName, int index) {
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
//            gatewayConfToolTaskViewController.getGatewayConfToolController().getServiceViewTabPane().getSelectionModel().select(tab1);
            gatewayConfToolTaskViewController.getServiceViewTabPane().getSelectionModel().select(tab1);
            return;
        }
        final Tab tab = new Tab(tabName);
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                List<Tab> tabList = new ArrayList<Tab>();
                gatewayConfToolTaskViewController.getServiceViewTabPane().getTabs().forEach((Tab tab2) -> {
                    if (tab2.getText().startsWith(tab.getText())) {
                        tabList.add(tab2);
                    }
                });
                gatewayConfToolTaskViewController.getServiceViewTabPane().getTabs().removeAll(tabList);
                serviceViewTabMap.remove(tab.getText());
            }
        });
        FXMLLoader fXMLLoader = GatewayConfToolServiceViewController.getFXMLLoader();
        try {
            tab.setContent(fXMLLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        GatewayConfToolServiceViewController gatewayConfToolServiceViewController = fXMLLoader.getController();
        gatewayConfToolServiceViewController.setData(gatewayConfToolTaskViewController, configObject);
        gatewayConfToolServiceViewController.setTabName(tabName);
        gatewayConfToolTaskViewController.getServiceViewTabPane().getTabs().add(tab);
        gatewayConfToolTaskViewController.getServiceViewTabPane().getSelectionModel().select(tab);
        serviceViewTabMap.put(tabName, tab);
    }

    public void saveTaskConfigAction() throws Exception {
        TaskConfig taskConfig = gatewayConfToolTaskViewController.getTaskConfig();
        String fileName = gatewayConfToolTaskViewController.getFileName();
        String taskConfigName = gatewayConfToolTaskViewController.getNameTextField().getText();
        if (!taskConfig.getName().equals(taskConfigName)) {
            String oldTaskConfigName = taskConfig.getName();
            taskConfig.setName(taskConfigName);
            gatewayConfToolTaskViewController.setTabName(taskConfigName);
            gatewayConfToolController.getGatewayConfToolService().getTaskConfigTabMap().get(oldTaskConfigName).setText(taskConfigName);
            Map<String, TaskConfig> taskConfigMap = gatewayConfToolController.getGatewayConfToolService().getTaskConfigFileMap().get(fileName);
            taskConfigMap.put(taskConfigName, taskConfigMap.get(oldTaskConfigName));
            taskConfigMap.remove(oldTaskConfigName);
            gatewayConfToolController.getConfigurationTreeView().getRoot().getChildren().forEach(stringTreeItem -> {
                if (fileName.equals(stringTreeItem.getValue())) {
                    stringTreeItem.getChildren().forEach(stringTreeItem1 -> {
                        if (oldTaskConfigName.equals(stringTreeItem1.getValue())) {
                            stringTreeItem1.setValue(taskConfigName);
                        }
                    });
                }
            });
        }
        taskConfig.setIsEnable(gatewayConfToolTaskViewController.getIsEnableCheckBox().isSelected());
        String taskType = gatewayConfToolTaskViewController.getTaskTypeTextField().getText();
        taskConfig.setTaskType(StringUtils.isBlank(taskType) ? null : taskType);
        taskConfig.setTriggerType(gatewayConfToolTaskViewController.getTriggerCronTextField().getText());
        taskConfig.setIntervalTime(gatewayConfToolTaskViewController.getIntervalTimeSpinner().getValue());
        taskConfig.setExecuteTimes(gatewayConfToolTaskViewController.getExecuteTimesSpinner().getValue());
        String triggerCron = gatewayConfToolTaskViewController.getTriggerCronTextField().getText();
        taskConfig.setTriggerCron(StringUtils.isBlank(triggerCron) ? null : triggerCron);
        taskConfig.setIsStatefulJob(gatewayConfToolTaskViewController.getIsStatefullJobCheckBox().isSelected());
        taskConfig.getProperties().clear();
        gatewayConfToolTaskViewController.getPropertiesTableData().forEach(map -> {
            taskConfig.getProperties().put(map.get("key"), map.get("value"));
        });
        if ("127.0.0.1".equals(gatewayConfToolController.getHostTextField().getText())) {
            File file = new File(gatewayConfToolController.getConfigurationPathTextField().getText(), fileName);
            Yaml yaml = new Yaml();
            Writer writer = new FileWriter(file);
            yaml.dump(Arrays.asList(taskConfig), writer);
        }
    }
}