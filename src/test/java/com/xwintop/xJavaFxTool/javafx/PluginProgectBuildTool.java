package com.xwintop.xJavaFxTool.javafx;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.EscapeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;

/**
 * @ClassName: PluginProgectBuildTool
 * @Description: 插件项目生成工具
 * @author: xufeng
 * @date: 2020/1/21 13:28
 */

public class PluginProgectBuildTool {
    private String projectPath = "C:/IDEA/xwintop/xJavaFxTool/src/main/";
    //    private String projectPath = "";
    private String resources = "resources/com/xwintop/xJavaFxTool/";
//    private String xmlPath = "assistTools/";
//    private String xmlPath = "codeTools/";
//    private String xmlPath = "debugTools/";
//    private String xmlPath = "developTools/";
//    private String xmlPath = "epmsTools/";
//    private String xmlPath = "games/";
//    private String xmlPath = "littleTools/";
//    private String xmlPath = "webTools/";
    private String xmlPath = "javaFxTools/";
    private String srcPath = "java/com/xwintop/xJavaFxTool/";

    private String pluginPath = "C:/IDEA/xJavaFxTool-plugin/";

    @Test
    public void buildJava() throws Exception {
        File[] fxmlFileList = new File(projectPath + resources + "fxmlView/" + xmlPath).listFiles();
        for (File file : fxmlFileList) {
            if (file.isDirectory()) {
                continue;
            }
            String pluginProgectName = FilenameUtils.getBaseName(file.getName());
            String pluginProgectPath = pluginPath + xmlPath + "x-" + pluginProgectName;
            File pluginProgectNameFile = new File(pluginProgectPath);
            if (pluginProgectNameFile.exists()) {
                continue;
            }
            pluginProgectNameFile.mkdir();
            pluginProgectPath = pluginProgectPath + "/src/main/";
            FileUtils.writeStringToFile(new File(pluginProgectNameFile, "pom.xml"), PluginProgectPomBuildTool.getPom_xml(pluginProgectName), "utf-8");
            FileUtils.writeStringToFile(new File(pluginProgectNameFile, "README.md"), pluginProgectName, "utf-8");
            FileUtils.writeStringToFile(new File(pluginProgectNameFile, "README_EN.md"), pluginProgectName, "utf-8");
            FileUtils.writeStringToFile(new File(pluginProgectNameFile, ".gitignore"), PluginProgectPomBuildTool.getGitignore(pluginProgectName), "utf-8");
            FileUtils.writeStringToFile(new File(pluginProgectPath + srcPath + "Main.java"), PluginProgectPomBuildTool.getMain_java(pluginProgectName, xmlPath.substring(0, xmlPath.length() - 1)), "utf-8");
            new File(pluginProgectPath + srcPath + "job").mkdirs();
            new File(pluginProgectPath + srcPath + "manager").mkdirs();
            new File(pluginProgectPath + srcPath + "model").mkdirs();
            new File(pluginProgectPath + srcPath + "utils").mkdirs();

            FileUtils.writeStringToFile(new File(pluginProgectPath + "resources/config/toolFxmlLoaderConfiguration.xml"), PluginProgectPomBuildTool.getToolFxmlLoaderConfiguration_xml(pluginProgectName, xmlPath.substring(0, xmlPath.length() - 1)), "utf-8");
            FileUtils.writeStringToFile(new File(pluginProgectPath + "resources/locale/" + pluginProgectName + ".properties"), PluginProgectPomBuildTool.getLocal_properties(pluginProgectName), "utf-8");
            FileUtils.writeStringToFile(new File(pluginProgectPath + "resources/locale/" + pluginProgectName + "_en_US.properties"), PluginProgectPomBuildTool.getLocal_en_US_properties(pluginProgectName), "utf-8");

            copyPluginFile(resources + "fxmlView/" + xmlPath + file.getName(), pluginProgectPath);
            copyPluginFile(srcPath + "controller/" + xmlPath + pluginProgectName + "Controller.java", pluginProgectPath);
            copyPluginFile(srcPath + "services/" + xmlPath + pluginProgectName + "Service.java", pluginProgectPath);
            copyPluginFile(srcPath + "view/" + xmlPath + pluginProgectName + "View.java", pluginProgectPath);
//            break;
        }
    }

    @Test
    public void buildJson() throws Exception {
//        System.out.println(UnicodeUtil.toUnicode("测试",true));
        File[] fxmlFileList = new File(projectPath + resources + "fxmlView/" + xmlPath).listFiles();
        for (File file : fxmlFileList) {
            if (file.isDirectory() || !file.getName().endsWith(".fxml")) {
                continue;
            }
            String pluginProgectName = FilenameUtils.getBaseName(file.getName());
            String pluginProgectPath = pluginPath + xmlPath + "x-" + pluginProgectName;
            System.out.println(PluginProgectPomBuildTool.getPluginList_Json(pluginProgectName, xmlPath));
//            System.out.println("cd " + pluginProgectPath + "\n mvn package");
            File pluginLibsJarFile = new File(pluginProgectPath + "/target/x-" + pluginProgectName + "-0.0.1.jar");
            if (pluginLibsJarFile.exists()) {
                File pluginLibsFile = new File(pluginPath + "plugin-libs/" + xmlPath);
                FileUtils.copyFileToDirectory(pluginLibsJarFile, pluginLibsFile);
            }
//            break;
        }
    }

    @Test
    public void buildChangeFileUnicode() throws Exception {
        File[] fxmlFileList = new File(projectPath + resources + "fxmlView/" + xmlPath).listFiles();
        for (File file : fxmlFileList) {
            if (file.isDirectory()) {
                continue;
            }
            String pluginProgectName = FilenameUtils.getBaseName(file.getName());
            String pluginProgectPath = pluginPath + xmlPath + "x-" + pluginProgectName;
            pluginProgectPath = pluginProgectPath + "/src/main/";

            File localeFile = new File(pluginProgectPath + "resources/locale/" + pluginProgectName + ".properties");
            FileUtils.writeStringToFile(localeFile, UnicodeUtil.toUnicode(FileUtils.readFileToString(localeFile, "utf-8")));
//            break;
        }
    }

    private void copyPluginFile(String path, String pluginProgectPath) {
        try {
            File fromFile = new File(projectPath + path);
            if (fromFile.exists()) {
                File toFile = new File(pluginProgectPath + path);
                toFile.getParentFile().mkdirs();
                FileUtils.copyFile(fromFile, toFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
