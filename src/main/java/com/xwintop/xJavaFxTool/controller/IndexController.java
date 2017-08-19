package com.xwintop.xJavaFxTool.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import com.xwintop.xJavaFxTool.view.IndexView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @ClassName: IndexController
 * @Description: 主页
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
public class IndexController extends IndexView {
	private Map<String, String> map = new HashMap<String, String>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		initView();
		initEvent();
	}

	private void initView() {
		map.put(bundle.getString("JavaFxXmlToObjectCode"), "/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml");
		map.put(bundle.getString("GeneratingCode"), "/fxml/epmsTools/GeneratingCode.fxml");
		map.put(bundle.getString("DebugEpms"), "/fxml/epmsTools/DebugEpms.fxml");
		map.put(bundle.getString("LinuxPathToWindowsPath"), "/fxml/littleTools/LinuxPathToWindowsPath.fxml");
		map.put(bundle.getString("MessageViewer"), "/fxml/epmsTools/MessageViewer.fxml");
		map.put(bundle.getString("TimeTool"), "/fxml/littleTools/TimeTool.fxml");
		map.put(bundle.getString("CharacterConverter"), "/fxml/littleTools/CharacterConverter.fxml");
		map.put(bundle.getString("EncryptAndDecrypt"), "/fxml/littleTools/EncryptAndDecrypt.fxml");
		map.put(bundle.getString("CronExpBuilder"), "/fxml/littleTools/CronExpBuilder.fxml");
		map.put(bundle.getString("FileCopy"), "/fxml/littleTools/FileCopy.fxml");
		map.put(bundle.getString("QRCodeBuilder"), "/fxml/littleTools/QRCodeBuilder.fxml");
		map.put(bundle.getString("IdCardGenerator"), "/fxml/codeTools/IdCardGenerator.fxml");
		map.put(bundle.getString("RegexTester"), "/fxml/codeTools/RegexTester.fxml");
		map.put(bundle.getString("ShortURL"), "/fxml/webTools/ShortURL.fxml");
		map.put(bundle.getString("EscapeCharacter"), "/fxml/codeTools/EscapeCharacter.fxml");
		map.put(bundle.getString("ZHConverter"), "/fxml/codeTools/ZHConverter.fxml");

		Map<String, String> webMap = new HashMap<String, String>();
		webMap.put(bundle.getString("webCronExpBuilder"), "/web/littleTools/cron/index.htm");

		map.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String title, String url) {
				MenuItem menuItem = new MenuItem(title);
				menuItem.setOnAction((ActionEvent event) -> {
					Tab tab = new Tab(title);
					FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(url));
					try {
						tab.setContent(generatingCodeFXMLLoader.load());
					} catch (IOException e) {
						e.printStackTrace();
					}
					tabPaneMain.getTabs().add(tab);
					tabPaneMain.getSelectionModel().select(tab);
				});
				toolsMenu.getItems().add(menuItem);
			}
		});

		Map<String, String> openMap = new HashMap<String, String>();
//		openMap.put(bundle.getString("JavaFxXmlToObjectCode"), "/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml");
		openMap.put(bundle.getString("ZHConverter"), "/fxml/littleTools/ZHConverter.fxml");
		openMap.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String t, String u) {
				Tab tab = new Tab(t);
				// ResourceBundle resourceBundle =
				// ResourceBundle.getBundle("locale.Menu", Locale.CHINA);
				// FXMLLoader generatingCodeFXMLLoader = new
				// FXMLLoader(getClass().getResource(u), resourceBundle);
				FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(u));
				try {
					tab.setContent(generatingCodeFXMLLoader.load());
				} catch (IOException e) {
					e.printStackTrace();
				}
				tabPaneMain.getTabs().add(tab);
			}
		});

		webMap.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String title, String url) {
				MenuItem menuItem = new MenuItem(title);
				menuItem.setOnAction((ActionEvent event) -> {
					Tab tab = new Tab(title);
					WebView browser = new WebView();
					WebEngine webEngine = browser.getEngine();
					webEngine.load(IndexController.class.getResource(url).toExternalForm());
					tab.setContent(browser);
					tabPaneMain.getTabs().add(tab);
					tabPaneMain.getSelectionModel().select(tab);
				});
				NetWorkToolsMenu.getItems().add(menuItem);
			}
		});
	}

	private void initEvent() {
		myTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

			}
		});
		myButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				myButtonAction(arg0);
				// TooltipUtil.showToast(myButton,"test");
				TooltipUtil.showToast(myTextField.getText());
				// TooltipUtil.showToast("test",Pos.BOTTOM_RIGHT);
				// JOptionPane.showMessageDialog(null, "test");
			}
		});
	}

	// When user click on myButton
	// this method will be called.
	public void myButtonAction(ActionEvent event) {
		for (MenuItem menuItem : toolsMenu.getItems()) {
			if (menuItem.getText().contains(myTextField.getText())) {
				menuItem.fire();
			}
		}
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
		map.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String title, String url) {
				Tab tab = new Tab(title);
				FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(url));
				try {
					tab.setContent(generatingCodeFXMLLoader.load());
				} catch (IOException e) {
					e.printStackTrace();
				}
				tabPaneMain.getTabs().add(tab);
			}
		});
	}

	@FXML
	private void aboutAction(ActionEvent event) {
		AlertUtil.showInfoAlert(bundle.getString("aboutText"));
	}
}
