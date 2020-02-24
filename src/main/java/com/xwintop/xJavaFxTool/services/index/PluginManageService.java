package com.xwintop.xJavaFxTool.services.index;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
    private PluginManageController pluginManageController;
    public final static Map<String, PluginJarInfo> PLUGIN_JAR_INFO_MAP = new HashMap<>();
    private JSONArray jsonArray = null;

    public PluginManageService(PluginManageController pluginManageController) {
        this.pluginManageController = pluginManageController;
    }

    public void getPluginList() {
        try {
            String jsonString = HttpUtil.get("https://xwintop.gitee.io/xjavafxtool-plugin/plugin-list.json");
//            String jsonString = "[{\"name\":\"Zookeeper工具\",\"synopsis\":\"Zookeeper工具(方便对zookeeper的一系列操作，包括新增、修改、删除(包括子文件)、重命名、复制、添加变更通知)\",\"version\":\"0.0.1\",\"jarName\": \"x-ZookeeperTool\",\"versionNumber\": 1,\"downloadUrl\":\"https://xwintop.gitee.io/xjavafxtool-plugin/plugin-libs/x-ZookeeperTool-0.0.1.jar\"}]";
            jsonArray = JSON.parseArray(jsonString);
            for (Object json : jsonArray) {
                addDataRow((JSONObject) json);
            }
        } catch (Exception e) {
            log.error("获取列表失败：", e);
            TooltipUtil.showToast("获取列表失败：" + e.getMessage());
        }
    }

    private void addDataRow(JSONObject data) {
        PluginJarInfo pluginJarInfo = PLUGIN_JAR_INFO_MAP.get(data.getString("jarName"));
        Map<String, String> dataRow = new HashMap<String, String>();
        dataRow.put("nameTableColumn", data.getString("name"));
        dataRow.put("synopsisTableColumn", data.getString("synopsis"));
        dataRow.put("versionTableColumn", data.getString("version"));
        if (pluginJarInfo == null) {
            dataRow.put("isDownloadTableColumn", "下载");
            dataRow.put("isEnableTableColumn", "false");
        } else {
            if (Integer.parseInt(data.getString("versionNumber")) > pluginJarInfo.getVersionNumber()) {
                dataRow.put("isDownloadTableColumn", "更新");
            } else {
                dataRow.put("isDownloadTableColumn", "已下载");
            }
            dataRow.put("isEnableTableColumn", pluginJarInfo.getIsEnable().toString());
        }
        dataRow.put("jarName", data.getString("jarName"));
        dataRow.put("downloadUrl", data.getString("downloadUrl"));
        dataRow.put("versionNumber", data.getString("versionNumber"));
        pluginManageController.getPluginDataTableData().add(dataRow);
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
//        File file = new File("libs/", StringUtils.substring(downloadUrl, 56, downloadUrl.length()));
        File file = new File("libs/", pluginJarInfo.getJarName() + "-" + pluginJarInfo.getVersion() + ".jar");
        HttpUtil.downloadFile(pluginJarInfo.getDownloadUrl(), file);
        PLUGIN_JAR_INFO_MAP.put(pluginJarInfo.getJarName(), pluginJarInfo);
        pluginManageController.getIndexController().addToolMenu(file);
        PluginManageService.savePluginJarList();
    }

    public void setIsEnableTableColumn(Integer index) {
        Map<String, String> dataRow = pluginManageController.getPluginDataTableData().get(index);
        String jarName = dataRow.get("jarName");
        PluginJarInfo pluginJarInfo = PLUGIN_JAR_INFO_MAP.get(jarName);
        if (pluginJarInfo != null) {
            pluginJarInfo.setIsEnable(Boolean.parseBoolean(dataRow.get("isEnableTableColumn")));
        }
    }

    public void selectPluginAction() {
        pluginManageController.getPluginDataTableData().clear();
        String selectText = pluginManageController.getSelectPluginTextField().getText();
        boolean empty = selectText.trim().length() == 0;
        for (Object json : jsonArray) {
            JSONObject data = (JSONObject) json;
            if (empty || StringUtils.containsIgnoreCase(data.toString(), selectText)) {
                addDataRow(data);
            }
        }
    }

    /*加载插件列表*/
    public static void reloadPluginJarList() {
        File systemPluginListfile = new File("system_plugin_list.json");
        if (systemPluginListfile.exists()) {
            try {
                List<PluginJarInfo> pluginJarInfoList = JSON.parseArray(FileUtils.readFileToString(systemPluginListfile, "utf-8"), PluginJarInfo.class);
                for (PluginJarInfo pluginJarInfo : pluginJarInfoList) {
                    PLUGIN_JAR_INFO_MAP.put(pluginJarInfo.getJarName(), pluginJarInfo);
                }
            } catch (Exception e) {
                log.error("读取插件jar包配置文件失败：", e);
            }
        }
    }

    /*保存插件列表*/
    public static void savePluginJarList() {
        File systemPluginListfile = new File("system_plugin_list.json");
        String systemPluginListString = JSON.toJSONString(PLUGIN_JAR_INFO_MAP.values());
        try {
            FileUtils.writeStringToFile(systemPluginListfile, systemPluginListString, "utf-8");
        } catch (Exception e) {
            log.error("保存插件jar包配置文件失败：", e);
        }
    }


    /**
     * 判断插件是否启用
     */
    public static boolean getPluginJarIsEnable(String fileName) {
        String jarName = StringUtils.substring(fileName, 0, StringUtils.lastIndexOf(fileName, "-"));
        PluginJarInfo pluginJarInfo = PLUGIN_JAR_INFO_MAP.get(jarName);
        if (pluginJarInfo != null && !pluginJarInfo.getIsEnable()) {
            return false;
        }
        return true;
    }
}