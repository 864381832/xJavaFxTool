package com.xwintop.xcore.javafx.wrapper;

import com.xwintop.xcore.util.CssProps;
import javafx.scene.Node;

/**
 * 指定一个边框样式，可反复用于界面元素上
 */
public class BorderWrapper {

    public enum BorderStyle {
        none, solid, dotted, dashed,
    }

    public static BorderWrapper of(String color, BorderStyle style, double width) {
        return new BorderWrapper(color, style, width);
    }

    private String color;

    private BorderStyle borderStyle;

    private double width;

    private BorderWrapper(String color, BorderStyle borderStyle, double width) {
        this.color = color;
        this.borderStyle = borderStyle;
        this.width = width;
    }

    public <T extends Node> T wrap(T node) {
        CssProps cssProps = new CssProps()
            .put("-fx-border-style", this.borderStyle.name())
            .put("-fx-border-color", this.color)
            .put("-fx-border-width", String.valueOf(this.width));
        cssProps.applyTo(node);
        return node;
    }
}
