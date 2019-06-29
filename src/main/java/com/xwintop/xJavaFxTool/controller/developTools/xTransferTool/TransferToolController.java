package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.thread.ThreadUtil;
import com.jcraft.jsch.ChannelSftp;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolService;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolView;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.task.service.impl.TaskConfigServiceImpl;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class TransferToolController extends TransferToolView {
    private TransferToolService transferToolService = new TransferToolService(this);

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
    }

    private void initEvent() {
        configurationTreeView.setEditable(true);
        configurationTreeView.setCellFactory(TextFieldTreeCell.forTreeView());
        configurationTreeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = configurationTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!selectedItem.getValue().endsWith("service.yml") && !selectedItem.getValue().equals("TaskConfig列表")) {
                    transferToolService.addTaskConfigTabPane(selectedItem.getParent().getValue(), selectedItem.getValue());
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
                    configurationTreeView.getRoot().setExpanded(false);
                    configurationTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(false);
                    });
                });
                ContextMenu contextMenu = new ContextMenu(menu_UnfoldAll, menu_FoldAll);
                if (selectedItem.getValue().equals("TaskConfig列表")) {
                    MenuItem menu_AddFile = new MenuItem("添加配置文件");
                    menu_AddFile.setOnAction(event1 -> {
                        String fileName = "taskConf" + DateFormatUtils.format(new Date(), "MMddHHmm") + "service.yml";
                        TreeItem<String> addItem = new TreeItem<>(fileName);
                        selectedItem.getChildren().add(addItem);
                        Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                        transferToolService.getTaskConfigFileMap().put(fileName, taskConfigMap);
                    });
                    contextMenu.getItems().add(menu_AddFile);
                }
                if (selectedItem.getValue().endsWith("service.yml")) {
                    MenuItem menu_AddTask = new MenuItem("添加任务");
                    menu_AddTask.setOnAction(event1 -> {
                        String taskConfigName = "taskConfig" + DateFormatUtils.format(new Date(), "MMddHHmm");
                        TreeItem<String> addItem = new TreeItem<>(taskConfigName);
                        selectedItem.getChildren().add(addItem);
                        TaskConfig taskConfig = new TaskConfig();
                        taskConfig.setName(taskConfigName);
                        transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).put(taskConfigName, taskConfig);
                    });
                    contextMenu.getItems().add(menu_AddTask);
                    MenuItem menu_RemoveFile = new MenuItem("删除文件");
                    menu_RemoveFile.setOnAction(event1 -> {
                        transferToolService.getTaskConfigFileMap().remove(selectedItem.getValue());
                        if ("127.0.0.1".equals(hostTextField.getText())) {
                            new File(configurationPathTextField.getText(), selectedItem.getValue()).delete();
                        } else {
                            try {
                                ChannelSftp channel = transferToolService.getSftpChannel();
                                String remotePath = configurationPathTextField.getText();
                                remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
                                channel.rm(remotePath + selectedItem.getValue());
                                transferToolService.closeSftpSession(channel);
                            } catch (Exception e) {
                                log.error("删除文件失败：", e);
                            }
                        }
                        selectedItem.getParent().getChildren().remove(selectedItem);
                    });
                    contextMenu.getItems().add(menu_RemoveFile);
                    MenuItem menu_RemoveAll = new MenuItem("删除所有任务");
                    menu_RemoveAll.setOnAction(event1 -> {
                        selectedItem.getChildren().clear();
                        transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).clear();
                    });
                    contextMenu.getItems().add(menu_RemoveAll);
                    MenuItem menu_SaveFile = new MenuItem("保存文件");
                    menu_SaveFile.setOnAction(event1 -> {
                        try {
                            if ("127.0.0.1".equals(hostTextField.getText())) {
                                File file = new File(configurationPathTextField.getText(), selectedItem.getValue());
                                Yaml yaml = new Yaml();
                                Writer writer = new FileWriter(file);
                                yaml.dump(transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).values().toArray(), writer);
                                writer.close();
                            } else {
                                Yaml yaml = new Yaml();
                                byte[] configBytes = yaml.dump(transferToolService.getTaskConfigFileMap().get(selectedItem.getValue()).values().toArray()).getBytes();
                                ChannelSftp channel = transferToolService.getSftpChannel();
                                String remotePath = configurationPathTextField.getText();
                                remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
                                channel.put(new ByteArrayInputStream(configBytes), remotePath + selectedItem.getValue());
                                transferToolService.closeSftpSession(channel);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    contextMenu.getItems().add(menu_SaveFile);
                }
                if (!selectedItem.getValue().endsWith("service.yml") && !selectedItem.getValue().equals("TaskConfig列表")) {
                    MenuItem menu_Copy = new MenuItem("复制选中行");
                    menu_Copy.setOnAction(event1 -> {
                        try {
                            String configName = selectedItem.getValue() + "_copy";
                            TreeItem<String> addItem = new TreeItem<>(configName);
                            selectedItem.getParent().getChildren().add(addItem);
                            TaskConfig taskConfig = transferToolService.getTaskConfigFileMap().get(selectedItem.getParent().getValue()).get(selectedItem.getValue());
                            TaskConfig newTaskConfig = (TaskConfig) BeanUtils.cloneBean(taskConfig);
                            newTaskConfig.setName(configName);
                            transferToolService.getTaskConfigFileMap().get(selectedItem.getParent().getValue()).put(configName, newTaskConfig);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    contextMenu.getItems().add(menu_Copy);
                    MenuItem menu_Remove = new MenuItem("删除选中任务");
                    menu_Remove.setOnAction(event1 -> {
                        transferToolService.getTaskConfigFileMap().get(selectedItem.getParent().getValue()).remove(selectedItem.getValue());
                        selectedItem.getParent().getChildren().remove(selectedItem);
                    });
                    contextMenu.getItems().add(menu_Remove);
                }
                configurationTreeView.setContextMenu(contextMenu);
            }
        });
        configurationTreeView.setOnEditCommit(event -> {
            if (event.getNewValue().equals(event.getOldValue())) {
                return;
            }
            if (event.getOldValue().endsWith("TaskConfig列表")) {
                Platform.runLater(() -> {
                    event.getTreeItem().setValue(event.getOldValue());
                });
            } else if (event.getOldValue().endsWith("service.yml")) {
                if (!event.getNewValue().endsWith("service.yml")) {
                    Platform.runLater(() -> {
                        event.getTreeItem().setValue(event.getOldValue());
                    });
                    return;
                }
                transferToolService.getTaskConfigFileMap().put(event.getNewValue(), transferToolService.getTaskConfigFileMap().get(event.getOldValue()));
                transferToolService.getTaskConfigFileMap().remove(event.getOldValue());
            } else {
                Map<String, TaskConfig> taskConfigMap = transferToolService.getTaskConfigFileMap().get(event.getTreeItem().getParent().getValue());
                taskConfigMap.get(event.getOldValue()).setName(event.getNewValue());
                taskConfigMap.put(event.getNewValue(), taskConfigMap.get(event.getOldValue()));
                taskConfigMap.remove(event.getOldValue());
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
            transferToolService.reloadTaskConfigFile();
            taskConfigTabPane.getTabs().removeAll(taskConfigTabPane.getTabs());
            transferToolService.getTaskConfigTabMap().clear();
        } catch (Exception e) {
            log.error("加载配置失败：", e);
        }
    }

    @FXML
    private void startTransferAction(ActionEvent event) {
        TooltipUtil.showToast("正在后台启动传输工具");
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