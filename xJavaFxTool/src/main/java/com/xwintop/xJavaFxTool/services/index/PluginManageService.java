package com.xwintop.xJavaFxTool.services.index;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.event.AppEvents;
import com.xwintop.xJavaFxTool.event.PluginEvent;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.javafx.dialog.FxProgressDialog;
import com.xwintop.xcore.javafx.dialog.ProgressTask;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
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
    public static final String SERVER_PLUGINS_URL = "https://xwintop.gitee.io/maven/plugin-libs/plugin-list.json";

    private PluginManageController pluginManageController;

    private PluginManager pluginManager = PluginManager.getInstance();

    private Map<String, PluginJarInfo> pluginJarInfoMap = new ConcurrentHashMap<>();

    public PluginManageService(PluginManageController pluginManageController) {
        this.pluginManageController = pluginManageController;
    }

    public void getPluginList() {
        pluginManager.getPluginList().forEach(this::addDataRow);
        CompletableFuture.runAsync(this::loadServerPlugins);
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
            if (plugin.getLocalVersionNumber() != null && plugin.getVersionNumber() > plugin.getLocalVersionNumber()) {
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
        PluginJarInfo pluginJarInfo = pluginJarInfoMap.get(jarName);
        ProgressTask progressTask = new ProgressTask() {
            @Override
            protected void execute() throws Exception {
                PluginManageService.this.downloadPlugin(
                    pluginJarInfo, (total, current) -> updateProgress(current, total)
                );
                if (afterDownload != null) {
                    afterDownload.accept(pluginJarInfo);
                }
            }
        };
        Window controllerWindow = pluginManageController.getPluginDataTableView().getScene().getWindow();
        FxProgressDialog dialog = FxProgressDialog.create(controllerWindow, progressTask, "正在下载插件 " + pluginJarInfo.getName() + "...");
        progressTask.setOnCancelled(event -> {
            throw new AppException("下载被取消。");
        });
        progressTask.setOnFailed(event -> {
            Throwable e = event.getSource().getException();
            if (e != null) {
                log.error("下载插件失败", e);
                FxAlerts.error(controllerWindow, "下载插件失败", e);
            } else {
                FxAlerts.error(controllerWindow, "下载失败", event.getSource().getMessage());
            }
        });
        dialog.showAndWait();
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

    public void loadServerPlugins() {
        try {
            String json = HttpUtil.get(SERVER_PLUGINS_URL);
            List<PluginJarInfo> pluginJarInfoList = JSON.parseArray(json, PluginJarInfo.class);
            pluginManageController.getOriginPluginData().clear();
            pluginJarInfoList.forEach(plugin -> {
                pluginJarInfoMap.put(plugin.getJarName(), plugin);
                PluginJarInfo exists = pluginManager.getPlugin(plugin.getJarName());
                if (exists != null) {
                    plugin.setLocalVersionNumber(exists.getLocalVersionNumber());
                    plugin.setIsEnable(exists.getIsEnable());
                    plugin.setIsDownload(exists.getIsDownload());
                }
                addDataRow(plugin);
            });
            log.info("下载插件列表完成。");
        } catch (Exception e) {
            log.error("下载插件列表失败", e);
        }
    }

    //删除插件
    public void deletePlugin() {
        Integer index = pluginManageController.getPluginDataTableView().getSelectionModel().getSelectedIndex();
        if (index == null || index == -1) {
            return;
        }
        Map<String, String> dataRow = pluginManageController.getOriginPluginData().get(index);
        String jarName = dataRow.get("jarName");
        PluginJarInfo pluginJarInfo = this.pluginManager.getPlugin(jarName);
        if (pluginJarInfo == null || BooleanUtils.isNotTrue(pluginJarInfo.getIsDownload())) {
            FxAlerts.info("提示", pluginJarInfo.getName() + " 该插件未下载");
            return;
        }
        if (!FxAlerts.confirmYesNo("删除插件", String.format("确定要删除插件 %s 吗？", pluginJarInfo.getName()))) {
            return;
        }
        if (pluginJarInfo != null) {
            try {
                FileUtils.delete(pluginJarInfo.getFile());
                dataRow.put("isEnableTableColumn", "false");
                dataRow.put("isDownloadTableColumn", "下载");
                PluginJarInfo pluginJarInfo1 = this.pluginJarInfoMap.get(jarName);
                pluginJarInfo1.setIsEnable(false);
                pluginJarInfo1.setIsDownload(false);
                this.pluginManager.getPluginList().remove(pluginJarInfo);
                PluginManager.getInstance().saveToFile();
                pluginManageController.getPluginDataTableView().refresh();
                AppEvents.fire(new PluginEvent(PluginEvent.PLUGIN_DOWNLOADED, pluginJarInfo));
            } catch (IOException e) {
                log.error("删除插件失败", e);
            }
        }
    }

    public void downloadPlugin(PluginJarInfo pluginJarInfo, BiConsumer<Long, Long> onProgressUpdate) throws IOException {
        File file = pluginJarInfo.getFile();
        FileUtils.forceMkdirParent(file);
        HttpResponse response = HttpUtil.createGet(pluginJarInfo.getDownloadUrl(), true).executeAsync();
        response.writeBodyForFile(file, new StreamProgress() {
            @Override
            public void start() {
            }

            @Override
            public void progress(long progressSize, long contentLength) {
                onProgressUpdate.accept(contentLength, progressSize);
            }

            @Override
            public void finish() {
            }
        });
        // 下载完毕
        pluginJarInfo.setIsDownload(true);
        pluginJarInfo.setIsEnable(true);
        pluginJarInfo.setLocalVersionNumber(pluginJarInfo.getVersionNumber());
    }
}
