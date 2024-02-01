package com.xwintop.xJavaFxTool.view.javaFxTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: ShowSystemInfoView
 * @Description: 显示系统信息
 * @author: xufeng
 * @date: 2017/11/28 22:16
 */
@Getter
@Setter
public abstract class ShowSystemInfoView implements Initializable {
    @FXML
    protected TabPane mainTabPane;
    @FXML
    protected Tab overviewTab;
    @FXML
    protected LineChart overviewCpuLineChart;
    @FXML
    protected LineChart overviewMemoryLineChart;
    @FXML
    protected LineChart overviewDiskLineChart;
    @FXML
    protected LineChart overviewNetLineChart;
    @FXML
    protected Tab cpuTab;
    @FXML
    protected TextArea systemInfoTextArea;
    @FXML
    protected Tab diskTab;
    @FXML
    protected WebView diskWebView;
    @FXML
    protected Tab vmTab;
    @FXML
    protected TextArea vmTextArea;

}