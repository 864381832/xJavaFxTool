package com.xwintop.xJavaFxTool.plugin;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import java.io.File;

public class PluginParserTest {

    public static void main(String[] args) throws Exception {
        String pluginJar = "libs/x-ActiveMqTool-0.0.1.jar";
        PluginParser.parse(new File(pluginJar), new PluginJarInfo());
    }
}