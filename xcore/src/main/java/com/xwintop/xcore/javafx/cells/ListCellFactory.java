package com.xwintop.xcore.javafx.cells;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 封装对列表节点的配置
 *
 * @author yiding.he
 */
public class ListCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

    private Function<T, String> textFunction;

    private Function<T, ObservableValue<String>> textProperty;

    private Function<T, Node> graphicFunction;

    private Function<T, ObservableValue<Node>> graphicProperty;

    private Consumer<ListCell<T>> cellInitializer;

    /**
     * 对 ListCell 的额外配置
     */
    public ListCellFactory<T> setCellInitializer(Consumer<ListCell<T>> cellInitializer) {
        this.cellInitializer = cellInitializer;
        return this;
    }

    /**
     * 设置列表项文本
     */
    public ListCellFactory<T> withTextFunction(Function<T, String> toStringFunction) {
        if (this.textProperty != null) {
            throw new IllegalStateException("You have already assigned textProperty.");
        }
        this.textFunction = toStringFunction;
        return this;
    }

    /**
     * 绑定列表项文本为一个自动更新的属性
     */
    public ListCellFactory<T> withTextProperty(Function<T, ObservableValue<String>> toStringProperty) {
        if (this.textFunction != null) {
            throw new IllegalStateException("You have already assigned textFunction.");
        }
        this.textProperty = toStringProperty;
        return this;
    }

    /**
     * 设置列表项图标
     */
    public ListCellFactory<T> withGraphicFunction(Function<T, Node> graphicFunction) {
        if (this.graphicProperty != null) {
            throw new IllegalStateException("You have already assigned graphicProperty.");
        }
        this.graphicFunction = graphicFunction;
        return this;
    }

    /**
     * 绑定列表项图标为一个自动更新的属性
     */
    public ListCellFactory<T> withGraphicProperty(Function<T, ObservableValue<Node>> graphicProperty) {
        if (this.graphicFunction != null) {
            throw new IllegalStateException("You have already assigned graphicFunction.");
        }
        this.graphicProperty = graphicProperty;
        return this;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {

        ListCell<T> listCell = new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (textProperty().isBound()) {
                    textProperty().unbind();
                }
                if (graphicProperty().isBound()) {
                    graphicProperty().unbind();
                }

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setCellText(this, item);
                    setCellGraphic(this, item);
                }
            }
        };

        if (cellInitializer != null) {
            cellInitializer.accept(listCell);
        }

        return listCell;
    }

    private void setCellGraphic(ListCell<T> cell, T item) {
        if (graphicFunction != null) {
            cell.setGraphic(graphicFunction.apply(item));
        }
        if (graphicProperty != null) {
            cell.graphicProperty().unbind();
            cell.graphicProperty().bind(graphicProperty.apply(item));
        }
    }

    private void setCellText(ListCell<T> cell, T item) {
        if (textFunction != null) {
            cell.setText(textFunction.apply(item));
        }
        if (textProperty != null) {
            cell.textProperty().unbind();
            cell.textProperty().bind(textProperty.apply(item));
        }
        if (textFunction == null && textProperty == null) {
            cell.setText(String.valueOf(item));
        }
    }
}
