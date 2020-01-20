package com.xwintop.xJavaFxTool.services.index;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    private OkHttpClient client = new OkHttpClient();

    public PluginManageService(PluginManageController pluginManageController) {
        this.pluginManageController = pluginManageController;
    }

    public void getPluginList() {
        try {
//            String jsonString = HttpUtil.get("https://xwintop.gitee.io/xjavafxtool-plugin/plugin-list.json");
            String jsonString = "[{\"name\":\"Zookeeper工具\",\"synopsis\":\"Zookeeper工具(方便对zookeeper的一系列操作，包括新增、修改、删除(包括子文件)、重命名、复制、添加变更通知)\",\"version\":\"0.0.1\",\"downloadUrl\":\"https://xwintop.gitee.io/xjavafxtool-plugin/plugin-libs/x-ZookeeperTool-0.0.1.jar\"}]";
            JSONArray jsonArray = JSON.parseArray(jsonString);
            for (Object json : jsonArray) {
                JSONObject data = (JSONObject) json;
                PluginJarInfo pluginJarInfo = XJavaFxSystemUtil.pluginJarInfoMap.get(data.getString("jarName"));
                Map<String, String> dataRow = new HashMap<String, String>();
                dataRow.put("nameTableColumn", data.getString("name"));
                dataRow.put("synopsisTableColumn", data.getString("synopsis"));
                dataRow.put("versionTableColumn", data.getString("version"));
                if (pluginJarInfo == null) {
                    dataRow.put("isDownloadTableColumn", "否");
                    dataRow.put("isEnableTableColumn", "false");
                } else {
                    if (Integer.parseInt(data.getString("versionNumber")) > pluginJarInfo.getVersionNumber()) {
                        dataRow.put("isDownloadTableColumn", "更新");
                    } else {
                        dataRow.put("isDownloadTableColumn", "是");
                    }
                    dataRow.put("isEnableTableColumn", pluginJarInfo.getIsDownload().toString());
                }
                dataRow.put("jarName", data.getString("jarName"));
                dataRow.put("downloadUrl", data.getString("downloadUrl"));
                dataRow.put("versionNumber", data.getString("versionNumber"));
                pluginManageController.getPluginDataTableData().add(dataRow);
            }
        } catch (Exception e) {
            log.error("获取列表失败：", e);
            TooltipUtil.showToast("获取列表失败：" + e.getMessage());
        }
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
        XJavaFxSystemUtil.pluginJarInfoMap.put(pluginJarInfo.getJarName(), pluginJarInfo);
    }
}