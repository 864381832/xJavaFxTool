package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolTaskViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolTaskViewView;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class TransferToolTaskViewController extends TransferToolTaskViewView {
    private TransferToolTaskViewService transferToolTaskViewService = new TransferToolTaskViewService(this);
    private ObservableList<Map<String, String>> propertiesTableData = FXCollections.observableArrayList();
    private ObservableList<String> receiverConfigListData = FXCollections.observableArrayList();
    private ObservableList<String> filterConfigsListData = FXCollections.observableArrayList();
    private ObservableList<String> senderConfigListData = FXCollections.observableArrayList();
    private String[] triggerTypeChoiceBoxStrings = new String[]{"SIMPLE", "CRON"};

    private TaskConfig taskConfig;

    private String fileName;
    private String tabName;

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/developTools/xTransferTool/TransferToolTaskView.fxml"));
        return fXMLLoader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        nameTextField.prefColumnCountProperty().bind(nameTextField.textProperty().length());
        taskTypeTextField.prefColumnCountProperty().bind(taskTypeTextField.textProperty().length());
        triggerCronTextField.prefColumnCountProperty().bind(triggerCronTextField.textProperty().length());
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesKeyTableColumn, "key");
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesValueTableColumn, "value");
        propertiesTableView.setItems(propertiesTableData);
        triggerTypeChoiceBox.getItems().addAll(triggerTypeChoiceBoxStrings);
        triggerTypeChoiceBox.setValue(triggerTypeChoiceBox.getItems().get(0));
        JavaFxViewUtil.setSpinnerValueFactory(intervalTimeSpinner, 0, Integer.MAX_VALUE, 5);
        JavaFxViewUtil.setSpinnerValueFactory(executeTimesSpinner, -1, Integer.MAX_VALUE);
        receiverConfigListView.setItems(receiverConfigListData);
        filterConfigsListView.setItems(filterConfigsListData);
        senderConfigListView.setItems(senderConfigListData);
    }

    private void initEvent() {
        triggerTypeChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (triggerTypeChoiceBoxStrings[0].equals(newValue)) {
                    triggerCronTextField.setDisable(true);
                    intervalTimeSpinner.setDisable(false);
                    executeTimesSpinner.setDisable(false);
                } else if (triggerTypeChoiceBoxStrings[1].equals(newValue)) {
                    triggerCronTextField.setDisable(false);
                    intervalTimeSpinner.setDisable(true);
                    executeTimesSpinner.setDisable(true);
                }
            }
        });
        serviceViewTabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_RemoveAll = new MenuItem("关闭所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    serviceViewTabPane.getTabs().clear();
                    transferToolTaskViewService.getServiceViewTabMap().clear();
                });
                serviceViewTabPane.setContextMenu(new ContextMenu(menu_RemoveAll));
            }
        });
        receiverConfigListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.xwintop.xTransfer.receiver.bean";
                String[] classNameS = new String[]{
                        "ReceiverConfigEmail",
                        "ReceiverConfigFs",
                        "ReceiverConfigFsSplit",
                        "ReceiverConfigFtp",
                        "ReceiverConfigIbmMq",
                        "ReceiverConfigJms",
                        "ReceiverConfigKafka",
                        "ReceiverConfigRabbitMq",
                        "ReceiverConfigRocketMq",
                        "ReceiverConfigActiveMq",
                        "ReceiverConfigHdfs",
                        "ReceiverConfigSftp"};
                for (String className : classNameS) {
                    MenuItem menuAdd = new MenuItem(className);
                    menuAdd.setOnAction(event1 -> {
                        try {
                            Object configObject = Class.forName(packageName + "." + className).newInstance();
                            taskConfig.getReceiverConfig().add((ReceiverConfig) configObject);
                            receiverConfigListData.add(((ReceiverConfig) configObject).getServiceName());
                            int selectIndex = receiverConfigListData.size() - 1;
                            receiverConfigListView.getSelectionModel().select(selectIndex);
                            transferToolTaskViewService.addServiceViewTabPane(configObject, selectIndex);
                        } catch (Exception e) {
                            log.error("添加失败：", e);
                            TooltipUtil.showToast("添加失败：" + e.getMessage());
                        }
                    });
                    menu.getItems().add(menuAdd);
                }
