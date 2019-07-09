package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolDataSourceService;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolServiceViewService;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolDataSourceView;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: TransferToolDataSourceController
 * @Description: 数据源配置
 * @author: xufeng
 * @date: 2019/6/28 18:00
 */

@Getter
@Setter
@Slf4j
public class TransferToolDataSourceController extends TransferToolDataSourceView {
    private TransferToolDataSourceService transferToolDataSourceService = new TransferToolDataSourceService(this);

    private DataSourceConfigDruid dataSourceConfigDruid;

    private String fileName;
    private String tabName;

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/developTools/xTransferTool/TransferToolDataSource.fxml"));
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
    }

    private void initService() {
    }

    @FXML
    private void saveAction(ActionEvent event) {
        try {
            transferToolDataSourceService.saveConfigAction();
        } catch (Exception e) {
            TooltipUtil.showToast("保存配置失败：" + e.getMessage());
            log.error("保存配置失败：", e);
        }
    }

    @FXML
    private void viewAction(ActionEvent event) {
        try {
            transferToolDataSourceService.viewTaskConfigAction();
        } catch (Exception e) {
            TooltipUtil.showToast("保存配置失败：" + e.getMessage());
            log.error("保存配置失败：", e);
        }
    }

    public void setData(TransferToolController transferToolController, DataSourceConfigDruid dataSourceConfigDruid) {
        this.dataSourceConfigDruid = dataSourceConfigDruid;
        this.transferToolDataSourceService.setTransferToolController(transferToolController);
        new TransferToolServiceViewService(null).addFlowPane(this.dataSourceConfigDruid, this.dataSourceFlowPane);
    }
}
