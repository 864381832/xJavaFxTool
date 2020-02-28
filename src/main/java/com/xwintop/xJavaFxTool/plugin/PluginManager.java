package com.xwintop.xJavaFxTool.plugin;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class PluginManager {

    public static final String SERVER_PLUGINS_URL = "https://xwintop.gitee.io/maven/plugin-libs/plugin-list.json";

    public static final String LOCAL_PLUGINS_PATH = "./system_plugin_list.json";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static PluginManager getInstance() {
        return new PluginManager(LOCAL_PLUGINS_PATH);
    }

    //////////////////////////////////////////////////////////////

    private final String localPluginsPath;

    private final List<PluginJarInfo> serverPluginList = new ArrayList<>(); // 服务端插件列表

    private final List<PluginJarInfo> localPluginList = new ArrayList<>();  // 本地插件列表

    public PluginManager(String localPluginsPath) {
        this.localPluginsPath = localPluginsPath;

        if (StringUtils.isNotEmpty(localPluginsPath)) {
            loadLocalPlugins();
        }
    }

    public List<PluginJarInfo> getLocalPluginList() {
        return Collections.unmodifiableList(this.localPluginList);
    }

    public List<PluginJarInfo> getServerPluginList() {
        return Collections.unmodifiableList(this.serverPluginList);
    }

    public void loadLocalPlugins() {
        try {
            Path path = Paths.get(this.localPluginsPath);
            if (!Files.exists(path)) {
                return;
            }

            String json = new String(Files.readAllBytes(path), DEFAULT_CHARSET);
            this.localPluginList.addAll(JSON.parseArray(json, PluginJarInfo.class));
        } catch (IOException e) {
            log.error("读取插件配置失败", e);
        }
    }

    public void loadServerPlugins() {
        try {
            String json = HttpUtil.get(SERVER_PLUGINS_URL);
            this.serverPluginList.addAll(JSON.parseArray(json, PluginJarInfo.class));
        } catch (Exception e) {
            log.error("下载插件列表失败", e);
        }
    }

    // 异步下载服务器插件列表，便于界面上展示 loading 动画
    public CompletableFuture<Void> loadServerPluginsAsync() {
        return CompletableFuture.runAsync(this::loadServerPlugins);
    }

    public PluginJarInfo getLocalPlugin(String jarName) {
        return this.localPluginList.stream()
            .filter(plugin -> Objects.equals(plugin.getJarName(), jarName))
            .findFirst().orElse(null);
    }

    public File downloadPlugin(PluginJarInfo pluginJarInfo) throws IOException {
        File file = new File("libs/", pluginJarInfo.getJarName() + "-" + pluginJarInfo.getVersion() + ".jar");
        HttpUtil.downloadFile(pluginJarInfo.getDownloadUrl(), file);

        pluginJarInfo.setIsDownload(true);
        this.localPluginList.add(pluginJarInfo);
        this.saveLocalPlugins();

        return file;
    }

    public void saveLocalPlugins() throws IOException {
        String json = JSON.toJSONString(this.localPluginList, true);
        Files.write(
            Paths.get(this.localPluginsPath),
            json.getBytes(DEFAULT_CHARSET)
        );
    }
}
