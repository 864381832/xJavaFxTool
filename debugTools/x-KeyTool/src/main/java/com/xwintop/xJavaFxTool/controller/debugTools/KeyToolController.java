package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.view.debugTools.KeyToolView;
import com.xwintop.xJavaFxTool.services.debugTools.KeyToolService;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Getter
@Setter
@Slf4j
public class KeyToolController extends KeyToolView {
    private KeyToolService keyToolService = new KeyToolService(this);
    private ObservableList<Map<String, String>> propertiesTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setPasswordTextFieldFactory(storepassTextField);
        JavaFxViewUtil.setPasswordTextFieldFactory(keypassTextField);
        JavaFxViewUtil.setSpinnerValueFactory(keysizeSpinner, 0, Integer.MAX_VALUE, 1024);
        JavaFxViewUtil.setSpinnerValueFactory(validitySpinner, 0, Integer.MAX_VALUE, 3650);
        JavaFxViewUtil.setSpinnerValueFactory(consumerAmountSpinner, 0, Integer.MAX_VALUE, 1);
        issuedTimeDatePicker.setValue(LocalDate.now());
        notBeforeDatePicker.setValue(LocalDate.now());
        notAfterDatePicker.setValue(LocalDate.of(new Date().getYear() + 1900 + 10, new Date().getMonth() + 1, new Date().getDate()));
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesKeyTableColumn, "key");
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesValueTableColumn, "value");
        propertiesTableView.setItems(propertiesTableData);
        Map ipMap = new HashMap();
        ipMap.put("key", "ip");
        ipMap.put("value", "*");
        propertiesTableData.add(ipMap);
        Map macMap = new HashMap();
        macMap.put("key", "mac");
        macMap.put("value", "*");
        propertiesTableData.add(macMap);
    }

    private void initEvent() {
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(propertiesTableView);
    }

    private void initService() {
    }

    @FXML
    private void genkeypairOnAction(ActionEvent event) {
        keyToolService.genkeypairOnAction();
    }

    @FXML
    private void exportcertOnAction(ActionEvent event) {
        keyToolService.exportcertOnAction();
    }

    @FXML
    private void importOnAction(ActionEvent event) {
        keyToolService.importOnAction();
    }

    @FXML
    private void createLicenseOnAction(ActionEvent event) {
        keyToolService.createLicenseOnAction();
    }

    @FXML
    private void verifyLicenseOnAction(ActionEvent event) {
        keyToolService.verifyLicenseOnAction();
    }
}