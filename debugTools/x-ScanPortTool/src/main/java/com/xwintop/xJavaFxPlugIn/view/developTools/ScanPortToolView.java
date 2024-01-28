package com.xwintop.xJavaFxPlugIn.view.developTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName: ScanPortToolView
 * @Description: 端口扫描工具
 * @author: xufeng
 * @date: 2019/5/15 17:33
 */

@Getter
@Setter
public abstract class ScanPortToolView implements Initializable {
    @FXML
    protected TextField hostTextField;
    @FXML
    protected Button scanButton;
    @FXML
    protected TextField diyPortTextField;
    @FXML
    protected TextField ipFilterTextField;
    @FXML
    protected TextField portFilterTextField;
    @FXML
    protected FlowPane commonPortFlowPane;
    @FXML
    protected Button parseDomainButton;
    @FXML
    protected TextField domainIpTextField;
    @FXML
    protected Button getNatIpAddressButton;
    @FXML
    protected TextField natIpTextField;
    @FXML
    protected TextField natIpAddressTextField;
    @FXML
    protected TableView<Map<String, String>> connectStatusTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> ipTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> portTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> statusTableColumn;
}
