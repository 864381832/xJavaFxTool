package com.xwintop.xJavaFxTool.newui.creator;

import javafx.scene.image.Image;
import lombok.Data;

@Data
public class PluginProjectInfo {

    private String location;

    private String groupId;

    private String artifactId;

    private String version;

    private String pluginName;

    private Image pluginLogo;
}