//                ClassLoader loader = Thread.currentThread().getContextClassLoader();
//                String packagePath = packageName.replace(".", "/");
//                URL url = loader.getResource(packagePath);
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    String selectString = receiverConfigListView.getSelectionModel().getSelectedItem();
                    receiverConfigListData.add(selectString);
                    Yaml yaml = new Yaml();
                    taskConfig.getReceiverConfig().add(yaml.load(yaml.dump(taskConfig.getReceiverConfig().get(receiverConfigListView.getSelectionModel().getSelectedIndex()))));
                });
                MenuItem menu_CopyToClipboard = new MenuItem("复制选中行到剪切板");
                menu_CopyToClipboard.setOnAction(event1 -> {
                    try {
                        String taskConfigString = new Yaml().dump(taskConfig.getReceiverConfig().get(receiverConfigListView.getSelectionModel().getSelectedIndex()));
                        ClipboardUtil.setStr(taskConfigString);
                        TooltipUtil.showToast("复制成功！" + taskConfigString);
                    } catch (Exception e) {
                        log.error("复制失败：", e);
                        TooltipUtil.showToast("复制失败：" + e.getMessage());
                    }
                });
                MenuItem menuAddByClipboard = new MenuItem("粘贴Receiver");
                menuAddByClipboard.setOnAction(event1 -> {
                    String configString = ClipboardUtil.getStr();
                    try {
                        ReceiverConfig receiverConfig = new Yaml().load(configString);
                        receiverConfigListData.add(receiverConfig.getServiceName());
                        taskConfig.getReceiverConfig().add(receiverConfig);
                    } catch (Exception e) {
                        log.error("粘贴Receiver加载异常:", e);
                        TooltipUtil.showToast("粘贴Receiver加载异常:" + e.getMessage());
                    }
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    taskConfig.getReceiverConfig().remove(receiverConfigListView.getSelectionModel().getSelectedIndex());
                    receiverConfigListData.remove(receiverConfigListView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    if (!AlertUtil.showConfirmAlert("确定要删除所有吗？")) {
                        return;
                    }
                    receiverConfigListData.clear();
                    taskConfig.getReceiverConfig().clear();
                });
                receiverConfigListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_CopyToClipboard, menuAddByClipboard, menu_Remove, menu_RemoveAll));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                int selectIndex = receiverConfigListView.getSelectionModel().getSelectedIndex();
                if (receiverConfigListView.getSelectionModel().getSelectedItems() == null || selectIndex == -1) {
                    return;
                }
                transferToolTaskViewService.addServiceViewTabPane(taskConfig.getReceiverConfig().get(selectIndex), selectIndex);
            }
        });
        filterConfigsListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.xwintop.xTransfer.filter.bean";
                String[] classNameS = new String[]{
                        "FilterConfigBackup",
                        "FilterConfigCompress",
                        "FilterConfigDecompress",
                        "FilterConfigEncryptDecrypt",
                        "FilterConfigOracleSqlldr",
                        "FilterConfigGroovyScript",
                        "FilterConfigPythonScript",
                        "FilterConfigJavaScript",
                        "FilterConfigLuaScript",
                        "FilterConfigUnicodeTransformation",
                };
                for (String className : classNameS) {
                    MenuItem menuAdd = new MenuItem(className);
                    menuAdd.setOnAction(event1 -> {
                        try {
                            Object configObject = Class.forName(packageName + "." + className).newInstance();
                            taskConfig.getFilterConfigs().add((FilterConfig) configObject);
                            filterConfigsListData.add(((FilterConfig) configObject).getServiceName());
                            int selectIndex = filterConfigsListData.size() - 1;
                            filterConfigsListView.getSelectionModel().select(selectIndex);
                            transferToolTaskViewService.addServiceViewTabPane(configObject, selectIndex);
                        } catch (Exception e) {
                            log.error("添加异常:", e);
                            TooltipUtil.showToast("添加异常:" + e.getMessage());
                        }
                    });
                    menu.getItems().add(menuAdd);
                }
