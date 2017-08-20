package com.xwintop.xJavaFxTool.services;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class IndexService {
	private ResourceBundle bundle;
	private Menu toolsMenu;
	
	public void setLanguageAction(String languageType) throws Exception {
		File file = ConfigureUtil.getConfigureFile("systemConfigure.properties");
		FileUtils.touch(file);
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
		if ("简体中文".equals(languageType)) {
			xmlConfigure.setProperty("Locale", Locale.SIMPLIFIED_CHINESE);
		} else if ("English".equals(languageType)) {
			xmlConfigure.setProperty("Locale", Locale.US);
		}
		xmlConfigure.save();
		AlertUtil.showInfoAlert(bundle.getString("SetLanguageText"));
	}
	
	public ContextMenu getSelectContextMenu(String selectText) {
		ContextMenu contextMenu = new ContextMenu();
		for (MenuItem menuItem : toolsMenu.getItems()) {
			if (menuItem.getText().contains(selectText)) {
				MenuItem menu_tab = new MenuItem(menuItem.getText());
				menu_tab.setOnAction(event1 -> {
					menuItem.fire();
				});
				contextMenu.getItems().add(menu_tab);
			}
		}
		return contextMenu;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public void setToolsMenu(Menu toolsMenu) {
		this.toolsMenu = toolsMenu;
	}
}
