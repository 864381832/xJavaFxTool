package com.xwintop.xJavaFxTool.services.index;

import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.javafx.dialog.FxProgressDialog;
import com.xwintop.xcore.javafx.dialog.ProgressTask;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 插件管理
 *
 * @author xufeng
 */
@Getter
@Setter
@Slf4j
public class PluginManageService {
    private PluginManageController pluginManageController;

    private PluginManager pluginManager = PluginManager.getInstance();

    public PluginManageService(PluginManageController pluginManageController) {
        this.pluginManageController = pluginManageController;
    }

    public void getPluginList() {
        pluginManager.loadServerPlugins();
        pluginManager.getPluginList().forEach(this::addDataRow);
    }

    public void addDataRow(PluginJarInfo plugin) {
        Map<String, String> dataRow = new HashMap<>();
        dataRow.put("nameTableColumn", plugin.getName());
        dataRow.put("synopsisTableColumn", plugin.getSynopsis());
        dataRow.put("versionTableColumn", plugin.getVersion());
        dataRow.put("jarName", plugin.getJarName());
        dataRow.put("downloadUrl", plugin.getDownloadUrl());
        dataRow.put("versionNumber", String.valueOf(plugin.getVersionNumber()));

        if (plugin.getIsDownload() == null || !plugin.getIsDownload()) {
            dataRow.put("isDownloadTableColumn", "下载");
            dataRow.put("isEnableTableColumn", "false");
        } else {
            if (plugin.getLocalVersionNumber() != null &&
                plugin.getVersionNumber() > plugin.getLocalVersionNumber()) {
                dataRow.put("isDownloadTableColumn", "更新");
            } else {
                dataRow.put("isDownloadTableColumn", "已下载");
            }
            dataRow.put("isEnableTableColumn", plugin.getIsEnable().toString());
        }

        pluginManageController.getOriginPluginData().add(dataRow);
    }

    public void downloadPluginJar(Map<String, String> dataRow, Consumer<PluginJarInfo> afterDownload) {

        String jarName = dataRow.get("jarName");
        PluginJarInfo pluginJarInfo = pluginManager.getPlugin(jarName);

        ProgressTask progressTask = new ProgressTask() {
            @Override
            protected void execute() throws Exception {
                pluginManager.downloadPlugin(
                    pluginJarInfo, (total, current) -> updateProgress(current, total)
                );

                if (afterDownload != null) {
                    afterDownload.accept(pluginJarInfo);
                }
            }
        };

        Window controllerWindow = pluginManageController.getWindow();
        FxProgressDialog dialog = FxProgressDialog.create(controllerWindow, progressTask, "正在下载插件 " + pluginJarInfo.getName() + "...");

        progressTask.setOnCancelled(event -> {
            throw new AppException("下载被取消。");
        });

        progressTask.setOnFailed(event -> {
            Throwable e = event.getSource().getException();
            if (e != null) {
                log.error("", e);
                FxAlerts.error(controllerWindow, "下载插件失败", e);
            } else {
                FxAlerts.error(controllerWindow, "下载失败", event.getSource().getMessage());
            }
        });

        dialog.show();
    }

    public void setIsEnableTableColumn(Integer index) {
        Map<String, String> dataRow = pluginManageController.getOriginPluginData().get(index);
        String jarName = dataRow.get("jarName");
        PluginJarInfo pluginJarInfo = this.pluginManager.getPlugin(jarName);
        if (pluginJarInfo != null) {
            pluginJarInfo.setIsEnable(Boolean.parseBoolean(dataRow.get("isEnableTableColumn")));
        }
    }

    public void searchPlugin(String keyword) {
        pluginManageController.getPluginDataTableData().setPredicate(map -> {
            if (StringUtils.isBlank(keyword)) {
                return true;
            } else {
                return isPluginDataMatch(map, keyword);
            }
        });
    }

    private boolean isPluginDataMatch(Map<String, String> map, String keyword) {
        return map.entrySet().stream().anyMatch(
            entry -> !entry.getKey().equals("downloadUrl") && entry.getValue().toLowerCase().contains(keyword.toLowerCase())
        );
    }

    /**
     * 判断插件是否启用
     */
    public static boolean isPluginEnabled(String fileName) {
        String jarName = StringUtils.substringBeforeLast(fileName, "-");
        PluginJarInfo pluginJarInfo = PluginManager.getInstance().getPlugin(jarName);
        if (pluginJarInfo == null) {
            return false;
        }
        Boolean isEnable = pluginJarInfo.getIsEnable();
        return isEnable != null && isEnable;
    }
}
