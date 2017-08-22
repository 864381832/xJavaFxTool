package com.xwintop.xJavaFxTool;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;

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
		try {
			File file = ConfigureUtil.getConfigureFile("systemConfigure.properties");
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
			String[] locale1 = xmlConfigure.getString("Locale").split("_");
			if(locale1 != null) {
				Config.defaultLocale = new Locale(locale1[0], locale1[1]);
			}
		} catch (Exception e) {
		}
		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Config.defaultLocale);
		URL url = getClass().getResource("/fxml/Index.fxml");

		FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
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