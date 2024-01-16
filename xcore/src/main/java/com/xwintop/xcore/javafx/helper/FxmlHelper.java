package com.xwintop.xcore.javafx.helper;

import com.xwintop.xcore.XCoreException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class FxmlHelper {

    public static FXMLLoader loadFromResource(String resourcePath) {
        return loadFromResource(resourcePath, null);
    }

    public static FXMLLoader loadFromResource(String resourcePath, String bundleName) {
        try {
            URL resource = FxmlHelper.class.getResource(resourcePath);
            if (resource == null) {
                throw new IllegalArgumentException("invalid fxml path " + resourcePath);
            }

            FXMLLoader fxmlLoader;
            if (StringUtils.isNotBlank(bundleName)) {
                fxmlLoader = new FXMLLoader(resource, ResourceBundle.getBundle(bundleName));
            } else {
                fxmlLoader = new FXMLLoader(resource);
            }

            fxmlLoader.load();
            return fxmlLoader;
        } catch (IOException e) {
            throw new XCoreException(e);
        }
    }

    public static Stage loadIntoStage(String fxml, String bundleName, Stage stage) {
        stage.setScene(new Scene(loadFromResource(fxml, bundleName).getRoot()));
        return stage;
    }

    public static Stage loadIntoStage(String fxml, Stage stage) {
        stage.setScene(new Scene(loadFromResource(fxml).getRoot()));
        return stage;
    }
}
