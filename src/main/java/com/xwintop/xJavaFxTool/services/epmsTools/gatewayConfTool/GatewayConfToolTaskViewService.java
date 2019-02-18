package com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool;

import com.easipass.gateway.filter.bean.FilterConfig;
import com.easipass.gateway.receiver.entity.ReceiverConfig;
import com.easipass.gateway.route.entity.SenderConfig;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolServiceViewController;
import com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool.GatewayConfToolTaskViewController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class GatewayConfToolTaskViewService {
    private GatewayConfToolTaskViewController gatewayConfToolTaskViewController;

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
}