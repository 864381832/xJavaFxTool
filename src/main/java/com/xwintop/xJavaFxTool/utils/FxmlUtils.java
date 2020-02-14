package com.xwintop.xJavaFxTool.utils;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FxmlUtils {

    /**
     * Load fxml without throwing exceptions
     */
    public static Parent load(String source) {
        try {
            return FXMLLoader.load(FxmlUtils.class.getResource(source));
        } catch (IOException e) {
            log.error("", e);
            new Alert(AlertType.ERROR, "Unable to load " + source + "\n" + e.toString(), ButtonType.OK).show();
            return new Pane();
        }
    }
}
