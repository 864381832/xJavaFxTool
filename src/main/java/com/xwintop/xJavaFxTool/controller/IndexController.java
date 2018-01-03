package com.xwintop.xJavaFxTool.controller;

import com.xwintop.xJavaFxTool.model.ToolFxmlLoaderConfiguration;
import com.xwintop.xJavaFxTool.services.IndexService;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xJavaFxTool.view.IndexView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: IndexController
 * @Description: 主页
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
public class IndexController extends IndexView {
	private static Logger log = Logger.getLogger(IndexController.class);
	private Map<String, Menu> menuMap = new HashMap<String, Menu>();
	private Map<String, MenuItem> menuItemMap = new HashMap<String, MenuItem>();
	private IndexService indexService = new IndexService();
	private ContextMenu contextMenu = new ContextMenu();

	public static FXMLLoader getFXMLLoader() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Config.defaultLocale);
		URL url = Object.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/Index.fxml");
		FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
		return fXMLLoader;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		initView();
		initEvent();
		initService();
		new Thread(()->{//启动SpringBoot
//			SpringApplication.run(Main.class,new String[0]);
		}).start();
	}

	private void initView() {
		List<ToolFxmlLoaderConfiguration> toolList = XJavaFxSystemUtil.loaderToolFxmlLoaderConfiguration();
		List<ToolFxmlLoaderConfiguration> plugInToolList = XJavaFxSystemUtil.loaderPlugInToolFxmlLoaderConfiguration();
		toolList.addAll(plugInToolList);
		menuMap.put("toolsMenu", toolsMenu);
		menuMap.put("moreToolsMenu", moreToolsMenu);
		menuMap.put("netWorkToolsMenu", netWorkToolsMenu);
		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			try {
				if (StringUtils.isEmpty(toolConfig.getResourceBundleName())) {
					if (StringUtils.isNotEmpty(bundle.getString(toolConfig.getTitle()))) {
						toolConfig.setTitle(bundle.getString(toolConfig.getTitle()));
					}
				} else {
					ResourceBundle resourceBundle = ResourceBundle.getBundle(toolConfig.getResourceBundleName(),
							Config.defaultLocale);
					if (StringUtils.isNotEmpty(resourceBundle.getString(toolConfig.getTitle()))) {
						toolConfig.setTitle(resourceBundle.getString(toolConfig.getTitle()));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if (toolConfig.getIsMenu()) {
				Menu menu = new Menu(toolConfig.getTitle());
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
			MenuItem menuItem = new MenuItem(toolConfig.getTitle());
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
					addWebView(menuItem.getText(), toolConfig.getUrl(), toolConfig.getIconPath());
				});
				if (toolConfig.getIsDefaultShow()) {
					addWebView(menuItem.getText(), toolConfig.getUrl(), toolConfig.getIconPath());
				}
			}
			menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuItem);
			menuItemMap.put(menuItem.getText(), menuItem);
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
		for (MenuItem value : menuItemMap.values()) {
			value.fire();
		}
	}

	/**
	 * @Title: addContent
	 * @Description: 添加Content内容
	 */
	private void addContent(String title, String url, String resourceBundleName, String iconPath) {
		try {
			FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(url));
			if (StringUtils.isNotEmpty(resourceBundleName)) {
				ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, Config.defaultLocale);
				generatingCodeFXMLLoader.setResources(resourceBundle);
			}
			if (singleWindowBootCheckBox.isSelected()) {
				JavaFxViewUtil.getNewStage(title,iconPath, generatingCodeFXMLLoader);
				return;
			}
			Tab tab = new Tab(title);
			if (StringUtils.isNotEmpty(iconPath)) {
				ImageView imageView = new ImageView(new Image(iconPath));
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				tab.setGraphic(imageView);
			}

			tab.setContent(generatingCodeFXMLLoader.load());
			tabPaneMain.getTabs().add(tab);
			tabPaneMain.getSelectionModel().select(tab);

			tab.setOnCloseRequest((Event event)->{
				JavaFxViewUtil.setControllerOnCloseRequest(generatingCodeFXMLLoader.getController(),event);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: addWebView
	 * @Description: 添加WebView视图
	 */
	private void addWebView(String title, String url, String iconPath) {
		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		if(url.startsWith("http")){
			webEngine.load(url);
		}else{
			webEngine.load(IndexController.class.getResource(url).toExternalForm());
		}
		if (singleWindowBootCheckBox.isSelected()) {
			JavaFxViewUtil.getNewStage(title, iconPath, new BorderPane(browser));
			return;
		}
		Tab tab = new Tab(title);
		if (StringUtils.isNotEmpty(iconPath)) {
			ImageView imageView = new ImageView(new Image(iconPath));
			imageView.setFitHeight(18);
			imageView.setFitWidth(18);
			tab.setGraphic(imageView);
		}
		tab.setContent(browser);
		tabPaneMain.getTabs().add(tab);
		tabPaneMain.getSelectionModel().select(tab);
	}

	@FXML
	private void aboutAction(ActionEvent event) throws Exception {
		AlertUtil.showInfoAlert(bundle.getString("aboutText")+Config.xJavaFxToolVersions);
	}

	@FXML
	private void setLanguageAction(ActionEvent event) throws Exception {
		MenuItem menuItem = (MenuItem) event.getSource();
		indexService.setLanguageAction(menuItem.getText());
	}
}
