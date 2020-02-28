package com.xwintop.xJavaFxTool.services.index;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: PluginManageService
 * @Description: 插件管理
 * @author: xufeng
 * @date: 2020/1/19 17:41
 */

@Getter
@Setter
@Slf4j
public class PluginManageService {

    public static final String PLUGIN_LIST_URL = "https://xwintop.gitee.io/maven/plugin-libs/plugin-list.json";

    public static final String PLUGIN_LIST_PATH = "system_plugin_list.json";

    private PluginManageController pluginManageController;

    private PluginManager pluginManager = PluginManager.getInstance();

    public PluginManageService(PluginManageController pluginManageController) {
        this.pluginManageController = pluginManageController;
    }

    public void getPluginList() {
        pluginManager.loadServerPlugins();
        pluginManager.getServerPluginList().forEach(this::addDataRow);
    }

    private void addDataRow(PluginJarInfo serverPlugin) {

        PluginJarInfo localPlugin = pluginManager.getLocalPlugin(serverPlugin.getJarName());

        Map<String, String> dataRow = new HashMap<>();
        dataRow.put("nameTableColumn", serverPlugin.getName());
        dataRow.put("synopsisTableColumn", serverPlugin.getSynopsis());
        dataRow.put("versionTableColumn", serverPlugin.getVersion());
        dataRow.put("jarName", serverPlugin.getJarName());
        dataRow.put("downloadUrl", serverPlugin.getDownloadUrl());
        dataRow.put("versionNumber", String.valueOf(serverPlugin.getVersionNumber()));

        if (localPlugin == null) {
            dataRow.put("isDownloadTableColumn", "下载");
            dataRow.put("isEnableTableColumn", "false");
        } else {
            if (serverPlugin.getVersionNumber() > localPlugin.getVersionNumber()) {
                dataRow.put("isDownloadTableColumn", "更新");
            } else {
                dataRow.put("isDownloadTableColumn", "已下载");
            }
            dataRow.put("isEnableTableColumn", localPlugin.getIsEnable().toString());
        }

        pluginManageController.getOriginPluginData().add(dataRow);
    }

    public void downloadPluginJar(Map<String, String> dataRow) throws Exception {
        PluginJarInfo pluginJarInfo = new PluginJarInfo();
        pluginJarInfo.setName(dataRow.get("nameTableColumn"));
        pluginJarInfo.setSynopsis(dataRow.get("synopsisTableColumn"));
        pluginJarInfo.setVersion(dataRow.get("versionTableColumn"));
        pluginJarInfo.setVersionNumber(Integer.parseInt(dataRow.get("versionNumber")));
        pluginJarInfo.setDownloadUrl(dataRow.get("downloadUrl"));
        pluginJarInfo.setJarName(dataRow.get("jarName"));
        pluginJarInfo.setIsDownload(true);
        pluginJarInfo.setIsEnable(true);

        File file = pluginManager.downloadPlugin(pluginJarInfo);

        pluginManageController.getIndexController().addToolMenu(file);
    }

    public void setIsEnableTableColumn(Integer index) {
        Map<String, String> dataRow = pluginManageController.getOriginPluginData().get(index);
        String jarName = dataRow.get("jarName");
        PluginJarInfo pluginJarInfo = this.pluginManager.getLocalPlugin(jarName);
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
            entry ->
                !entry.getKey().equals("downloadUrl") &&
                    entry.getValue().toLowerCase().contains(keyword.toLowerCase())
        );
    }

    /**
     * 判断插件是否启用
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isPluginEnabled(String fileName) {
        String jarName = substringBeforeLast(fileName, "-");
        PluginJarInfo pluginJarInfo = PluginManager.getInstance().getLocalPlugin(jarName);
        return pluginJarInfo == null || pluginJarInfo.getIsEnable();
    }
}
