package com.xwintop.xJavaFxTool;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.xwintop.xJavaFxTool.controller.IndexController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	private static Logger log = Logger.getLogger(Main.class);

	@Override
	public void start(Stage primaryStage) throws Exception {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Locale.CHINA);
		URL url = getClass().getResource("/fxml/Index.fxml");
//		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.epmsTools.Menu", Locale.UK);
//		URL url = getClass().getResource("/fxml/epmsTools/GeneratingCode.fxml");

		FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
//		Parent root = FXMLLoader.load(url, resourceBundle);
		Parent root = fXMLLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setTitle("MyJavaFxTool");
		primaryStage.getIcons().add(new Image("/images/icon.jpg"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		IndexController indexController = fXMLLoader.getController();
		Map<String,String> map = new HashMap<String,String>();
//		map.put("epms转换", "/fxml/epmsTools/GeneratingCode.fxml");
//		map.put("epms调试工具", "/fxml/epmsTools/DebugEpms.fxml");
//		map.put("javaFx转换", "/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml");
//		map.put("路径转换", "/fxml/littleTools/LinuxPathToWindowsPath.fxml");
//		map.put("报文查看", "/fxml/epmsTools/MessageViewer.fxml");
//		map.put("Time转换", "/fxml/littleTools/TimeTool.fxml");
		map.put("编码转换", "/fxml/littleTools/CharacterConverter.fxml");
		map.forEach(new BiConsumer<String,String>() {
			@Override
			public void accept(String t, String u) {
				Tab tab = new Tab(t);
				FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(u), resourceBundle);
				try {
					tab.setContent(generatingCodeFXMLLoader.load());
				} catch (IOException e) {
					e.printStackTrace();
				}
				indexController.getTabPaneMain().getTabs().add(tab);
			}
		});
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(Main.class.getResource("/config/log4j.properties"));
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			log.error(e);
		}
	}
}