//                ClassLoader loader = Thread.currentThread().getContextClassLoader();
//                String packagePath = packageName.replace(".", "/");
//                URL url = loader.getResource(packagePath);
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    String selectString = filterConfigsListView.getSelectionModel().getSelectedItem();
                    filterConfigsListData.add(selectString);
                    Yaml yaml = new Yaml();
                    taskConfig.getFilterConfigs().add(yaml.load(yaml.dump(taskConfig.getFilterConfigs().get(filterConfigsListView.getSelectionModel().getSelectedIndex()))));
                });
                MenuItem menu_CopyToClipboard = new MenuItem("复制选中行到剪切板");
                menu_CopyToClipboard.setOnAction(event1 -> {
                    try {
                        String taskConfigString = new Yaml().dump(taskConfig.getFilterConfigs().get(filterConfigsListView.getSelectionModel().getSelectedIndex()));
                        ClipboardUtil.setStr(taskConfigString);
                        TooltipUtil.showToast("复制成功！" + taskConfigString);
                    } catch (Exception e) {
                        log.error("复制失败：", e);
                        TooltipUtil.showToast("复制失败：" + e.getMessage());
                    }
                });
                MenuItem menuAddByClipboard = new MenuItem("粘贴Filter");
                menuAddByClipboard.setOnAction(event1 -> {
                    String configString = ClipboardUtil.getStr();
                    try {
                        FilterConfig filterConfig = new Yaml().load(configString);
                        filterConfigsListData.add(filterConfig.getServiceName());
                        taskConfig.getFilterConfigs().add(filterConfig);
                    } catch (Exception e) {
                        log.error("粘贴Filter加载异常:", e);
                        TooltipUtil.showToast("粘贴Filter加载异常:" + e.getMessage());
                    }
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    taskConfig.getFilterConfigs().remove(filterConfigsListView.getSelectionModel().getSelectedIndex());
                    filterConfigsListData.remove(filterConfigsListView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    filterConfigsListData.clear();
                    taskConfig.getFilterConfigs().clear();
                });
                filterConfigsListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_CopyToClipboard, menuAddByClipboard, menu_Remove, menu_RemoveAll));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                int selectIndex = filterConfigsListView.getSelectionModel().getSelectedIndex();
                if (filterConfigsListView.getSelectionModel().getSelectedItems() == null || selectIndex == -1) {
                    return;
                }
                transferToolTaskViewService.addServiceViewTabPane(taskConfig.getFilterConfigs().get(selectIndex), selectIndex);
            }
        });
        senderConfigListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.xwintop.xTransfer.sender.bean";
                String[] classNameS = new String[]{
                        "SenderConfigEmail",
                        "SenderConfigFs",
                        "SenderConfigFtp",
                        "SenderConfigIbmMq",
                        "SenderConfigJms",
                        "SenderConfigKafka",
                        "SenderConfigRabbitMq",
                        "SenderConfigRocketMq",
                        "SenderConfigActiveMq",
                        "SenderConfigHdfs",
                        "SenderConfigSftp"
                };
                for (String className : classNameS) {
                    MenuItem menuAdd = new MenuItem(className);
                    menuAdd.setOnAction(event1 -> {
                        try {
                            Object configObject = Class.forName(packageName + "." + className).newInstance();
                            taskConfig.getSenderConfig().add((SenderConfig) configObject);
                            senderConfigListData.add(((SenderConfig) configObject).getServiceName());
                            int selectIndex = senderConfigListData.size() - 1;
                            senderConfigListView.getSelectionModel().select(selectIndex);
                            transferToolTaskViewService.addServiceViewTabPane(configObject, selectIndex);
                        } catch (Exception e) {
                            log.error("添加异常:", e);
                            TooltipUtil.showToast("添加异常:" + e.getMessage());
                        }
                    });
                    menu.getItems().add(menuAdd);
                }
