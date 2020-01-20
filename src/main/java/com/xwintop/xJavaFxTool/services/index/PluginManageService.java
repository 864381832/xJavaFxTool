package com.xwintop.xJavaFxTool.services.index;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

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
                Map<String, String> dataRow = new HashMap<String, String>();
                dataRow.put("nameTableColumn", data.getString("name"));
                dataRow.put("synopsisTableColumn", data.getString("synopsis"));
                dataRow.put("versionTableColumn", data.getString("version"));
                dataRow.put("isDownloadTableColumn", "否");
                dataRow.put("isEnableTableColumn", "false");
                dataRow.put("downloadUrl", data.getString("downloadUrl"));
                pluginManageController.getPluginDataTableData().add(dataRow);
            }
        } catch (Exception e) {
            log.error("获取列表失败：", e);
            TooltipUtil.showToast("获取列表失败：" + e.getMessage());
        }
    }

    public void downloadPluginJar(String downloadUrl) throws Exception {
        File file = new File("libs/", StringUtils.substring(downloadUrl, 56, downloadUrl.length()));
        HttpUtil.downloadFile(downloadUrl, file);
    }
}