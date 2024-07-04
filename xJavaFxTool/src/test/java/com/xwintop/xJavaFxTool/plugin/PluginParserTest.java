package com.xwintop.xJavaFxTool.plugin;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginParserTest {

    public static void main(String[] args) throws Exception {
        String pluginJar = "libs/x-ActiveMqTool-0.0.1.jar";
        PluginParser.parse(new File(pluginJar), new PluginJarInfo());
    }

    @Test
    public void copyPluginJar() throws Exception {
        String projectPath = "/Users/xufeng/workSpaces/IdeaProjects/xJavaFxTool/";
        List<String> xmlPathList = new ArrayList<>();
        xmlPathList.add("assistTools/");
        xmlPathList.add("codeTools/");
        xmlPathList.add("debugTools/");
        xmlPathList.add("developTools/");
        xmlPathList.add("games/");
        xmlPathList.add("javaFxTools/");
        xmlPathList.add("littleTools/");
        xmlPathList.add("netWorkTools/");
        xmlPathList.add("webTools/");
        xmlPathList.add("javaFxTools/");
        String pluginPath = "/Users/xufeng/workSpaces/IdeaProjects/maven/plugin-libs2/";
        for (String xmlPath : xmlPathList) {
            File[] fxmlFileList = new File(projectPath + xmlPath).listFiles();
            for (File pluginFile : fxmlFileList) {
                if (new File(pluginFile, "build/libs").exists()) {
                    File[] jarFileList = new File(pluginFile, "build/libs").listFiles((dir, name) -> name.endsWith(".jar"));
                    if(jarFileList != null) {
                        for (File file : jarFileList) {
                            FileUtils.copyFileToDirectory(file, new File(pluginPath, xmlPath));
                        }
                    }
                }
            }
        }
    }

}