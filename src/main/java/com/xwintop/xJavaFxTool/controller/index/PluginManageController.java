package com.xwintop.xJavaFxTool.controller.index;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.event.AppEvents;
import com.xwintop.xJavaFxTool.event.PluginEvent;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.plugin.PluginParser;
import com.xwintop.xJavaFxTool.services.index.PluginManageService;
import com.xwintop.xJavaFxTool.view.index.PluginManageView;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 插件管理
 *
 * @author xufeng
 * @since 2020/1/19 17:41
 */

@Getter
@Setter
@Slf4j
public class PluginManageController extends PluginManageView {
    public static final String FXML = "/com/xwintop/xJavaFxTool/fxmlView/index/PluginManage.fxml";

    private PluginManageService pluginManageService = new PluginManageService(this);

    private ObservableList<Map<String, String>> originPluginData = FXCollections.observableArrayList();

    private FilteredList<Map<String, String>> pluginDataTableData = new FilteredList<>(originPluginData, m -> true);

    public static FXMLLoader getFXMLLoader() {
        return new FXMLLoader(IndexController.class.getResource(FXML));
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
        JavaFxViewUtil.setTableColumnMapAsCheckBoxValueFactory(isEnableTableColumn, "isEnableTableColumn",
            (mouseEvent, index) -> pluginManageService.setIsEnableTableColumn(index)
        );

        downloadTableColumn.setCellFactory(
            new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>() {
                @Override
                public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param) {
                    return new TableCell<Map<String, String>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            this.setText(null);
                            this.setGraphic(null);
                            if (!empty) {
                                Map<String, String> dataRow = pluginDataTableData.get(this.getIndex());
                                Button downloadButton = new Button(dataRow.get("isDownloadTableColumn"));
                                if ("已下载".equals(dataRow.get("isDownloadTableColumn"))) {
                                    downloadButton.setDisable(true);
                                }
                                this.setContentDisplay(ContentDisplay.CENTER);
                                downloadButton.setOnMouseClicked(event -> downloadPlugin(dataRow));
                                this.setGraphic(downloadButton);
                            }
                        }
                    };
                }
            });

        pluginDataTableView.setItems(pluginDataTableData);
    }

    private void downloadPlugin(Map<String, String> dataRow) {
        try {
            pluginManageService.downloadPluginJar(dataRow, pluginJarInfo ->
                Platform.runLater(() -> afterDownload(dataRow, pluginJarInfo))
            );
        } catch (Exception e) {
            log.error("下载插件失败：", e);
            TooltipUtil.showToast("下载插件失败：" + e.getMessage());
        }
    }

    private void afterDownload(Map<String, String> dataRow, PluginJarInfo pluginJarInfo) {
        // 没有下载成功不做处理
        if (pluginJarInfo.getIsDownload() == null || !pluginJarInfo.getIsDownload()) {
            return;
        }
        try {
            PluginJarInfo pluginJarInfoOld = PluginManager.getInstance().getPlugin(pluginJarInfo.getJarName());
            if (pluginJarInfoOld != null) {
                FileUtils.delete(pluginJarInfoOld.getFile());
                PluginManager.getInstance().getPluginList().remove(pluginJarInfoOld);
            }
            PluginManager.getInstance().getPluginList().add(pluginJarInfo);
            TooltipUtil.showToast("插件 " + dataRow.get("nameTableColumn") + " 下载完成");
            PluginParser.parse(pluginJarInfo.getFile(), pluginJarInfo);
            PluginManager.getInstance().saveToFile();
            dataRow.put("isEnableTableColumn", "true");
            dataRow.put("isDownloadTableColumn", "已下载");
            pluginDataTableView.refresh();
            AppEvents.fire(new PluginEvent(PluginEvent.PLUGIN_DOWNLOADED, pluginJarInfo));
        } catch (IOException e) {
            log.error("", e);
            FxAlerts.error("处理文件失败", e);
        }
    }

    private void initEvent() {
        // 右键菜单
        ContextMenu contextMenu = new ContextMenu();
        JavaFxViewUtil.addMenuItem(contextMenu, "保存配置", actionEvent -> {
            try {
                PluginManager.getInstance().saveToFile();
                TooltipUtil.showToast("保存配置成功");
            } catch (Exception ex) {
                log.error("保存插件配置失败", ex);
            }
        });
        JavaFxViewUtil.addMenuItem(contextMenu, "删除插件", actionEvent -> pluginManageService.deletePlugin());
        pluginDataTableView.setContextMenu(contextMenu);
        // 搜索
        selectPluginTextField.textProperty().addListener((_ob, _old, _new) -> pluginManageService.searchPlugin(_new));
    }

    private void initService() {
        pluginManageService.getPluginList();
    }

    public void searchPlugin() {
        pluginManageService.searchPlugin(selectPluginTextField.getText());
    }
}