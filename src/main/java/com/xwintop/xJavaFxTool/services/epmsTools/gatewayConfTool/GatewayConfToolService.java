package com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool;

import com.easipass.gateway.entity.TaskConfig;
import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataTableController;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolController;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolTaskViewController;
import com.xwintop.xcore.util.RedisUtil;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class GatewayConfToolService {
    private GatewayConfToolController gatewayConfToolController;
    private Map<String, Map<String, TaskConfig>> taskConfigFileMap = new ConcurrentHashMap<>();//任务对应配置文件map

    private Map<String, Tab> taskConfigTabMap = new HashMap<String, Tab>();

    public GatewayConfToolService(GatewayConfToolController gatewayConfToolController) {
        this.gatewayConfToolController = gatewayConfToolController;
    }

    public void reloadTaskConfigFile() {
        if (StringUtils.isBlank(gatewayConfToolController.getConfigurationPathTextField().getText())) {
            TooltipUtil.showToast("配置目录不能为空！");
            return;
        }
        TreeItem<String> treeItem = gatewayConfToolController.getConfigurationTreeView().getRoot();
        treeItem.getChildren().clear();
        if ("127.0.0.1".equals(gatewayConfToolController.getHostTextField().getText())) {
            File CONFIG_DIR = new File(gatewayConfToolController.getConfigurationPathTextField().getText());
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
        }
    }

    public void addTaskConfigTabPane(String fileName, String taskName) {
        String tabName = taskName;
        Tab tab1 = taskConfigTabMap.get(tabName);
        if (tab1 != null) {
            gatewayConfToolController.getTaskConfigTabPane().getSelectionModel().select(tab1);
            return;
        }
        final Tab tab = new Tab(tabName);
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                List<Tab> tabList = new ArrayList<Tab>();
                gatewayConfToolController.getTaskConfigTabPane().getTabs().forEach((Tab tab2) -> {
                    if (tab2.getText().startsWith(tabName)) {
                        tabList.add(tab2);
                    }
                });
                gatewayConfToolController.getTaskConfigTabPane().getTabs().removeAll(tabList);
                taskConfigTabMap.remove(tab.getText());
            }
        });
        FXMLLoader fXMLLoader = GatewayConfToolTaskViewController.getFXMLLoader();
        try {
            tab.setContent(fXMLLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        GatewayConfToolTaskViewController gatewayConfToolTaskViewController = fXMLLoader.getController();
        gatewayConfToolTaskViewController.setData(gatewayConfToolController, taskConfigFileMap.get(fileName).get(taskName));
        gatewayConfToolTaskViewController.setTabName(tabName);
        gatewayConfToolController.getTaskConfigTabPane().getTabs().add(tab);
        gatewayConfToolController.getTaskConfigTabPane().getSelectionModel().select(tab);
        taskConfigTabMap.put(tabName, tab);
    }
}