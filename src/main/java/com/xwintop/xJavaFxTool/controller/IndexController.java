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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		initView();
		initEvent();
	}
	
	private void initView(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("javaFx转换", "/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml");
		map.put("epms转换", "/fxml/epmsTools/GeneratingCode.fxml");
		map.put("epms调试工具", "/fxml/epmsTools/DebugEpms.fxml");
		map.put("路径转换", "/fxml/littleTools/LinuxPathToWindowsPath.fxml");
		map.put("报文查看", "/fxml/epmsTools/MessageViewer.fxml");
		map.put("Time转换", "/fxml/littleTools/TimeTool.fxml");
		map.put("编码转换", "/fxml/littleTools/CharacterConverter.fxml");
		map.put("加密解密", "/fxml/littleTools/EncryptAndDecrypt.fxml");
		map.put("Cron表达式生成器", "/fxml/littleTools/CronExpBuilder.fxml");
		map.put("文件复制", "/fxml/littleTools/FileCopy.fxml");
		map.put("二维码生成工具", "/fxml/littleTools/QRCodeBuilder.fxml");
		map.put("身份证生成器", "/fxml/codeTools/IdCardGenerator.fxml");
		map.put("正则表达式生成工具", "/fxml/codeTools/RegexTester.fxml");
		map.put("网址缩短", "/fxml/webTools/ShortURL.fxml");
		
		Map<String,String> webMap = new HashMap<String,String>();
		webMap.put("Cron表达式生成器Html版", "/web/littleTools/cron/index.htm");
		
		map.forEach(new BiConsumer<String,String>() {
			@Override
			public void accept(String title, String url) {
				MenuItem menuItem = new MenuItem(title);
				menuItem.setOnAction((ActionEvent event)->{
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
		
		Map<String,String> openMap = new HashMap<String,String>();
		openMap.put("javaFx转换", "/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml");
		openMap.put("网址缩短", "/fxml/webTools/ShortURL.fxml");
		openMap.forEach(new BiConsumer<String,String>() {
			@Override
			public void accept(String t, String u) {
				Tab tab = new Tab(t);
//				ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Locale.CHINA);
//				FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(u), resourceBundle);
				FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(u));
				try {
					tab.setContent(generatingCodeFXMLLoader.load());
				} catch (IOException e) {
					e.printStackTrace();
				}
				tabPaneMain.getTabs().add(tab);
			}
		});
		
		webMap.forEach(new BiConsumer<String,String>() {
			@Override
			public void accept(String title, String url) {
				MenuItem menuItem = new MenuItem(title);
				menuItem.setOnAction((ActionEvent event)->{
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
	private void initEvent(){
		myTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
			}
		});
		myButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				myButtonAction(arg0);
//				TooltipUtil.showToast(myButton,"test");
				TooltipUtil.showToast(myTextField.getText());
//				TooltipUtil.showToast("test",Pos.BOTTOM_RIGHT);
//				JOptionPane.showMessageDialog(null, "test");
			}
		});
	}

	// When user click on myButton
	// this method will be called.
	public void myButtonAction(ActionEvent event) {
		for(MenuItem menuItem:toolsMenu.getItems()){
			if(menuItem.getText().contains(myTextField.getText())){
				menuItem.fire();
			}
		}
	}
	
	@FXML
	private void exitAction(ActionEvent event){
		System.exit(0);
	}
	
	@FXML
	private void closeAllTabAction(ActionEvent event){
		tabPaneMain.getTabs().clear();
	}

	@FXML
	private void aboutAction(ActionEvent event){
		AlertUtil.showInfoAlert("欢迎使用JavaFx工具集合。");
	}
}
