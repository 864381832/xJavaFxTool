package com.xwintop.xJavaFxTool.newui.creator;

import static com.xwintop.xJavaFxTool.utils.ResourceBundleUtils.toNativeAscii;

import com.xwintop.xJavaFxTool.utils.ResourceUtils;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class CreatePluginProjectService {

    private static final CreatePluginProjectService INSTANCE = new CreatePluginProjectService();

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static CreatePluginProjectService getInstance() {
        return INSTANCE;
    }

    public void createProject(PluginProjectInfo pluginProjectInfo) {
        try {
            FileUtils.forceMkdir(new File(pluginProjectInfo.getLocation()));
            savePom(pluginProjectInfo);
            saveConfiguration(pluginProjectInfo);
            saveResourceBundle(pluginProjectInfo);
            saveFxml(pluginProjectInfo);
            saveControllerClass(pluginProjectInfo);
            saveMainClass(pluginProjectInfo);
        } catch (Exception e) {
            FxAlerts.error("创建失败", e);
        }
    }

    private void saveMainClass(PluginProjectInfo info) throws Exception {
        String java = ResourceUtils.readResource("/plugin-template/PLUGIN_NAMEMain.java", CHARSET);
        java = java
            .replace("[PACKAGE]", info.getPackageName())
            .replace("[PLUGIN_NAME]", info.getPluginName())
            .replace("[CLASS_NAME]", info.getMainClass());
        String javaPath = "src/main/java/" + info.getMainFullClass().replace(".", "/") + ".java";
        writeFile(Paths.get(info.getLocation(), javaPath), java);
    }

    private void saveControllerClass(PluginProjectInfo info) throws Exception {
        String java = ResourceUtils.readResource("/plugin-template/PLUGIN_NAMEController.java", CHARSET);
        java = java
            .replace("[PACKAGE]", info.getPackageName())
            .replace("[CLASS_NAME]", info.getControllerClass());
        String javaPath = "src/main/java/" + info.getControllerFullClass().replace(".", "/") + ".java";
        writeFile(Paths.get(info.getLocation(), javaPath), java);
    }

    private void saveFxml(PluginProjectInfo info) throws Exception {
        String fxml = ResourceUtils.readResource("/plugin-template/PLUGIN_NAME.fxml", CHARSET);
        fxml = fxml
            .replace("[CONTROLLER]", info.getControllerFullClass())
            .replace("[PLUGIN_NAME]", info.getPluginName());
        String resourceBundlePath = "src/main/resources/fxml/" + info.getPluginName() + ".fxml";
        writeFile(Paths.get(info.getLocation(), resourceBundlePath), fxml);
    }

    private void saveResourceBundle(PluginProjectInfo info) throws Exception {
        String resourceBundle = ResourceUtils.readResource("/plugin-template/PLUGIN_NAME.properties", CHARSET);
        resourceBundle = resourceBundle.replace("[TITLE]", toNativeAscii(info.getPluginTitle(), false, true));
        String resourceBundlePath = "src/main/resources/locale/" + info.getPluginName() + ".properties";
        writeFile(Paths.get(info.getLocation(), resourceBundlePath), resourceBundle);
    }

    private void saveConfiguration(PluginProjectInfo info) throws Exception {
        String config = ResourceUtils.readResource("/plugin-template/toolFxmlLoaderConfiguration.xml", CHARSET);
        config = config.replace("[PLUGIN_NAME]", info.getPluginName());
        writeFile(Paths.get(info.getLocation(), "src/main/resources/config/toolFxmlLoaderConfiguration.xml"), config);
    }

    private void savePom(PluginProjectInfo info) throws Exception {
        String pom = ResourceUtils.readResource("/plugin-template/pom.xml", CHARSET);
        pom = pom
            .replace("[GROUPID]", info.getGroupId())
            .replace("[ARTIFACTID]", info.getArtifactId())
            .replace("[VERSION]", info.getVersion());
        Files.write(Paths.get(info.getLocation(), "pom.xml"), pom.getBytes(CHARSET));
    }

    private void writeFile(Path path, String fileContent) throws IOException {
        FileUtils.forceMkdirParent(path.toFile());
        Files.write(path, fileContent.getBytes(CHARSET));
    }
}
