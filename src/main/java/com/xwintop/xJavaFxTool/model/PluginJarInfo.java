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
    private String name;//名称
    private String synopsis;//简介
    private String jarName;//jar包名称
    private String version;//版本名称
    private Integer versionNumber;//版本号（用来判断更新）
    private String downloadUrl;//下载地址
    private Boolean isDownload;//是否下载
    private Boolean isEnable;//是否启用
}
