package com.xwintop.xJavaFxTool.services.index;

import cn.hutool.http.HttpStatus;
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
import okhttp3.*;
import okio.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    /**
     * 当下载插件时，模拟数种 UA
     */
    public static final String[] OPTIONAL_UA_LIST = {
        "Mozilla/5.0 (Windows NT 6.1; rv:51.0) Gecko/20100101 Firefox/51.0",
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko"
    };

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

    public File downloadPlugin(PluginJarInfo pluginJarInfo, BiConsumer<Long, Long> onProgressUpdate) throws IOException {
        File file = pluginJarInfo.getFile();
        FileUtils.forceMkdirParent(file);
        OkHttpClient pluginDownloader = new OkHttpClient.Builder().addInterceptor(new PluginManageService.DownloadProgressInterceptor(onProgressUpdate)).build();
        // 使用多个 UA 尝试下载
        Throwable downloadFailure = null;
        for (String ua : OPTIONAL_UA_LIST) {
            try {
                tryDownload(pluginJarInfo.getName(), pluginJarInfo.getDownloadUrl(), ua, file, pluginDownloader);
                downloadFailure = null;
                break;
            } catch (Exception e) {
                downloadFailure = e;
            }
        }
        if (downloadFailure != null) {
            if (downloadFailure instanceof IOException) {
                throw (IOException) downloadFailure;
            } else {
                throw new IOException("插件 '" + pluginJarInfo.getName() + "' 下载失败 " + pluginJarInfo.getJarName(), downloadFailure);
            }
        }
        // 下载完毕
        pluginJarInfo.setIsDownload(true);
        pluginJarInfo.setIsEnable(true);
        pluginJarInfo.setLocalVersionNumber(pluginJarInfo.getVersionNumber());
        return file;
    }

    /**
     * 尝试指定的 UA 进行下载，如果下载失败则抛出异常
     *
     * @param pluginName 插件名称
     * @param url        下载地址
     * @param ua         UA 字符串
     * @param file       下载到的目标文件
     * @throws IOException 如果下载失败
     */
    private void tryDownload(String pluginName, String url, String ua, File file, OkHttpClient pluginDownloader) throws IOException {
        Request request = new Request.Builder().header("User-Agent", ua).url(url).build();
        try (Response response = pluginDownloader.newCall(request).execute()) {
            if (response.code() != HttpStatus.HTTP_OK) {
                throw new IOException("插件 '" + pluginName + "' 下载失败 : HTTP " + response.code());
            }

            InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                IOUtils.copy(inputStream, outputStream);
            }
        }
    }

    private class DownloadProgressInterceptor implements Interceptor {
        private BiConsumer<Long, Long> onProgressUpdate;

        DownloadProgressInterceptor(BiConsumer<Long, Long> onProgressUpdate) {
            this.onProgressUpdate = onProgressUpdate;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                .body(new PluginManageService.ProgressResponseBody(originalResponse.body(), onProgressUpdate)).build();
        }
    }

    private static class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;

        private BiConsumer<Long, Long> onProgressUpdate;

        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody, BiConsumer<Long, Long> onProgressUpdate) {
            this.responseBody = responseBody;
            this.onProgressUpdate = onProgressUpdate;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    onProgressUpdate.accept(totalBytesRead, responseBody.contentLength());
                    return bytesRead;
                }
            };
        }
    }
}
