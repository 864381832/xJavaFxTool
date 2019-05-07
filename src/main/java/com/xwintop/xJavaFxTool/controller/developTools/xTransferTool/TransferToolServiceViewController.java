package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolServiceViewService;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolServiceViewView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class TransferToolServiceViewController extends TransferToolServiceViewView {
    private TransferToolServiceViewService transferToolServiceViewService = new TransferToolServiceViewService(this);

    private TransferToolTaskViewController transferToolTaskViewController;
    private String tabName;

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/developTools/xTransferTool/TransferToolServiceView.fxml"));
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
        rowTabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_RemoveAll = new MenuItem("关闭所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    rowTabPane.getTabs().clear();
                    transferToolServiceViewService.getRowTabMap().clear();
                });
                rowTabPane.setContextMenu(new ContextMenu(menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void saveAction(ActionEvent event) {
        transferToolServiceViewService.saveConfigAction(transferToolServiceViewService.getConfigObject(), serviceViewFlowPane);
    }

    public void setData(TransferToolTaskViewController xTransferToolTaskViewController, Object configObject) {
        transferToolServiceViewService.setConfigObject(configObject);
        this.transferToolTaskViewController = xTransferToolTaskViewController;
        transferToolServiceViewService.addFlowPane(configObject, serviceViewFlowPane);
    }

}