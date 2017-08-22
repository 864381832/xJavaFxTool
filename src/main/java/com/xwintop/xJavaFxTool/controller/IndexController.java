package com.xwintop.xJavaFxTool.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;

import com.xwintop.xJavaFxTool.model.ToolFxmlLoaderConfiguration;
import com.xwintop.xJavaFxTool.services.IndexService;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.view.IndexView;
import com.xwintop.xcore.util.javafx.AlertUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @ClassName: IndexController
 * @Description: 主页
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
public class IndexController extends IndexView {
	private Map<String, Menu> menuMap = new HashMap<String, Menu>();
	private Map<String, MenuItem> menuItemMap = new HashMap<String, MenuItem>();
	private IndexService indexService = new IndexService();
	private ContextMenu contextMenu = new ContextMenu();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		List<ToolFxmlLoaderConfiguration> toolList = new ArrayList<ToolFxmlLoaderConfiguration>();
		try {
			XMLConfiguration xml = new XMLConfiguration("config/toolFxmlLoaderConfiguration.xml");
			for (ConfigurationNode configurationNode : xml.getRoot().getChildren("ToolFxmlLoaderConfiguration")) {
				ToolFxmlLoaderConfiguration toolFxmlLoaderConfiguration = new ToolFxmlLoaderConfiguration();
				List<ConfigurationNode> attributes = configurationNode.getAttributes();
				for (ConfigurationNode configuration : attributes) {
					BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
							configuration.getValue());
				}
				List<ConfigurationNode> childrenList = configurationNode.getChildren();
				for (ConfigurationNode configuration : childrenList) {
					BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
							configuration.getValue());
				}
				toolList.add(toolFxmlLoaderConfiguration);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		menuMap.put("toolsMenu", toolsMenu);
		menuMap.put("moreToolsMenu", moreToolsMenu);
		menuMap.put("netWorkToolsMenu", netWorkToolsMenu);
		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			if (StringUtils.isEmpty(toolConfig.getMenuParentId())) {
				toolConfig.setMenuParentId("toolsMenu");
			}
			if (toolConfig.getIsMenu()) {
				Menu menu = new Menu(bundle.getString(toolConfig.getTitle()));
				if (StringUtils.isNotEmpty(toolConfig.getIconPath())) {
					ImageView imageView = new ImageView(new Image(toolConfig.getIconPath()));
					imageView.setFitHeight(18);
					imageView.setFitWidth(18);
					menu.setGraphic(imageView);
				}
				menuMap.put(toolConfig.getMenuId(), menu);
			}
		}

		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			if (toolConfig.getIsMenu()) {
				menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuMap.get(toolConfig.getMenuId()));
			}
		}

		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			if (toolConfig.getIsMenu()) {
				continue;
			}
			MenuItem menuItem = new MenuItem(bundle.getString(toolConfig.getTitle()));
			if (StringUtils.isNotEmpty(toolConfig.getIconPath())) {
				ImageView imageView = new ImageView(new Image(toolConfig.getIconPath()));
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				menuItem.setGraphic(imageView);
			}
			if ("Node".equals(toolConfig.getControllerType())) {
				menuItem.setOnAction((ActionEvent event) -> {
					addContent(menuItem.getText(), toolConfig.getUrl(), toolConfig.getResourceBundleName(),
							toolConfig.getIconPath());
				});
				if (toolConfig.getIsDefaultShow()) {
					addContent(menuItem.getText(), toolConfig.getUrl(), toolConfig.getResourceBundleName(),
							toolConfig.getIconPath());
				}
			} else if ("WebView".equals(toolConfig.getControllerType())) {
				menuItem.setOnAction((ActionEvent event) -> {
					addWebView(menuItem.getText(), toolConfig.getUrl(),toolConfig.getIconPath());
				});
				if (toolConfig.getIsDefaultShow()) {
					addWebView(menuItem.getText(), toolConfig.getUrl(),toolConfig.getIconPath());
				}
			}
			menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuItem);
			menuItemMap.put(menuItem.getText(),menuItem);
		}
	}

	private void initEvent() {
		myTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				selectAction(newValue);
			}
		});
		myButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				selectAction(myTextField.getText());
				// TooltipUtil.showToast(myTextField.getText());
				// TooltipUtil.showToast("test",Pos.BOTTOM_RIGHT);
				// JOptionPane.showMessageDialog(null, "test");
			}
		});
	}

	private void initService() {
		indexService.setBundle(bundle);
		indexService.setMenuItemMap(menuItemMap);
	}

	public void selectAction(String selectText) {
		if (contextMenu.isShowing()) {
			contextMenu.hide();
		}
		contextMenu = indexService.getSelectContextMenu(selectText);
		contextMenu.show(myTextField, null, 0, myTextField.getHeight());
	}

	@FXML
	private void exitAction(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void closeAllTabAction(ActionEvent event) {
		tabPaneMain.getTabs().clear();
	}

	@FXML
	private void openAllTabAction(ActionEvent event) {
		for(MenuItem value:menuItemMap.values()){
			value.fire();
		}
	}

	/**
	 * @Title: addContent
	 * @Description: 添加Content内容
	 */
	private void addContent(String title, String url, String resourceBundleName, String iconPath) {
		Tab tab = new Tab(title);
		if (StringUtils.isNotEmpty(iconPath)) {
			ImageView imageView = new ImageView(new Image(iconPath));
			imageView.setFitHeight(18);
			imageView.setFitWidth(18);
			tab.setGraphic(imageView);
		}
		FXMLLoader generatingCodeFXMLLoader;
		if (StringUtils.isEmpty(resourceBundleName)) {
			generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(url));
		} else {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, Config.defaultLocale);
			generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(url), resourceBundle);
		}
		try {
			tab.setContent(generatingCodeFXMLLoader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		tabPaneMain.getTabs().add(tab);
		tabPaneMain.getSelectionModel().select(tab);
	}

	/**
	 * @Title: addWebView
	 * @Description: 添加WebView视图
	 */
	private void addWebView(String title, String url, String iconPath) {
		Tab tab = new Tab(title);
		if (StringUtils.isNotEmpty(iconPath)) {
			ImageView imageView = new ImageView(new Image(iconPath));
			imageView.setFitHeight(18);
			imageView.setFitWidth(18);
			tab.setGraphic(imageView);
		}
		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		webEngine.load(IndexController.class.getResource(url).toExternalForm());
		tab.setContent(browser);
		tabPaneMain.getTabs().add(tab);
		tabPaneMain.getSelectionModel().select(tab);
	}

	@FXML
	private void aboutAction(ActionEvent event) {
		AlertUtil.showInfoAlert(bundle.getString("aboutText"));
	}

	@FXML
	private void setLanguageAction(ActionEvent event) throws Exception {
		MenuItem menuItem = (MenuItem) event.getSource();
		indexService.setLanguageAction(menuItem.getText());
	}
}
