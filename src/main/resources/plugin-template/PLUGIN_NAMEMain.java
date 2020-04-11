package [PACKAGE];

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.ResourceBundle;

public class [CLASS_NAME] extends Application {

    public static final ResourceBundle RES = ResourceBundle.getBundle("locale.[PLUGIN_NAME]");

    public static void main(String[] args) {
        launch([CLASS_NAME].class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load([CLASS_NAME].class.getResource("/fxml/[PLUGIN_NAME].fxml"));
        primaryStage.setTitle(RES.getString("Title"));
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }
}