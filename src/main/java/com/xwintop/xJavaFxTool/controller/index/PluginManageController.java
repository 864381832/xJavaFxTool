package com.xwintop.xJavaFxTool.controller.index;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.index.PluginManageService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.index.PluginManageView;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: PluginManageController
 * @Description: 插件管理
 * @author: xufeng
 * @date: 2020/1/19 17:41
 */

@Getter
@Setter
@Slf4j
public class PluginManageController extends PluginManageView {
    private PluginManageService pluginManageService = new PluginManageService(this);
    private ObservableList<Map<String, String>> pluginDataTableData = FXCollections.observableArrayList();

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/index/PluginManage.fxml"));
        return fXMLLoader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(nameTableColumn, "nameTableColumn");
        JavaFxViewUtil.setTableColumnMapValueFactory(synopsisTableColumn, "synopsisTableColumn");
        JavaFxViewUtil.setTableColumnMapValueFactory(versionTableColumn, "versionTableColumn");
        JavaFxViewUtil.setTableColumnMapValueFactory(isDownloadTableColumn, "isDownloadTableColumn");
        JavaFxViewUtil.setTableColumnMapAsCheckBoxValueFactory(isEnableTableColumn, "isEnableTableColumn");
        JavaFxViewUtil.setTableColumnButonFactory(downloadTableColumn, "下载", (me, index) -> {
            try {
                Map<String, String> dataRow = pluginDataTableData.get(index);
                String downloadUrl = dataRow.get("downloadUrl");
                pluginManageService.downloadPluginJar(downloadUrl);
                TooltipUtil.showToast("下载插件完成");
            } catch (Exception e) {
                log.error("下载插件失败：", e);
                TooltipUtil.showToast("下载插件失败：" + e.getMessage());
            }
        });
        pluginDataTableView.setItems(pluginDataTableData);
    }

    private void initEvent() {
    }

    private void initService() {
        pluginManageService.getPluginList();
    }

    @FXML
    private void selectPluginAction(ActionEvent event) {
    }
}