package com.xwintop.xJavaFxTool.plugin;

import com.alibaba.fastjson.JSON;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Data
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

    private final List<PluginJarInfo> devPluginList = new ArrayList<>(); // dev插件列表

    public PluginManager() {
        this.loadLocalPluginConfiguration();
    }

    // 查询插件
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
            String json = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
            this.pluginList.addAll(JSON.parseArray(json, PluginJarInfo.class));
        } catch (IOException e) {
            log.error("读取插件配置失败", e);
        }
    }

    public void loadLocalDevPluginConfiguration() {
        try {
            // 系统类库路径
            File libPath = new File("devLibs/");
            // 获取所有的.jar文件
            File[] jarFiles = libPath.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                for (File file : jarFiles) {
                    try {
                        PluginJarInfo plugin = new PluginJarInfo();
                        plugin.setLocalPath(file.getAbsolutePath());
                        plugin.setIsEnable(true);
                        plugin.setIsDownload(true);
                        PluginParser.parse(file, plugin);
                        devPluginList.add(plugin);
                    } catch (Exception e) {
                        log.error("解析失败", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("添加libs中jar包到系统中异常:", e);
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
                    PluginParser.initParse(pluginFile, plugin);
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
     */
//    public AddPluginResult addPluginJar(File jarFile) {
//        PluginClassLoader tmpClassLoader = PluginClassLoader.create(jarFile);
//        PluginJarInfo newJarInfo = new PluginJarInfo();
//        newJarInfo.setLocalPath(jarFile.getAbsolutePath());
//        newJarInfo.setIsEnable(true);
//        newJarInfo.setIsDownload(true);
//        PluginParser.parse(jarFile, newJarInfo, tmpClassLoader);
//
//        for (int i = 0; i < pluginList.size(); i++) {
//            PluginJarInfo jarInfo = pluginList.get(i);
//            if (Objects.equals(jarInfo.getFxmlPath(), newJarInfo.getFxmlPath())) {
//                newJarInfo.setVersion(jarInfo.getVersion());
//                newJarInfo.setSynopsis(jarInfo.getSynopsis());
//                pluginList.set(i, newJarInfo);
//                saveToFileQuietly();
//                return new AddPluginResult(newJarInfo, false);
//            }
//        }
//
//        pluginList.add(newJarInfo);
//        saveToFileQuietly();
//        return new AddPluginResult(newJarInfo, true);
//    }

    // 保存配置，如果失败则抛出异常
    public void saveToFile() throws IOException {
        String json = JSON.toJSONString(this.pluginList, true);
        Path path = Paths.get(LOCAL_PLUGINS_PATH);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        FileUtils.writeStringToFile(path.toFile(), json, StandardCharsets.UTF_8);
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
