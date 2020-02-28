package com.xwintop.xJavaFxTool.model;

import lombok.Data;

/**
 * @ClassName: PluginJarInfo
 * @Description: 插件信息
 * @author: xufeng
 * @date: 2020/1/20 17:28
 */

@Data
public class PluginJarInfo {

    ////////////////////////////////////////////////////////////// 下面的属性在远程插件列表和本地配置中都存在

    private String name;            // 名称

    private String synopsis;        // 简介

    private String jarName;         // jar包名称

    private String version;         // 版本名称

    private Integer versionNumber;  // 版本号（用来判断更新）

    private String downloadUrl;     // 下载地址

    ////////////////////////////////////////////////////////////// 下面的属性在远程插件列表中不存在

    private Boolean isDownload;     // 是否下载

    private Boolean isEnable;       // 是否启用

    private Integer localVersionNumber;    // 插件本地版本
}
