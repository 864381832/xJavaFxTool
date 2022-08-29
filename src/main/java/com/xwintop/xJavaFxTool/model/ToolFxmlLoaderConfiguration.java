package com.xwintop.xJavaFxTool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class ToolFxmlLoaderConfiguration {

    /**
     * 资源url
     */
    private String url;

    /**
     * 国际化资源文件
     */
    private String resourceBundleName;

    /**
     * class名称
     */
    private String className;

    /**
     * 标题（配合国际化资源文件，无则默认显示原字符）
     */
    private String title;

    /**
     * 图标路径
     */
    private String iconPath;

    /**
     * 是否在启动时自动加载
     */
    private Boolean isDefaultShow = false;

    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 菜单父id
     */
    private String menuParentId;

    /**
     * 是否为菜单
     */
    private Boolean isMenu = false;

    /**
     * 内容类型
     */
    private String controllerType = "Node";
}
