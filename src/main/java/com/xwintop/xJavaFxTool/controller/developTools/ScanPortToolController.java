package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.ScanPortToolService;
import com.xwintop.xJavaFxTool.view.developTools.ScanPortToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class ScanPortToolController extends ScanPortToolView {
    private ScanPortToolService scanPortToolService = new ScanPortToolService(this);

    String str2 = "ftp:21,22,telnet:23,smtp:25,http:80";
    String str3 = "dns:53,tftp:69,snmp:161,162";
    String str4 = "1158,1433,1521,2100,3128,26,69";
    String str5 = "3306,3389,7001,8080,8081,110,143";
    String str6 = "9080,9090,43958,443,465,995,1080";
    Integer[] ports = new Integer[] { 21, 22, 23, 25, 26, 53, 69, 80, 110, 143, 443, 465, 69, 161, 162, 135, 995, 1080,
            1158, 1433, 1521, 2100, 3128, 3306, 3389, 7001, 8080, 8081, 9080, 9090, 43958, 135, 445, 1025, 1026, 1027,
            1028, 1055, 5357 };
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
    private void scanAction(ActionEvent event) {
    }
}