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

    private String pluginTitle;

    private Image pluginLogo;

    //////////////////////////////////////////////////////////////

    public String getPackageName() {
        return groupId + "." + toClassName(artifactId).toLowerCase();
    }

    public String getControllerClass() {
        return toClassName(artifactId) + "Controller";
    }

    public String getControllerFullClass() {
        return getPackageName() + "." + getControllerClass();
    }

    public String getMainClass() {
        return toClassName(artifactId) + "Main";
    }

    public String getMainFullClass() {
        return getPackageName() + "." + getMainClass();
    }

    private String toClassName(String hyphens) {
        StringBuilder sb = new StringBuilder();
        for (String s : hyphens.split("-")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
}
