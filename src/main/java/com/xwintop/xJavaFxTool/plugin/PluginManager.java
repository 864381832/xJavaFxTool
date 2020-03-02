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
import java.util.function.Consumer;
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

    private final List<PluginJarInfo> pluginList = new ArrayList<>(); // 插件列表

    public PluginManager(String localPluginsPath) {
        this.localPluginsPath = localPluginsPath;

        if (StringUtils.isNotEmpty(localPluginsPath)) {
            loadLocalPlugins();
        }
    }

    ////////////////////////////////////////////////////////////// 查询插件

    public List<PluginJarInfo> getPluginList() {
        return Collections.unmodifiableList(this.pluginList);
    }


    public PluginJarInfo getPlugin(String jarName) {
        return this.pluginList.stream()
            .filter(plugin -> Objects.equals(plugin.getJarName(), jarName))
            .findFirst().orElse(null);
    }

    ////////////////////////////////////////////////////////////// 插件列表

    public void loadLocalPlugins() {
        try {
            Path path = Paths.get(this.localPluginsPath);
            if (!Files.exists(path)) {
                return;
            }

            String json = new String(Files.readAllBytes(path), DEFAULT_CHARSET);
            JSON.parseArray(json, PluginJarInfo.class).forEach(plugin -> {
                this.addOrUpdatePlugin(plugin, exist -> {
                    exist.setLocalVersionNumber(plugin.getLocalVersionNumber());
                    exist.setIsDownload(plugin.getIsDownload());
                    exist.setIsEnable(plugin.getIsEnable());
                });
            });
        } catch (IOException e) {
            log.error("读取插件配置失败", e);
        }
    }

    public void loadServerPlugins() {
        try {
            String json = HttpUtil.get(SERVER_PLUGINS_URL);
            JSON.parseArray(json, PluginJarInfo.class).forEach(plugin -> {
                this.addOrUpdatePlugin(plugin, exist -> {
                    exist.setName(plugin.getName());
                    exist.setSynopsis(plugin.getSynopsis());
                    exist.setVersion(plugin.getVersion());
                    exist.setVersionNumber(plugin.getVersionNumber());
                    exist.setDownloadUrl(plugin.getDownloadUrl());
                });
            });
        } catch (Exception e) {
            log.error("下载插件列表失败", e);
        }
    }

    // 异步下载服务器插件列表，便于界面上展示 loading 动画
    public CompletableFuture<Void> loadServerPluginsAsync() {
        return CompletableFuture.runAsync(this::loadServerPlugins);
    }

    private void addOrUpdatePlugin(PluginJarInfo pluginJarInfo,  Consumer<PluginJarInfo> ifExists) {
        PluginJarInfo exists = getPlugin(pluginJarInfo.getJarName());
        if (exists == null) {
            this.pluginList.add(pluginJarInfo);
        } else {
            ifExists.accept(exists);
        }
    }

    ////////////////////////////////////////////////////////////// 下载插件

    public File downloadPlugin(PluginJarInfo pluginJarInfo) throws IOException {
        PluginJarInfo plugin = getPlugin(pluginJarInfo.getJarName());
        if (plugin == null) {
            throw new IllegalStateException("没有找到插件 " + pluginJarInfo.getJarName());
        }

        File file = new File("libs/", pluginJarInfo.getJarName() + "-" + pluginJarInfo.getVersion() + ".jar");
        HttpUtil.downloadFile(pluginJarInfo.getDownloadUrl(), file);

        plugin.setIsDownload(true);
        plugin.setIsEnable(true);
        plugin.setLocalVersionNumber(plugin.getVersionNumber());
        this.saveToFile();

        return file;
    }

    ////////////////////////////////////////////////////////////// 保存配置

    public void saveToFile() throws IOException {
        String json = JSON.toJSONString(this.pluginList, true);
        Files.write(
            Paths.get(this.localPluginsPath),
            json.getBytes(DEFAULT_CHARSET)
        );
    }
}
