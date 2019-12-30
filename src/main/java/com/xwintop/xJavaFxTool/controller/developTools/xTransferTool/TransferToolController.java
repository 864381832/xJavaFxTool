package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.jcraft.jsch.ChannelSftp;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolService;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolUrlDocumentDialogService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.utils.TransferViewUtil;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolView;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.task.service.impl.TaskConfigServiceImpl;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class TransferToolController extends TransferToolView {
    private TransferToolService transferToolService = new TransferToolService(this);

    private ContextMenu contextMenu = new ContextMenu();
    private String[] flowStyleChoiceBoxStrings = new String[]{"AUTO", "FLOW", "BLOCK"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        TreeItem<String> treeItem = new TreeItem<String>("TaskConfig列表");
        treeItem.setExpanded(true);
        configurationTreeView.setRoot(treeItem);
        hostTextField.setText("127.0.0.1");
        configurationPathTextField.setText("./configuration");
        flowStyleChoiceBox.getItems().addAll(flowStyleChoiceBoxStrings);
        flowStyleChoiceBox.setValue(flowStyleChoiceBox.getItems().get(0));
    }

    private void initEvent() {
        JavaFxViewUtil.setPasswordTextFieldFactory(passwordTextField);
        hostTextField.setOnMouseClicked(event -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
            contextMenu.getItems().clear();
            List<Map<String, String>> list = TransferToolUrlDocumentDialogService.getConfig();
            if (list != null) {
                for (Map<String, String> map : list) {
                    MenuItem menu_tab = new MenuItem(map.get("name"));
                    menu_tab.setOnAction(event1 -> {
                        hostTextField.setText(map.get("host"));
                        portTextField.setText(map.get("port"));
                        usernameTextField.setText(map.get("userName"));
                        passwordTextField.setText(map.get("password"));
                        configurationPathTextField.setText(map.get("path"));
                    });
                    contextMenu.getItems().add(menu_tab);
                }
            }
            MenuItem menu_tab = new MenuItem("编辑历史连接");
            menu_tab.setOnAction(event1 -> {
                try {
                    FXMLLoader fXMLLoader = TransferToolUrlDocumentDialogController.getFXMLLoader();
                    JavaFxViewUtil.openNewWindow("历史连接编辑", fXMLLoader.load());
                } catch (Exception e) {
                    log.error("加载历史连接编辑界面失败", e);
                }
            });
            contextMenu.getItems().add(menu_tab);
            contextMenu.show(hostTextField, null, 0, hostTextField.getHeight());
        });

        selectTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
            contextMenu.getItems().clear();
            for (Map.Entry<String, String> stringStringEntry : transferToolService.getTaskConfigFileStringMap().entrySet()) {
                if (stringStringEntry.getValue().contains(newValue)) {
                    Map<String, TaskConfig> taskConfigMap = transferToolService.getTaskConfigFileMap().get(stringStringEntry.getKey());
                    if (taskConfigMap != null && !taskConfigMap.isEmpty()) {
                        for (Map.Entry<String, TaskConfig> stringTaskConfigEntry : taskConfigMap.entrySet()) {
                            if (stringTaskConfigEntry.getValue().toString().contains(newValue)) {
                                MenuItem menu_tab = new MenuItem(stringTaskConfigEntry.getKey());
                                menu_tab.setOnAction(event1 -> {
                                    transferToolService.addTaskConfigTabPane(stringStringEntry.getKey(), stringTaskConfigEntry.getKey());
                                });
                                contextMenu.getItems().add(menu_tab);
                            }
                        }
                    }
                    Map<String, DataSourceConfigDruid> dataSourceConfigDruidMap = transferToolService.getDataSourceConfigFileMap().get(stringStringEntry.getKey());
                    if (dataSourceConfigDruidMap != null && !dataSourceConfigDruidMap.isEmpty()) {
                        for (Map.Entry<String, DataSourceConfigDruid> stringDataSourceConfigDruidEntry : dataSourceConfigDruidMap.entrySet()) {
                            if (stringDataSourceConfigDruidEntry.getValue().toString().contains(newValue)) {
                                MenuItem menu_tab = new MenuItem(stringDataSourceConfigDruidEntry.getKey());
                                menu_tab.setOnAction(event1 -> {
                                    transferToolService.addTaskConfigTabPane(stringStringEntry.getKey(), stringDataSourceConfigDruidEntry.getKey());
                                });
                                contextMenu.getItems().add(menu_tab);
                            }
                        }
                    }
                }
            }
            contextMenu.show(selectTextField, null, 0, selectTextField.getHeight());
        });

        configurationTreeView.setCellFactory(TextFieldTreeCell.forTreeView());
        configurationTreeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = configurationTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                if ("TaskConfig列表".equals(selectedItem.getValue()) || selectedItem.getValue().endsWith("service.yml") || selectedItem.getValue().endsWith("datasource.yml")) {
                    selectedItem.setExpanded(!selectedItem.isExpanded());
                } else {
                    if (selectedItem.getParent().getValue().endsWith("service.yml") || selectedItem.getParent().getValue().endsWith("datasource.yml")) {
                        transferToolService.addTaskConfigTabPane(selectedItem.getParent().getValue(), selectedItem.getValue());
                    } else {
                        transferToolService.addTaskFileTextArea(selectedItem.getValue(), transferToolService.getTaskConfigFileStringMap().get(selectedItem.getValue()));
                    }
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_UnfoldAll = new MenuItem("展开所有");
                menu_UnfoldAll.setOnAction(event1 -> {
                    configurationTreeView.getRoot().setExpanded(true);
                    configurationTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(true);
                    });
                });
                MenuItem menu_FoldAll = new MenuItem("折叠所有");
                menu_FoldAll.setOnAction(event1 -> {
                    configurationTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(false);
                    });
                });
                ContextMenu contextMenu = new ContextMenu(menu_UnfoldAll, menu_FoldAll);
                if ("TaskConfig列表".equals(selectedItem.getValue())) {
                    MenuItem menu_AddFile = new MenuItem("添加任务配置文件");
                    menu_AddFile.setOnAction(event1 -> {
                        String fileName = "taskConf" + DateFormatUtils.format(new Date(), "MMddHHmm") + "service.yml";
                        TreeItem<String> addItem = new TreeItem<>(fileName);
                        selectedItem.getChildren().add(addItem);
                        Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                        transferToolService.getTaskConfigFileMap().put(fileName, taskConfigMap);
                        transferToolService.getTaskConfigFileStringMap().put(fileName, "");
                    });
                    contextMenu.getItems().add(menu_AddFile);
                    MenuItem menu_AddDataSourceFile = new MenuItem("添加数据源配置文件");
                    menu_AddDataSourceFile.setOnAction(event1 -> {
                        String fileName = "dataConf" + DateFormatUtils.format(new Date(), "MMddHHmm") + "datasource.yml";
                        TreeItem<String> addItem = new TreeItem<>(fileName);
                        selectedItem.getChildren().add(addItem);
                        Map<String, DataSourceConfigDruid> taskConfigMap = new ConcurrentHashMap<>();
                        transferToolService.getDataSourceConfigFileMap().put(fileName, taskConfigMap);
                        transferToolService.getTaskConfigFileStringMap().put(fileName, "");
                    });
                    contextMenu.getItems().add(menu_AddDataSourceFile);
                } else {
                    if (selectedItem.getValue().endsWith("service.yml") || selectedItem.getValue().endsWith("datasource.yml") || "TaskConfig列表".equals(selectedItem.getParent().getValue())) {
                        MenuItem menu_ViewFile = new MenuItem("查看文件内容");
                        menu_ViewFile.setOnAction(event1 -> {
                            transferToolService.addTaskFileTextArea(selectedItem.getValue(), transferToolService.getTaskConfigFileStringMap().get(selectedItem.getValue()));
                        });
                        contextMenu.getItems().add(menu_ViewFile);
                        MenuItem menu_RenameFile = new MenuItem("文件重命名");
                        menu_RenameFile.setOnAction(event1 -> {
                            String string = AlertUtil.showInputAlertDefaultValue("重命名文件", selectedItem.getValue());
                            if (StringUtils.isEmpty(string) || selectedItem.getValue().equals(string)) {
                                return;
                            }
                            if (selectedItem.getValue().endsWith("service.yml")) {
                                transferToolService.getTaskConfigFileMap().put(string, transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()));
                                transferToolService.getTaskConfigFileMap().remove(selectedItem.getValue());
                            } else if (selectedItem.getValue().endsWith("datasource.yml")) {
                                transferToolService.getDataSourceConfigFileMap().put(string, transferToolService.getDataSourceConfigFileMap().get(selectedItem.getValue()));
                                transferToolService.getDataSourceConfigFileMap().remove(selectedItem.getValue());
                            }
                            transferToolService.getTaskConfigFileStringMap().put(string, transferToolService.getTaskConfigFileStringMap().get(selectedItem.getValue()));
                            transferToolService.getTaskConfigFileStringMap().remove(selectedItem.getValue());
                            if ("127.0.0.1".equals(hostTextField.getText())) {
                                new File(configurationPathTextField.getText(), selectedItem.getValue()).renameTo(new File(configurationPathTextField.getText(), string));
                            } else {
                                try {
                                    ChannelSftp channel = TransferViewUtil.getSftpChannel(this);
                                    String remotePath = configurationPathTextField.getText();
                                    remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
                                    channel.rename(remotePath + selectedItem.getValue(), remotePath + string);
                                    TransferViewUtil.closeSftpSession(channel);
                                } catch (Exception e) {
                                    log.error("重命名文件失败：", e);
                                }
                            }
                            selectedItem.setValue(string);
                        });
                        contextMenu.getItems().add(menu_RenameFile);
                    }

                    if (selectedItem.getValue().endsWith("service.yml") || selectedItem.getValue().endsWith("datasource.yml")) {
                        if (selectedItem.getValue().endsWith("service.yml")) {
                            MenuItem menu_AddFileByCopy = new MenuItem("复制文件");
                            menu_AddFileByCopy.setOnAction(event1 -> {
                                String fileName = "copy_" + selectedItem.getValue();
                                TreeItem<String> addItem = new TreeItem<>(fileName);
                                selectedItem.getParent().getChildren().add(addItem);
                                String yamlFileString = transferToolService.getTaskConfigFileStringMap().get(selectedItem.getValue());
                                Object config = new Yaml().load(yamlFileString);
                                Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                                if (config instanceof List) {
                                    Iterable<TaskConfig> taskConfigs = (Iterable<TaskConfig>) config;
                                    for (TaskConfig taskConfig : taskConfigs) {
                                        String configName = StringUtils.appendIfMissing(taskConfig.getName(), "_copy", "_copy");
                                        while (taskConfigMap.containsKey(configName)) {
                                            String[] copyName = configName.split("_copy");
                                            if (copyName.length > 1) {
                                                configName = copyName[0] + "_copy" + (Integer.parseInt(copyName[1]) + 1);
                                            } else {
                                                configName = configName + 1;
                                            }
                                        }
                                        taskConfig.setName(configName);
                                        TreeItem<String> treeItem3 = new TreeItem<>(taskConfig.getName());
                                        addItem.getChildren().add(treeItem3);
                                        taskConfigMap.put(taskConfig.getName(), taskConfig);
                                    }
                                } else {
                                    TaskConfig taskConfig = (TaskConfig) config;
                                    String configName = StringUtils.appendIfMissing(taskConfig.getName(), "_copy", "_copy");
                                    while (taskConfigMap.containsKey(configName)) {
                                        String[] copyName = configName.split("_copy");
                                        if (copyName.length > 1) {
                                            configName = copyName[0] + "_copy" + (Integer.parseInt(copyName[1]) + 1);
                                        } else {
                                            configName = configName + 1;
                                        }
                                    }
                                    taskConfig.setName(configName);
                                    TreeItem<String> treeItem3 = new TreeItem<>(taskConfig.getName());
                                    addItem.getChildren().add(treeItem3);
                                    taskConfigMap.put(taskConfig.getName(), taskConfig);
                                }
                                transferToolService.getTaskConfigFileMap().put(fileName, taskConfigMap);
                                transferToolService.getTaskConfigFileStringMap().put(fileName, new Yaml().dump(config));
                            });
                            contextMenu.getItems().add(menu_AddFileByCopy);
                        }
                        MenuItem menu_AddTask = new MenuItem("添加");
                        menu_AddTask.setOnAction(event1 -> {
                            if (selectedItem.getValue().endsWith("service.yml")) {
                                String taskConfigName = "taskConfig" + DateFormatUtils.format(new Date(), "MMddHHmmss");
                                TreeItem<String> addItem = new TreeItem<>(taskConfigName);
                                selectedItem.getChildren().add(addItem);
                                TaskConfig taskConfig = new TaskConfig();
                                taskConfig.setName(taskConfigName);
                                transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).put(taskConfigName, taskConfig);
                            } else if (selectedItem.getValue().endsWith("datasource.yml")) {
                                String taskConfigName = "dataSourceConfig" + DateFormatUtils.format(new Date(), "MMddHHmmss");
                                TreeItem<String> addItem = new TreeItem<>(taskConfigName);
                                selectedItem.getChildren().add(addItem);
                                DataSourceConfigDruid taskConfig = new DataSourceConfigDruid();
                                taskConfig.setId(taskConfigName);
                                transferToolService.getDataSourceConfigFileMap().get(selectedItem.getValue()).put(taskConfigName, taskConfig);
                            }
                        });
                        contextMenu.getItems().add(menu_AddTask);
                        if (selectedItem.getValue().endsWith("service.yml")) {
                            MenuItem menu_AddTaskByCopy = new MenuItem("粘贴任务");
                            menu_AddTaskByCopy.setOnAction(event1 -> {
                                String taskConfigString = ClipboardUtil.getStr();
                                try {
                                    TaskConfig taskConfig = new Yaml().load(taskConfigString);
                                    String taskConfigName = StringUtils.appendIfMissing(taskConfig.getName(), "_copy", "_copy");
                                    while (transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).containsKey(taskConfigName)) {
                                        String[] copyName = taskConfigName.split("_copy");
                                        if (copyName.length > 1) {
                                            taskConfigName = copyName[0] + "_copy" + (Integer.parseInt(copyName[1]) + 1);
                                        } else {
                                            taskConfigName = taskConfigName + 1;
                                        }
                                    }
                                    TreeItem<String> addItem = new TreeItem<>(taskConfigName);
                                    selectedItem.getChildren().add(addItem);
                                    taskConfig.setName(taskConfigName);
                                    transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).put(taskConfigName, taskConfig);
                                } catch (Exception e) {
                                    TooltipUtil.showToast("粘贴任务加载异常:" + e.getMessage());
                                }
                            });
                            contextMenu.getItems().add(menu_AddTaskByCopy);
                        }
                        MenuItem menu_RemoveFile = new MenuItem("删除文件");
                        menu_RemoveFile.setOnAction(event1 -> {
                            if (!AlertUtil.showConfirmAlert("确定要删除文件吗？")) {
                                return;
                            }
                            for (String taskName : transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).keySet()) {
                                transferToolService.removeTaskConfigTabPane(taskName);
                            }
                            transferToolService.getTaskConfigFileMap().remove(selectedItem.getValue());
                            transferToolService.getTaskConfigFileStringMap().remove(selectedItem.getValue());
                            if ("127.0.0.1".equals(hostTextField.getText())) {
                                new File(configurationPathTextField.getText(), selectedItem.getValue()).delete();
                            } else {
                                try {
                                    ChannelSftp channel = TransferViewUtil.getSftpChannel(this);
                                    String remotePath = configurationPathTextField.getText();
                                    remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
                                    channel.rm(remotePath + selectedItem.getValue());
                                    TransferViewUtil.closeSftpSession(channel);
                                } catch (Exception e) {
                                    log.error("删除文件失败：", e);
                                    TooltipUtil.showToast("删除文件失败：" + e.getMessage());
                                }
                            }
                            selectedItem.getParent().getChildren().remove(selectedItem);
                        });
                        contextMenu.getItems().add(menu_RemoveFile);
                        MenuItem menu_RemoveAll = new MenuItem("删除所有配置");
                        menu_RemoveAll.setOnAction(event1 -> {
                            if (!AlertUtil.showConfirmAlert("确定要删除所有配置吗？")) {
                                return;
                            }
                            selectedItem.getChildren().clear();
                            if (selectedItem.getValue().endsWith("service.yml")) {
                                for (String taskName : transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).keySet()) {
                                    transferToolService.removeTaskConfigTabPane(taskName);
                                }
                                transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).clear();
                            } else if (selectedItem.getValue().endsWith("datasource.yml")) {
                                for (String taskName : transferToolService.getDataSourceConfigFileMap().get(selectedItem.getValue()).keySet()) {
                                    transferToolService.removeTaskConfigTabPane(taskName);
                                }
                                transferToolService.getDataSourceConfigFileMap().get(selectedItem.getValue()).clear();
                            }
                        });
                        contextMenu.getItems().add(menu_RemoveAll);
                        MenuItem menu_SaveFile = new MenuItem("保存文件");
                        menu_SaveFile.setOnAction(event1 -> {
                            try {
                                String configYamlString = null;
                                if (selectedItem.getValue().endsWith("service.yml")) {
                                    configYamlString = TransferViewUtil.saveConfig(this, selectedItem.getValue(), transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).values().toArray());
                                } else if (selectedItem.getValue().endsWith("datasource.yml")) {
                                    configYamlString = TransferViewUtil.saveConfig(this, selectedItem.getValue(), transferToolService.getDataSourceConfigFileMap().get(selectedItem.getValue()).values().toArray());
                                }
                                transferToolService.getTaskConfigFileStringMap().put(selectedItem.getValue(), configYamlString);
                            } catch (Exception e) {
                                log.error("保存失败:", e);
                                TooltipUtil.showToast("保存失败:" + e.getMessage());
                            }
                        });
                        contextMenu.getItems().add(menu_SaveFile);
                    }
                    if (!selectedItem.getValue().endsWith("service.yml") && !"TaskConfig列表".equals(selectedItem.getValue()) && !selectedItem.getValue().endsWith("datasource.yml") && !"TaskConfig列表".equals(selectedItem.getParent().getValue())) {
                        if (selectedItem.getParent().getValue().endsWith("service.yml")) {
                            MenuItem menu_Copy = new MenuItem("复制选中行");
                            menu_Copy.setOnAction(event1 -> {
                                try {
                                    Map<String, TaskConfig> taskConfigMap = transferToolService.getTaskConfigFileMap().get(selectedItem.getParent().getValue());
                                    String configName = StringUtils.appendIfMissing(selectedItem.getValue(), "_copy", "_copy");
                                    while (taskConfigMap.containsKey(configName)) {
                                        String[] copyName = configName.split("_copy");
                                        if (copyName.length > 1) {
                                            configName = copyName[0] + "_copy" + (Integer.parseInt(copyName[1]) + 1);
                                        } else {
                                            configName = configName + 1;
                                        }
                                    }
                                    TreeItem<String> addItem = new TreeItem<>(configName);
                                    selectedItem.getParent().getChildren().add(addItem);
                                    TaskConfig taskConfig = taskConfigMap.get(selectedItem.getValue());
                                    Yaml yaml = new Yaml();
                                    TaskConfig newTaskConfig = yaml.load(yaml.dump(taskConfig));
                                    newTaskConfig.setName(configName);
                                    taskConfigMap.put(configName, newTaskConfig);
                                } catch (Exception e) {
                                    log.error("复制失败：", e);
                                    TooltipUtil.showToast("复制失败：" + e.getMessage());
                                }
                            });
                            contextMenu.getItems().add(menu_Copy);
                            MenuItem menu_CopyToClipboard = new MenuItem("复制选中行到剪切板");
                            menu_CopyToClipboard.setOnAction(event1 -> {
                                try {
                                    TaskConfig taskConfig = transferToolService.getTaskConfigFileMap().get(selectedItem.getParent().getValue()).get(selectedItem.getValue());
                                    String taskConfigString = new Yaml().dump(taskConfig);
                                    ClipboardUtil.setStr(taskConfigString);
                                    TooltipUtil.showToast("复制成功！" + taskConfigString);
                                } catch (Exception e) {
                                    log.error("复制失败：", e);
                                    TooltipUtil.showToast("复制失败：" + e.getMessage());
                                }
                            });
                            contextMenu.getItems().add(menu_CopyToClipboard);
                        }
                        MenuItem menu_Remove = new MenuItem("删除选中行");
                        menu_Remove.setOnAction(event1 -> {
                            transferToolService.removeTaskConfigTabPane(selectedItem.getValue());
                            if (selectedItem.getParent().getValue().endsWith("service.yml")) {
                                transferToolService.getTaskConfigFileMap().get(selectedItem.getParent().getValue()).remove(selectedItem.getValue());
                            } else if (selectedItem.getParent().getValue().endsWith("datasource.yml")) {
                                transferToolService.getDataSourceConfigFileMap().get(selectedItem.getParent().getValue()).remove(selectedItem.getValue());
                            }
                            selectedItem.getParent().getChildren().remove(selectedItem);
                        });
                        contextMenu.getItems().add(menu_Remove);
                    }
                }
                configurationTreeView.setContextMenu(contextMenu);
            }
        });
        taskConfigTabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_RemoveAll = new MenuItem("关闭所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    taskConfigTabPane.getTabs().clear();
                    transferToolService.getTaskConfigTabMap().clear();
                });
                taskConfigTabPane.setContextMenu(new ContextMenu(menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void treeRefurbishAction(ActionEvent event) {
        try {
            TransferToolUrlDocumentDialogService.addConfig(hostTextField.getText(), portTextField.getText(), usernameTextField.getText(), passwordTextField.getText(), configurationPathTextField.getText());
            transferToolService.reloadTaskConfigFile();
            taskConfigTabPane.getTabs().removeAll(taskConfigTabPane.getTabs());
            transferToolService.getTaskConfigTabMap().clear();
        } catch (Exception e) {
            TooltipUtil.showToast("加载配置失败！");
            log.error("加载配置失败：", e);
        }
    }

    @FXML
    private void startTransferAction(ActionEvent event) {
        TooltipUtil.showToast("正在后台启动传输工具");
        startTransferButton.setDisable(true);
        ThreadUtil.execute(() -> {
            try {
                Singleton.get(TaskConfigServiceImpl.class).initTaskSchedule();
            } catch (Exception e) {
                log.error("加载任务失败:", e);
            }
        });
    }

    @FXML
    private void connectAction(ActionEvent event) {
        this.treeRefurbishAction(null);
    }
}