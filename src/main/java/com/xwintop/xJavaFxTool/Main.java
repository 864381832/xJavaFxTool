package com.xwintop.xJavaFxTool;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	private static Logger log = Logger.getLogger(Main.class);

	@Override
	public void start(Stage primaryStage) throws Exception {
//		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Locale.CHINA);
		URL url = getClass().getResource("/fxml/Index.fxml");

//		FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
		FXMLLoader fXMLLoader = new FXMLLoader(url);
//		Parent root = FXMLLoader.load(url, resourceBundle);
		Parent root = fXMLLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setTitle("MyJavaFxTool");
		primaryStage.getIcons().add(new Image("/images/icon.jpg"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
//		IndexController indexController = fXMLLoader.getController();
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