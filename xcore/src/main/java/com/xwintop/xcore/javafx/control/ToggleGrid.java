package com.xwintop.xcore.javafx.control;

import com.xwintop.xcore.util.KeyValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.event.EventListenerSupport;

/**
 * 一个可以通过鼠标拖拽来选择内容的 Grid
 */
public class ToggleGrid<K, V> extends FlowPane {

    private double cellWidth;

    private double cellHeight;

    private List<TogglePane> togglePanes = new ArrayList<>();

    private int selectionStart = -1;

    private int selectionEnd = -1;

    private boolean mouseDragging;

    private EventListenerSupport<Runnable> selectionUpdatedListeners = EventListenerSupport.create(Runnable.class);

    public ToggleGrid(@NamedArg("cellWidth") double cellWidth, @NamedArg("cellHeight")double cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.setHgap(1);
        this.setVgap(1);

        this.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, event -> this.onMouseReleased());
    }

    public void addSelectionUpdatedListener(Runnable runnable) {
        this.selectionUpdatedListeners.addListener(runnable);
    }

    public void select(KeyValue<K, V>... items) {
        HashSet<KeyValue<K, V>> valueSet = new HashSet<>(Arrays.asList(items));
        select(valueSet::contains);
    }

    public void select(Predicate<KeyValue<K, V>> predicate) {
        this.togglePanes.forEach(
            togglePane -> {
                boolean selected = predicate.test(togglePane.item);
                togglePane.setSelected(selected);
            }
        );
        selectionUpdated();
    }

    public void clearSelection() {
        this.togglePanes.stream()
            .filter(TogglePane::isSelected)
            .forEach(togglePane -> togglePane.setSelected(false));
        selectionUpdated();
    }

    public List<V> getSelectedValues() {
        return this.togglePanes.stream()
            .filter(TogglePane::isSelected)
            .map(togglePane -> togglePane.item.getValue())
            .collect(Collectors.toList());
    }

    public List<K> getSelectedKeys() {
        return this.togglePanes.stream()
            .filter(TogglePane::isSelected)
            .map(togglePane -> togglePane.item.getKey())
            .collect(Collectors.toList());
    }

    public void addCell(KeyValue<K, V> item) {
        TogglePane togglePane = new TogglePane(cellWidth, cellHeight, item, this.togglePanes.size());
        this.getChildren().add(togglePane);
        this.togglePanes.add(togglePane);
    }

    //////////////////////////////////////////////////////////////

    private TogglePane startTogglePane;

    private boolean startTogglePaneSelected;

    private void onMousePressed(TogglePane togglePane) {
        this.startTogglePane = togglePane;
        this.startTogglePaneSelected = togglePane.isSelected();
        this.selectionStart = togglePane.index;
        this.selectionEnd = this.selectionStart;
        this.mouseDragging = true;
        updateSelectionByRange();
    }

    private void onMouseClicked() {
        // 如果没有移动格子，说明只是一次点击
        if (this.selectionStart == this.selectionEnd) {
            startTogglePane.setSelected(!startTogglePaneSelected);
            selectionUpdated();
        }
    }

    private void onMouseDragOver(TogglePane togglePane) {
        if (!this.mouseDragging) {
            return;
        }
        this.selectionEnd = togglePane.index;
        updateSelectionByRange();
    }

    private void onMouseReleased() {
        this.mouseDragging = false;
    }

    //////////////////////////////////////////////////////////////

    private void updateSelectionByRange() {
        int start = Math.min(this.selectionStart, this.selectionEnd);
        int end = Math.max(this.selectionStart, this.selectionEnd);
        this.togglePanes.forEach(togglePane -> {
            if (togglePane.index >= start && togglePane.index <= end) {
                togglePane.setSelected(true);
            }
        });
        selectionUpdated();
    }

    private void selectionUpdated() {
        this.selectionUpdatedListeners.fire().run();
    }

    public class TogglePane extends VBox {

        private final Label label = new Label();

        private int index;

        private KeyValue<K, V> item;

        private BooleanProperty selected = new SimpleBooleanProperty();

        public TogglePane(double width, double height, KeyValue<K, V> item, int index) {
            this.setAlignment(Pos.CENTER);
            this.setPrefSize(width, height);
            this.setMinSize(width, height);
            this.setMaxSize(width, height);
            setUnselected();

            this.getChildren().add(label);
            this.label.setText(String.valueOf(item));
            this.index = index;
            this.item = item;

            this.selected.addListener((observable, oldValue, selected) -> {
                if (selected) {
                    setSelected();
                } else {
                    setUnselected();
                }
            });

            this.addEventHandler(MouseDragEvent.DRAG_DETECTED, event -> this.startFullDrag());
            this.addEventHandler(MouseDragEvent.MOUSE_PRESSED, event -> onMousePressed(this));
            this.addEventHandler(MouseDragEvent.MOUSE_CLICKED, event -> onMouseClicked());
            this.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, event -> onMouseDragOver(this));
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        private void setUnselected() {
            this.setStyle("-fx-background-color: #DDDDDD");
            this.label.setStyle("-fx-text-fill: #000000");
        }

        private void setSelected() {
            this.setStyle("-fx-background-color: #444444;");
            this.label.setStyle("-fx-text-fill: #FFFFFF");
        }

        public String getText() {
            return this.label.getText();
        }
    }
}
