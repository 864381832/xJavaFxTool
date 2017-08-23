package com.xwintop.xJavaFxTool;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	private static Logger log = Logger.getLogger(Main.class);
	private static Stage stage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		XJavaFxSystemUtil.initSystemLocal();
		XJavaFxSystemUtil.addJarByLibs();
		
		FXMLLoader fXMLLoader = IndexController.getFXMLLoader();
		ResourceBundle resourceBundle = fXMLLoader.getResources();
		Parent root = fXMLLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setTitle(resourceBundle.getString("Title"));
		primaryStage.getIcons().add(new Image("/images/icon.jpg"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});

		stage = primaryStage;
		// IndexController indexController = fXMLLoader.getController();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(Main.class.getResource("/config/log4j.properties"));
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setStage(Stage stage) {
		Main.stage = stage;
	}
}