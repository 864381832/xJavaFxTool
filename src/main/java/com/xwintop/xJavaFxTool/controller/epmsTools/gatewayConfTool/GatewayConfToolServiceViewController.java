package com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool;

import com.easipass.gateway.filter.bean.FilterConfig;
import com.easipass.gateway.receiver.entity.ReceiverConfig;
import com.easipass.gateway.route.entity.SenderConfig;
import com.jfoenix.controls.JFXCheckBox;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool.GatewayConfToolServiceViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool.GatewayConfToolServiceViewView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class GatewayConfToolServiceViewController extends GatewayConfToolServiceViewView {
    private GatewayConfToolServiceViewService gatewayConfToolServiceViewService = new GatewayConfToolServiceViewService(this);

    private GatewayConfToolTaskViewController gatewayConfToolTaskViewController;
    private String tabName;

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/epmsTools/gatewayConfTool/GatewayConfToolServiceView.fxml"));
        return fXMLLoader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void saveAction(ActionEvent event) {
        gatewayConfToolServiceViewService.saveConfigAction();
    }

    public void setData(GatewayConfToolTaskViewController gatewayConfToolTaskViewController, Object configObject) {
        gatewayConfToolServiceViewService.setConfigObject(configObject);
        this.gatewayConfToolTaskViewController = gatewayConfToolTaskViewController;
        for (Field field : configObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType() == String.class) {
                    Label label = new Label(field.getName() + ":");
                    label.setTextFill(Color.RED);
                    FlowPane.setMargin(label,new Insets(0,-15,0,0));
                    serviceViewFlowPane.getChildren().add(label);
                    TextField textField = new TextField(field.get(configObject) == null ? "" : field.get(configObject).toString());
                    textField.setId(field.getName());
                    if("serviceName".equals(field.getName())){
                        textField.setEditable(false);
                    }
                    serviceViewFlowPane.getChildren().add(textField);
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    JFXCheckBox checkBox = new JFXCheckBox(field.getName());
                    checkBox.setId(field.getName());
                    checkBox.setSelected(field.getBoolean(configObject));
                    serviceViewFlowPane.getChildren().add(checkBox);
                } else if (field.getType() == int.class || field.getType() == long.class) {
                    Label label = new Label(field.getName() + ":");
                    label.setTextFill(Color.RED);
                    FlowPane.setMargin(label,new Insets(0,-15,0,0));
                    serviceViewFlowPane.getChildren().add(label);
                    Spinner<Integer> spinner = new Spinner<>();
                    spinner.setId(field.getName());
                    JavaFxViewUtil.setSpinnerValueFactory(spinner, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.valueOf(field.get(configObject).toString()));
                    spinner.setEditable(true);
                    serviceViewFlowPane.getChildren().add(spinner);
                } else if (field.getType() == Map.class) {
                    Label label = new Label(field.getName() + ":");
                    label.setTextFill(Color.RED);
                    FlowPane.setMargin(label,new Insets(0,-15,0,0));
                    serviceViewFlowPane.getChildren().add(label);
                    TableView<Map<String, String>> propertiesTableView = new TableView<>();
                    propertiesTableView.setId(field.getName());
                    propertiesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    propertiesTableView.setPrefHeight(80);
                    TableColumn<Map<String, String>, String> propertiesKeyTableColumn = new TableColumn<>("key");
                    TableColumn<Map<String, String>, String> propertiesValueTableColumn = new TableColumn<>("value");
                    propertiesTableView.getColumns().add(propertiesKeyTableColumn);
                    propertiesTableView.getColumns().add(propertiesValueTableColumn);
                    JavaFxViewUtil.setTableColumnMapValueFactory(propertiesKeyTableColumn, "key");
                    JavaFxViewUtil.setTableColumnMapValueFactory(propertiesValueTableColumn, "value");
                    ObservableList<Map<String, String>> propertiesTableData = FXCollections.observableArrayList();
                    propertiesTableView.setItems(propertiesTableData);
                    ((Map) field.get(configObject)).forEach((key, value) -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("key", key.toString());
                        map.put("value", value.toString());
                        propertiesTableData.add(map);
                    });
                    JavaFxViewUtil.addTableViewOnMouseRightClickMenu(propertiesTableView);
                    serviceViewFlowPane.getChildren().add(propertiesTableView);
                } else if (field.getType() == List.class) {
                    Label label = new Label(field.getName() + ":");
                    label.setTextFill(Color.RED);
                    FlowPane.setMargin(label,new Insets(0,-15,0,0));
                    serviceViewFlowPane.getChildren().add(label);
                    ListView<String> listView = new ListView<>();
                    listView.setId(field.getName());
                    listView.setPrefHeight(80);
                    ObservableList<String> listData = FXCollections.observableArrayList();
                    listView.setItems(listData);
                    listData.addAll((List) field.get(configObject));
                    JavaFxViewUtil.addListViewOnMouseRightClickMenu(listView);
                    serviceViewFlowPane.getChildren().add(listView);
                } else {
                    Label label = new Label(field.getName() + ":");
                    label.setTextFill(Color.RED);
                    FlowPane.setMargin(label,new Insets(0,-15,0,0));
                    serviceViewFlowPane.getChildren().add(label);
                    TextField textField = new TextField(field.get(configObject) == null ? "" : field.get(configObject).toString());
                    textField.setId(field.getName());
                    serviceViewFlowPane.getChildren().add(textField);
                }
//                FieldUtils.readField(field, configObject, true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (configObject instanceof ReceiverConfig) {
        } else if (configObject instanceof FilterConfig) {
        } else if (configObject instanceof SenderConfig) {
        }
    }
}