package com.xwintop.xJavaFxTool.controller.epmsTools.gatewayConfTool;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.epmsTools.gatewayConfTool.GatewayConfToolTaskViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool.GatewayConfToolTaskViewView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class GatewayConfToolTaskViewController extends GatewayConfToolTaskViewView {
    private GatewayConfToolTaskViewService gatewayConfToolTaskViewService = new GatewayConfToolTaskViewService(this);
    private ObservableList<Map<String, String>> propertiesTableData = FXCollections.observableArrayList();
    private String[] triggerTypeChoiceBoxStrings = new String[] { "SIMPLE", "CRON" };

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
    }

    private void initEvent() {
    }

    private void initService() {
    }

    public void setData(GatewayConfToolController gatewayConfToolController) {
        this.gatewayConfToolController = gatewayConfToolController;
//        gatewayConfToolTaskViewService.reloadTableData();
    }
}