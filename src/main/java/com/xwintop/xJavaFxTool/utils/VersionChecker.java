package com.xwintop.xJavaFxTool.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: VersionChecker
 * @Description: 新版本检查
 * @author: xufeng
 * @date: 2022/3/28 14:59
 */

@Slf4j
public class VersionChecker {
    public static boolean checkNewVersion() {
        try {
            String json = HttpUtil.get("https://gitee.com/api/v5/repos/xwintop/xJavaFxTool/releases/latest");
            log.info("检查新版本:" + json);
            JSONObject node = JSON.parseObject(json);
            final String latestVersion = node.getString("tag_name");
            final String features = node.getString("body");
            if (isLargerThanCurrent(latestVersion)) {
                final String content = new StringBuilder()
                    .append("版本名：").append(node.getString("name")).append("\r\n")
                    .append("更新内容: \r\n").append(features)
                    .toString();
                if (FxAlerts.confirmOkCancel("发现新版本 " + latestVersion, content)) {
                    HttpClientUtil.openBrowseURLThrowsException("https://gitee.com/xwintop/xJavaFxTool/releases");
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("检查新版本失败！", e);
        }
        return true;
    }

    private static Boolean isLargerThanCurrent(String remoteVersion) {
        final String[] arr = remoteVersion.split("v");
        String r = remoteVersion;
        if (arr.length == 2) {
            r = arr[1];
        }

        final String[] localVersionArr = Config.xJavaFxToolVersions.substring(1).split("\\.");
        final String[] remoteVersionArr = r.split("\\.");
        for (int i = 0; i < localVersionArr.length; i++) {
            try {
                final int localVersionSymbol = Integer.parseInt(localVersionArr[i]);
                final int remoteVersionSymbol = Integer.parseInt(remoteVersionArr[i]);
                if (localVersionSymbol < remoteVersionSymbol) {
                    return true;
                } else if (localVersionSymbol > remoteVersionSymbol) {
                    return false;
                }
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }
}
