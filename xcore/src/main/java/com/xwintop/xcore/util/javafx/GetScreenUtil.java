package com.xwintop.xcore.util.javafx;

import javafx.scene.Node;

public class GetScreenUtil {

    public static double getScreenX(Node control) {
        return control.getScene().getWindow().getX() + control.getScene().getX() + control.localToScene(0, 0).getX();
    }

    public static double getScreenY(Node control) {
        return control.getScene().getWindow().getY() + control.getScene().getY() + control.localToScene(0, 0).getY();
    }

    public static double getWidth(Node control) {
        return control.getBoundsInParent().getWidth();
    }

    public static double getHeight(Node control) {
        return control.getBoundsInParent().getHeight();
    }
}
