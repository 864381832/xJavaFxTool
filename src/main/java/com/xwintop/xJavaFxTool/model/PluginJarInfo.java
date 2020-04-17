package com.xwintop.xJavaFxTool.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * 插件信息
 *
 * @author xufeng
 */

@Data
@NoArgsConstructor
public class PluginJarInfo {

    ///////////////////////////////////////// 下面的属性在远程插件列表和本地配置中都存在

    private String name;            // 名称

    private String synopsis;        // 简介

    private String jarName;         // jar包名称

    private String version;         // 版本名称

    private Integer versionNumber;  // 版本号（用来判断更新）

    private String downloadUrl;     // 下载地址

    ///////////////////////////////////////// 下面的属性在远程插件列表中不存在

    private Boolean isDownload;     // 是否下载

    private Boolean isEnable;       // 是否启用

    private Boolean isFavorite;     // 是否置顶

    private Integer localVersionNumber;     // 插件本地版本

    private String localPath;       // 插件本地文件路径（如果是本地插件）

    ///////////////////////////////////////// 下面的属性来自插件描述文件 toolFxmlLoaderConfiguration.xml

    private String fxmlPath;                // FXML 资源路径

    private String pagePath;                // 网页资源路径

    private String iconPath;                // 图标资源路径（可选）

    private String bundleName;              // 配置资源路径

    private String className;               // ？

    private String menuId;                  // （当是菜单时）菜单ID

    private String menuParentId;            // 上级菜单ID

    private String menuParentTitle;         // 上级菜单标题（资源名）

    private Boolean isMenu = false;         // 是否为菜单

    private String title;                   // （Tab 页或窗体）标题

    private String controllerType = "Node"; // 内容类型（Node/WebView）

    ////////////////////////////////////////////////////////////

    public boolean getIsFavorite() {
        return this.isFavorite != null && this.isFavorite;
    }

    ////////////////////////////////////////////////////////////

    @JSONField(serialize = false)
    public File getFile() {
        return localPath == null? new File("libs/", getJarName() + "-" + getVersion() + ".jar"): new File(localPath);
    }

    @JSONField(serialize = false)
    public String getDefaultIconPath() {
        return bundleName == null ? "" :
            (bundleName.replace("locale.", "/logo/") + ".png");
    }
}
