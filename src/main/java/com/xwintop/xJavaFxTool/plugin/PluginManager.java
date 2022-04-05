package com.xwintop.xJavaFxTool.plugin;

import com.alibaba.fastjson.JSON;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class PluginManager {
    public static final String LOCAL_PLUGINS_PATH = "./system_plugin_list.json";

    private static PluginManager instance;

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    private final List<PluginJarInfo> pluginList = new ArrayList<>(); // 插件列表

    public PluginManager() {
        this.loadLocalPluginConfiguration();
    }

    ////////////////////////////////////////////////////////////// 查询插件

    public List<PluginJarInfo> getPluginList() {
        return this.pluginList;
    }

    public List<PluginJarInfo> getEnabledPluginList() {
        return this.pluginList.stream().filter(PluginJarInfo::getIsEnable).collect(Collectors.toList());
    }

    public PluginJarInfo getPlugin(String jarName) {
        return this.pluginList.stream()
            .filter(plugin -> Objects.equals(plugin.getJarName(), jarName))
            .findFirst().orElse(null);
    }

    ////////////////////////////////////////////////////////////// 插件列表

    /**
     * 从配置文件中加载本地插件信息
     */
    private void loadLocalPluginConfiguration() {
        try {
            Path path = Paths.get(LOCAL_PLUGINS_PATH);
            if (!Files.exists(path)) {
                return;
            }

            String json = Files.readString(path, StandardCharsets.UTF_8);
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

    /**
     * 解析本地插件文件
     */
    public void loadLocalPlugins() {
        List<PluginJarInfo> removeList = new ArrayList();
        this.pluginList.forEach(plugin -> {
            File pluginFile = plugin.getFile();
            if (pluginFile.exists()) {
                try {
                    PluginParser.parse(pluginFile, plugin);
                } catch (Exception e) {
                    log.error("解析失败", e);
                }
            } else {
                removeList.add(plugin);
            }
        });
        this.pluginList.removeAll(removeList);
    }

    /**
     * 添加本地插件。如果与已有插件同名，则替换已有插件信息
     *
     * @param jarFile 插件文件
     */
    public AddPluginResult addPluginJar(File jarFile) {
        PluginClassLoader tmpClassLoader = PluginClassLoader.create(jarFile);
        PluginJarInfo newJarInfo = new PluginJarInfo();
        newJarInfo.setLocalPath(jarFile.getAbsolutePath());
        newJarInfo.setIsEnable(true);
        newJarInfo.setIsDownload(true);
        PluginParser.parse(jarFile, newJarInfo, tmpClassLoader);

        for (int i = 0; i < pluginList.size(); i++) {
            PluginJarInfo jarInfo = pluginList.get(i);
            if (Objects.equals(jarInfo.getFxmlPath(), newJarInfo.getFxmlPath())) {
                newJarInfo.setVersion(jarInfo.getVersion());
                newJarInfo.setSynopsis(jarInfo.getSynopsis());
                pluginList.set(i, newJarInfo);
                saveToFileQuietly();
                return new AddPluginResult(newJarInfo, false);
            }
        }

        pluginList.add(newJarInfo);
        saveToFileQuietly();
        return new AddPluginResult(newJarInfo, true);
    }

    /**
     * 向插件列表添加插件信息或更改插件列表中已有的插件信息
     *
     * @param pluginJarInfo 需要添加的插件信息
     * @param ifExists      如果插件已存在，则不会将 pluginJarInfo 加入，
     *                      而是提供已有的插件信息对象供调用者更新其属性
     */
    public void addOrUpdatePlugin(PluginJarInfo pluginJarInfo, Consumer<PluginJarInfo> ifExists) {
        PluginJarInfo exists = getPlugin(pluginJarInfo.getJarName());
        if (exists == null) {
            this.pluginList.add(pluginJarInfo);
        } else {
            ifExists.accept(exists);
        }
    }

    // 保存配置，如果失败则抛出异常
    public void saveToFile() throws IOException {
        String json = JSON.toJSONString(this.pluginList, true);
        Path path = Paths.get(LOCAL_PLUGINS_PATH);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, json, StandardCharsets.UTF_8);
    }

    // 保存配置，如果失败不抛出异常
    public void saveToFileQuietly() {
        try {
            saveToFile();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
