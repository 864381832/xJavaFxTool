package com.xwintop.xJavaFxTool;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
		// 默认情况下，Fx运行时会在最后一个stage的close(或hide)后自动关闭，即自动调用Application.stop()
		// 除非通过Platform.setImplicitExit(false)取消这个默认行为。这样,即使所有Fx窗口关闭（或隐藏）,Fx运行时还在正常运行
		// Platform.setImplicitExit(false);
		// ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu",
		// Locale.CHINA);
		URL url = getClass().getResource("/fxml/Index.fxml");

		// FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
		FXMLLoader fXMLLoader = new FXMLLoader(url);
		// Parent root = FXMLLoader.load(url, resourceBundle);
		Parent root = fXMLLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setTitle("MyJavaFxTool");
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
			System.out.println(e.getMessage());
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