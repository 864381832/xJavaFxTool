package com.xwintop.xJavaFxPlugIn.controller.developTools;

import com.xwintop.xJavaFxPlugIn.services.developTools.ScanPortToolService;
import com.xwintop.xJavaFxPlugIn.view.developTools.ScanPortToolView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: ScanPortToolController
 * @Description: 端口扫描工具
 * @author: xufeng
 * @date: 2019/5/15 17:33
 */

@Getter
@Setter
@Slf4j
public class ScanPortToolController extends ScanPortToolView {
    private ScanPortToolService scanPortToolService = new ScanPortToolService(this);
    private ObservableList<Map<String, String>> connectStatusTableData = FXCollections.observableArrayList();
    String[] portStrings = new String[]{
            "ftp:21,22,telnet:23,smtp:25,http:80",
            "dns:53,tftp:69,snmp:161,162",
            "1158,1433,1521,2100,3128,26,69",
            "3306,3389,7001,8080,8081,110,143",
            "9080,9090,43958,443,465,995,1080",
            "79,88,113,220",
            "1-65535"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        for (String portString : portStrings) {
            CheckBox checkBox = new CheckBox(portString);
            checkBox.setId(portString);
            if (!"1-65535".equals(portString)) {
                checkBox.setSelected(true);
            }
            commonPortFlowPane.getChildren().add(checkBox);
        }
        JavaFxViewUtil.setTableColumnMapValueFactory(ipTableColumn, "ip");
        JavaFxViewUtil.setTableColumnMapValueFactory(portTableColumn, "port");
        JavaFxViewUtil.setTableColumnMapValueFactory(statusTableColumn, "status");
        connectStatusTableView.setItems(connectStatusTableData);
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void scanAction(ActionEvent event) {
        try {
            scanPortToolService.scanAction();
        } catch (Exception e) {
            log.error("扫描异常：", e);
        }
    }

    @FXML
    private void parseDomainAction(ActionEvent event) {
        try {
            scanPortToolService.parseDomainAction();
        } catch (Exception e) {
            log.error("解析域名异常：", e);
        }
    }

    @FXML
    private void getNatIpAddressAction(ActionEvent event) {
        try {
            scanPortToolService.getNatIpAddressAction();
        } catch (Exception e) {
            log.error("获取外网ip异常：", e);
        }
    }
}
