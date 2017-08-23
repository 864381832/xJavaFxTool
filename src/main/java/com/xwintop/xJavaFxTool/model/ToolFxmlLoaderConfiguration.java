package com.xwintop.xJavaFxTool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolFxmlLoaderConfiguration {
	private String url;//资源url
	private String resourceBundleName;//国际化资源文件
	private String className;//class名称
	private String title;//标题（配合国际化资源文件，无着默认显示原字符）
	private String iconPath;//图标路径
	private Boolean isDefaultShow = false;// 是否默认打开
	private String menuId;// 菜单id
	private String menuParentId;// 菜单父id
	private Boolean isMenu = false;//是否为菜单
	private String controllerType = "Node";// 内容类型
}
