package com.xwintop.xJavaFxTool.controller.index;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.PluginManageService;
import com.xwintop.xJavaFxTool.view.index.PluginManageView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

    public static final String FXML = "/com/xwintop/xJavaFxTool/fxmlView/index/PluginManage.fxml";

    private PluginManageService pluginManageService = new PluginManageService(this);

    private ObservableList<Map<String, String>> originPluginData = FXCollections.observableArrayList();

    private FilteredList<Map<String, String>> pluginDataTableData = new FilteredList<>(originPluginData, m -> true);

    private IndexController indexController;

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
            (mouseEvent, index) -> {
                pluginManageService.setIsEnableTableColumn(index);
            });

        // TODO 实现插件的启用禁用

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
                                downloadButton.setOnMouseClicked((me) -> {
                                    try {
                                        pluginManageService.downloadPluginJar(dataRow);
                                        dataRow.put("isEnableTableColumn", "true");
                                        dataRow.put("isDownloadTableColumn", "已下载");
                                        downloadButton.setText("已下载");
                                        downloadButton.setDisable(true);
                                        pluginDataTableView.refresh();
                                        TooltipUtil.showToast("插件 " + dataRow.get("nameTableColumn") + " 下载完成");
                                    } catch (Exception e) {
                                        log.error("下载插件失败：", e);
                                        TooltipUtil.showToast("下载插件失败：" + e.getMessage());
                                    }
                                });
                                this.setGraphic(downloadButton);
                            }
                        }
                    };
                }
            });

        pluginDataTableView.setItems(pluginDataTableData);
    }

    private void initEvent() {

        // 右键菜单
        MenuItem mnuSavePluginConfig = new MenuItem("保存配置");
        mnuSavePluginConfig.setOnAction(ev -> {
            try {
                PluginManager.getInstance().saveToFile();
                TooltipUtil.showToast("保存配置成功");
            } catch (Exception ex) {
                log.error("保存插件配置失败", ex);
            }
        });

        ContextMenu contextMenu = new ContextMenu(mnuSavePluginConfig);
        pluginDataTableView.setContextMenu(contextMenu);

        // 搜索
        selectPluginTextField.textProperty().addListener((_ob, _old, _new) -> {
            pluginManageService.searchPlugin(_new);
        });
    }

    private void initService() {
        pluginManageService.getPluginList();
    }

    @FXML
    private void selectPluginAction(ActionEvent event) {
        pluginManageService.searchPlugin(selectPluginTextField.getText());
    }
}