package com.xwintop.xJavaFxTool.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xcore.util.HttpClientUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;

/**
 * @ClassName: VersionChecker
 * @Description: 版本检查工具类
 * @author: xufeng
 * @date: 2021/1/31 22:25
 */

@Slf4j
public class VersionChecker {

    public static void checkerVersion(String checkUrl, String downloadUrl, String version) {
        VersionChecker.hasNewVersion(checkUrl, version, (latestVersion, features) -> {
            String title = "发现新版本";
            final String content = new StringBuilder()
                    .append("最新版本: ").append(latestVersion).append("\r\n")
                    .append("当前版本: v").append(version).append("\r\n")
                    .append("新特性: \r\n").append(features)
                    .toString();
            if (AlertUtil.showConfirmAlert(content)) {
                try {
                    HttpClientUtil.openBrowseURLThrowsException(downloadUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void hasNewVersion(String url, String localVersion, BiConsumer<String, String> runnable) {
        String json = HttpUtil.get(url);
        JSONObject jsonObject = JSON.parseObject(json);
        try {
            final String latestVersion = jsonObject.getString("tag_name");
            final String features = jsonObject.getString("body");
            compareAndRun(latestVersion, features, localVersion, runnable);
        } catch (Exception exception) {
            log.error("check update failed", exception);
        }
    }

    private static void compareAndRun(String latestVersion, String features, String localVersion, BiConsumer<String, String> runnable) {
        if (isLargerThanCurrent(latestVersion, localVersion)) {
            Platform.runLater(() -> runnable.accept(latestVersion, features));
        }
    }

    private static Boolean isLargerThanCurrent(String remoteVersion, String localVersion) {
        final String[] arr = remoteVersion.split("v");
        String r = remoteVersion;
        if (arr.length == 2) {
            r = arr[1];
        }

        final String[] localVersionArr = localVersion.split("\\.");
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
