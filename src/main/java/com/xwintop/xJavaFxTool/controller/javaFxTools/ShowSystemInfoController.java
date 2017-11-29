package com.xwintop.xJavaFxTool.controller.javaFxTools;

import com.xwintop.xJavaFxTool.services.javaFxTools.ShowSystemInfoService;
import com.xwintop.xJavaFxTool.view.javaFxTools.ShowSystemInfoView;

import java.net.URL;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

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
        showSystemInfoService.showOverviewCpuLineChart();
        showSystemInfoService.showOverviewMemoryLineChart();
        showSystemInfoService.showOverviewDiskLineChart();
        showSystemInfoService.showOverviewNetLineChart();
        showSystemInfoService.showDidkInfo();
        showSystemInfoService.showVmInfo();
    }

    private void initEvent() {
    }

    private void initService() {
    }
}