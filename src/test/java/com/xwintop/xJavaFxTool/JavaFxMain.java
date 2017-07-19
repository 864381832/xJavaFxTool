package com.xwintop.xJavaFxTool;

import org.junit.Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass().getResource("/com/xwintop/xJavaFxTool/MyScene.fxml"));
			primaryStage.setTitle("My Application");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// launch(args);
	// }

	@Test
	public void launchMain() {
		JavaFxMain.launch(null);
	}
}
