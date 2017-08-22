package com.xwintop.xJavaFxTool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolFxmlLoaderConfiguration {
	private String url;
	private String resourceBundleName;
	private String className;
	private String title;
	private String iconPath;//
	private Boolean isDefaultShow = false;// 是否默认打开
	private String menuId;// 菜单id
	private String menuParentId;// 菜单父id
	private Boolean isMenu = false;//是否为菜单
	private String controllerType = "Node";// 内容类型
}