//                ClassLoader loader = Thread.currentThread().getContextClassLoader();
//                String packagePath = packageName.replace(".", "/");
//                URL url = loader.getResource(packagePath);
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    String selectString = senderConfigListView.getSelectionModel().getSelectedItem();
                    senderConfigListData.add(selectString);
                    Yaml yaml = new Yaml();
                    taskConfig.getSenderConfig().add(yaml.load(yaml.dump(taskConfig.getSenderConfig().get(senderConfigListView.getSelectionModel().getSelectedIndex()))));
                });
                MenuItem menu_CopyToClipboard = new MenuItem("复制选中行到剪切板");
                menu_CopyToClipboard.setOnAction(event1 -> {
                    try {
                        String taskConfigString = new Yaml().dump(taskConfig.getSenderConfig().get(senderConfigListView.getSelectionModel().getSelectedIndex()));
                        ClipboardUtil.setStr(taskConfigString);
                        TooltipUtil.showToast("复制成功！" + taskConfigString);
                    } catch (Exception e) {
                        log.error("复制失败：", e);
                        TooltipUtil.showToast("复制失败：" + e.getMessage());
                    }
                });
                MenuItem menuAddByClipboard = new MenuItem("粘贴Sender");
                menuAddByClipboard.setOnAction(event1 -> {
                    String configString = ClipboardUtil.getStr();
                    try {
                        SenderConfig senderConfig = new Yaml().load(configString);
                        senderConfigListData.add(senderConfig.getServiceName());
                        taskConfig.getSenderConfig().add(senderConfig);
                    } catch (Exception e) {
                        TooltipUtil.showToast("粘贴Sender加载异常:" + e.getMessage());
                    }
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    taskConfig.getSenderConfig().remove(senderConfigListView.getSelectionModel().getSelectedIndex());
                    senderConfigListData.remove(senderConfigListView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    senderConfigListData.clear();
                    taskConfig.getSenderConfig().clear();
                });
                senderConfigListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_CopyToClipboard, menuAddByClipboard, menu_Remove, menu_RemoveAll));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                int selectIndex = senderConfigListView.getSelectionModel().getSelectedIndex();
                if (senderConfigListView.getSelectionModel().getSelectedItems() == null || selectIndex == -1) {
                    return;
                }
                transferToolTaskViewService.addServiceViewTabPane(taskConfig.getSenderConfig().get(selectIndex), selectIndex);
            }
        });

        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(propertiesTableView);
    }

    private void initService() {
    }

    @FXML
    void saveTaskConfigAction(ActionEvent event) {
        try {
            transferToolTaskViewService.saveTaskConfigAction();
        } catch (Exception e) {
            TooltipUtil.showToast("保存配置失败：" + e.getMessage());
            log.error("保存配置失败：", e);
        }
    }

    @FXML
    void viewTaskConfigAction(ActionEvent event) {
        try {
            transferToolTaskViewService.viewTaskConfigAction();
        } catch (Exception e) {
            TooltipUtil.showToast("预览配置失败：" + e.getMessage());
            log.error("预览配置失败：", e);
        }
    }

    public void setData(TransferToolController xTransferToolController, TaskConfig taskConfig) {
        transferToolTaskViewService.setTransferToolController(xTransferToolController);
        this.taskConfig = taskConfig;
        nameTextField.setText(this.taskConfig.getName());
        isEnableCheckBox.setSelected(this.taskConfig.getIsEnable());
        taskTypeTextField.setText(this.taskConfig.getTaskType());
        triggerTypeChoiceBox.setValue(this.taskConfig.getTriggerType());
        intervalTimeSpinner.getValueFactory().setValue(this.taskConfig.getIntervalTime());
        executeTimesSpinner.getValueFactory().setValue(this.taskConfig.getExecuteTimes());
        triggerCronTextField.setText(this.taskConfig.getTriggerCron());
        isStatefullJobCheckBox.setSelected(this.taskConfig.getIsStatefulJob());
        receiverConfigListData.clear();
        for (ReceiverConfig receiverConfig : this.taskConfig.getReceiverConfig()) {
            receiverConfigListData.add(receiverConfig.getServiceName());
        }
        filterConfigsListData.clear();
        for (FilterConfig filterConfig : this.taskConfig.getFilterConfigs()) {
            filterConfigsListData.add(filterConfig.getServiceName());
        }
        senderConfigListData.clear();
        for (SenderConfig senderConfig : this.taskConfig.getSenderConfig()) {
            senderConfigListData.add(senderConfig.getServiceName());
        }
        propertiesTableData.clear();
        this.taskConfig.getProperties().forEach((key, value) -> {
            Map<String, String> map = new HashMap<>();
            map.put("key", key);
            map.put("value", value.toString());
            propertiesTableData.add(map);
        });
    }
}