package com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool;

import com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool.GatewayConfToolService;
import com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool.GatewayConfToolView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class GatewayConfToolController extends GatewayConfToolView {
    private GatewayConfToolService gatewayConfToolService = new GatewayConfToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        TreeItem<String> treeItem = new TreeItem<String>("TaskConfig列表");
        configurationTreeView.setRoot(treeItem);
        configurationPathTextField.setText("E:\\ideaWorkspaces\\gatewaySpring\\configuration");
    }

    private void initEvent() {
        configurationTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observable,
                                        TreeItem<String> oldValue, TreeItem<String> newValue) {
                        String name = newValue.getValue();
                        if (newValue.getChildren().size() == 0) {
                            gatewayConfToolService.addTaskConfigTabPane(newValue.getParent().getValue(), name);
                        }
                    }
                });
    }

    private void initService() {
    }

    @FXML
    private void treeRefurbishAction(ActionEvent event) {
        gatewayConfToolService.reloadTaskConfigFile();
    }

    @FXML
    private void connectAction(ActionEvent event) {
    }
}