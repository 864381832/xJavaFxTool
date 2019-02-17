package com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool;

import com.easipass.gateway.entity.TaskConfig;
import com.easipass.gateway.filter.bean.FilterConfig;
import com.easipass.gateway.receiver.entity.ReceiverConfig;
import com.easipass.gateway.route.entity.SenderConfig;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool.GatewayConfToolTaskViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool.GatewayConfToolTaskViewView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class GatewayConfToolTaskViewController extends GatewayConfToolTaskViewView {
    private GatewayConfToolTaskViewService gatewayConfToolTaskViewService = new GatewayConfToolTaskViewService(this);
    private ObservableList<Map<String, String>> propertiesTableData = FXCollections.observableArrayList();
    private ObservableList<String> receiverConfigListData = FXCollections.observableArrayList();
    private ObservableList<String> filterConfigsListData = FXCollections.observableArrayList();
    private ObservableList<String> senderConfigListData = FXCollections.observableArrayList();
    private String[] triggerTypeChoiceBoxStrings = new String[]{"SIMPLE", "CRON"};

    private TaskConfig taskConfig;

    private GatewayConfToolController gatewayConfToolController;
    private String tabName;

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/epmsTools/gatewayConfTool/GatewayConfToolTaskView.fxml"));
        return fXMLLoader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesKeyTableColumn, "key");
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesValueTableColumn, "value");
        propertiesTableView.setItems(propertiesTableData);
        triggerTypeChoiceBox.getItems().addAll(triggerTypeChoiceBoxStrings);
        triggerTypeChoiceBox.setValue(triggerTypeChoiceBox.getItems().get(0));
        JavaFxViewUtil.setSpinnerValueFactory(intervalTimeSpinner, 5, Integer.MAX_VALUE);
        JavaFxViewUtil.setSpinnerValueFactory(executeTimesSpinner, -1, Integer.MAX_VALUE);
        receiverConfigListView.setItems(receiverConfigListData);
        filterConfigsListView.setItems(filterConfigsListData);
        senderConfigListView.setItems(senderConfigListData);
    }

    private void initEvent() {
        receiverConfigListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.easipass.gateway.receiver.bean";
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                String packagePath = packageName.replace(".", "/");
                URL url = loader.getResource(packagePath);
                for (File childFile : new File(url.getPath()).listFiles()) {
                    if (!childFile.getName().contains("$")) {
                        MenuItem menuAdd = new MenuItem(StringUtils.removeEnd(childFile.getName(), ".class"));
                        menuAdd.setOnAction(event1 -> {
                            receiverConfigListData.add(menuAdd.getText());
                        });
                        menu.getItems().add(menuAdd);
                    }
                }
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
//                    Map<String,String> map = dialogTableView.getSelectionModel().getSelectedItem();
//                    Map<String,String> map2 =  new HashMap<String,String>(map);
//                    dialogTableData.add(dialogTableView.getSelectionModel().getSelectedIndex(), map2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    receiverConfigListData.remove(receiverConfigListView.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    receiverConfigListData.clear();
                });
                receiverConfigListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    public void setData(GatewayConfToolController gatewayConfToolController, TaskConfig taskConfig) {
        this.gatewayConfToolController = gatewayConfToolController;
        this.taskConfig = taskConfig;
//        gatewayConfToolTaskViewService.reloadTableData();

        nameTextField.setText(taskConfig.getName());
        isEnableCheckBox.setSelected(taskConfig.getIsEnable());
        taskTypeTextField.setText(taskConfig.getTaskType());
        triggerTypeChoiceBox.setValue(taskConfig.getTriggerType());
        intervalTimeSpinner.getValueFactory().setValue(taskConfig.getIntervalTime());
        executeTimesSpinner.getValueFactory().setValue(taskConfig.getExecuteTimes());
        triggerCronTextField.setText(taskConfig.getTriggerCron());
        isStatefullJobCheckBox.setSelected(taskConfig.getIsStatefulJob());
        receiverConfigListData.clear();
        for (ReceiverConfig receiverConfig : taskConfig.getReceiverConfig()) {
            receiverConfigListData.add(receiverConfig.getServiceName());
        }
        filterConfigsListData.clear();
        for (FilterConfig filterConfig : taskConfig.getFilterConfigs()) {
            filterConfigsListData.add(filterConfig.getServiceName());
        }
        senderConfigListData.clear();
        for (SenderConfig senderConfig : taskConfig.getSenderConfig()) {
            senderConfigListData.add(senderConfig.getServiceName());
        }
        propertiesTableData.clear();
        taskConfig.getProperties().forEach((key, value) -> {
            Map<String, String> map = new HashMap<>();
            map.put("key", key);
            map.put("value", value.toString());
            propertiesTableData.add(map);
        });
    }
}