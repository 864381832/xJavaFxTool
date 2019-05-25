package com.xwintop.xJavaFxTool.controller.javaFxTools;

import cn.hutool.core.thread.ThreadUtil;
import com.xwintop.xJavaFxTool.services.javaFxTools.ShowSystemInfoService;
import com.xwintop.xJavaFxTool.view.javaFxTools.ShowSystemInfoView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ShowSystemInfoController
 * @Description: 显示系统信息
 * @author: xufeng
 * @date: 2017/11/28 22:16
 */
@Getter
@Setter
@Log4j
public class ShowSystemInfoController extends ShowSystemInfoView {

    private ShowSystemInfoService showSystemInfoService = new ShowSystemInfoService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        diskWebView.getEngine().load(ShowSystemInfoService.class.getResource("/com/xwintop/xJavaFxTool/web/javaFxTools/ShowSystemInfo/diskInfohCharts.html").toExternalForm());
        showSystemInfoService.showOverviewCpuLineChart();
        showSystemInfoService.showOverviewMemoryLineChart();
        showSystemInfoService.showOverviewDiskLineChart();
//        showSystemInfoService.showOverviewNetLineChart();
//        showSystemInfoService.showDiskInfo();
        ThreadUtil.execute(() -> {
            showSystemInfoService.showSystemInfo();
        });
        ThreadUtil.execute(() -> {
            showSystemInfoService.showVmInfo();
        });
    }

    private void initEvent() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if (newValue == diskTab) {
                    System.out.println(newValue.getText());
                    showSystemInfoService.showDiskInfo();
                }
            }
        });
    }

    private void initService() {
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) {
        System.out.println("移除所以线程");
        showSystemInfoService.stopTimerList();
    }

}