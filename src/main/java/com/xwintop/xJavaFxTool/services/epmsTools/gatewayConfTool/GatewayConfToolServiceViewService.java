package com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool;

import com.jfoenix.controls.JFXCheckBox;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolServiceViewController;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class GatewayConfToolServiceViewService {
    private GatewayConfToolServiceViewController gatewayConfToolServiceViewController;

    private Object configObject;

    public GatewayConfToolServiceViewService(GatewayConfToolServiceViewController gatewayConfToolServiceViewController) {
        this.gatewayConfToolServiceViewController = gatewayConfToolServiceViewController;
    }

    public void saveConfigAction() {
        for (Field field : configObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            for (Node node : gatewayConfToolServiceViewController.getServiceViewFlowPane().getChildren()) {
                if (field.getName().equals(node.getId())) {
                    try {
                        if (field.getType() == String.class) {
                            if (StringUtils.isBlank(((TextField) node).getText())) {
                                field.set(configObject, null);
                            } else {
                                field.set(configObject, ((TextField) node).getText());
                            }
                        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                            field.set(configObject, ((JFXCheckBox) node).isSelected());
                        } else if (field.getType() == int.class || field.getType() == long.class) {
                            if (((Spinner<Integer>) node).getValue() != null) {
                                field.set(configObject, ((Spinner<Integer>) node).getValue());
                            }
                        } else if (field.getType() == Map.class) {
                            TableView<Map<String, String>> propertiesTableView = (TableView<Map<String, String>>) node;
                            Map<String, String> propertiesMap = new HashMap<>();
                            propertiesTableView.getItems().forEach(map -> {
                                propertiesMap.put(map.get("key"), map.get("value"));
                            });
                            field.set(configObject, propertiesMap);
                        } else if (field.getType() == List.class) {
                            List<String> list = new ArrayList<>(((ListView<String>) node).getItems());
                            field.set(configObject, list);
                        } else if (field.getType() == Integer.class) {
                            if (StringUtils.isBlank(((TextField) node).getText())) {
                                field.set(configObject, null);
                            } else {
                                field.set(configObject, Integer.valueOf(((TextField) node).getText()));
                            }
                        } else if (field.getType() == Long.class) {
                            if (StringUtils.isBlank(((TextField) node).getText())) {
                                field.set(configObject, null);
                            } else {
                                field.set(configObject, Long.valueOf(((TextField) node).getText()));
                            }
                        } else {
                            if (StringUtils.isBlank(((TextField) node).getText())) {
                                field.set(configObject, null);
                            } else {
                                field.set(configObject, ((TextField) node).getText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}