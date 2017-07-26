package com.xwintop.xJavaFxTool;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.xwintop.xJavaFxTool.controller.IndexController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
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
		
		Tab tab = new Tab("epms转换");
		FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/epmsTools/GeneratingCode.fxml"), resourceBundle);
		tab.setContent(generatingCodeFXMLLoader.load());
		indexController.getTabPaneMain().getTabs().add(tab);
		
		Tab tab2 = new Tab("epms调试工具");
		FXMLLoader debugEpmsFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/epmsTools/DebugEpms.fxml"), resourceBundle);
		tab2.setContent(debugEpmsFXMLLoader.load());
		indexController.getTabPaneMain().getTabs().add(tab2);
		
		Tab tab3 = new Tab("javaFx转换");
		FXMLLoader javaFxXmlToObjectCodeFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml"), resourceBundle);
		tab3.setContent(javaFxXmlToObjectCodeFXMLLoader.load());
		indexController.getTabPaneMain().getTabs().add(tab3);
		
		Tab tab4 = new Tab("路径转换");
		FXMLLoader linuxPathToWindowsPathFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/littleTools/LinuxPathToWindowsPath.fxml"), resourceBundle);
		tab4.setContent(linuxPathToWindowsPathFXMLLoader.load());
		indexController.getTabPaneMain().getTabs().add(tab4);
		
		Tab tab5 = new Tab("报文查看");
		FXMLLoader messageViewerFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/epmsTools/MessageViewer.fxml"), resourceBundle);
		tab5.setContent(messageViewerFXMLLoader.load());
		indexController.getTabPaneMain().getTabs().add(tab5);
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