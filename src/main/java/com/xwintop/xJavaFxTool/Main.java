package com.xwintop.xJavaFxTool;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

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
		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Locale.UK);
		URL url = getClass().getResource("/fxml/Index.fxml");
//		ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.epmsTools.Menu", Locale.UK);
//		URL url = getClass().getResource("/fxml/epmsTools/GeneratingCode.fxml");

		Parent root = FXMLLoader.load(url, resourceBundle);
		primaryStage.setResizable(true);
		primaryStage.setTitle("MyJavaFxTool");
		primaryStage.getIcons().add(new Image("/images/icon.jpg"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(Main.class.getResource("/config/log4j.properties"));
		try {
			Main.launch(args);
		} catch (Exception e) {
			log.error(e);
		}
	}
}