package com.xwintop.xcore.javafx.control;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * “面包屑” 导航控件
 */
public class Breadcrumb extends HBox {

    public static final EventType<PathChangedEvent> PATH_CHANGED = new EventType<>(Event.ANY, "PATH_CHANGED");

    public static final class PathChangedEvent extends Event {

        private final List<String> path;

        public PathChangedEvent(List<String> path) {
            super(PATH_CHANGED);
            this.path = path;
        }

        public List<String> getPath() {
            return path;
        }
    }

    //////////////////////////

    private List<String> path;

    public Breadcrumb() {
        setAlignment(Pos.BASELINE_LEFT);
        setPadding(new Insets(5));
        setSpacing(4);
    }

    /**
     * 添加路径
     *
     * @param value 路径
     */
    public void appendPath(String value) {
        this.path.add(value);
        refresh();
        fireEvent(new PathChangedEvent(Collections.unmodifiableList(this.path)));
    }

    /**
     * 设置路径
     *
     * @param path 路径
     */
    public void setPath(List<String> path) {
        this.path = path;
        refresh();
        fireEvent(new PathChangedEvent(Collections.unmodifiableList(this.path)));
    }

    /**
     * 设置路径
     *
     * @param path 路径
     */
    public void setPath(String... path) {
        setPath(Arrays.asList(path));
    }

    private void refresh() {
        getChildren().clear();
        for (int i = 0; i < path.size(); i++) {
            String s = path.get(i);
            getChildren().add(pathItem(s, i));
            if (i < path.size() - 1) {
                getChildren().add(pathSplitter());
            }
        }
    }

    private Label pathSplitter() {
        Label label = new Label("›");
        label.setStyle("-fx-text-fill: #999999");
        label.setPadding(new Insets(0, 0, 2, 0));
        return label;
    }

    private Label pathItem(String s, int index) {
        Label label = new Label(s);
        label.setStyle("-fx-cursor: hand;-fx-text-fill: #0076a3;");
        label.setOnMouseEntered(e -> label.setUnderline(true));
        label.setOnMouseExited(e -> label.setUnderline(false));
        label.setOnMouseClicked(e -> selectLocation(index));
        return label;
    }

    private void selectLocation(int index) {
        setPath(this.path.subList(0, index + 1));
    }
}
