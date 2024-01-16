package com.xwintop.xcore.javafx.cells;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 封装对 TreeView 的配置过程
 *
 * @author yiding.he
 */
public class TreeCellFactory<T> implements Callback<TreeView<T>, TreeCell<T>> {

    private Consumer<T> onDoubleClick;

    private Function<T, String> toString;

    private Function<TreeItem<T>, Image> iconSupplier;

    /**
     * 设置当双击树节点时要做的事
     */
    public TreeCellFactory<T> setOnDoubleClick(Consumer<T> onDoubleClick) {
        this.onDoubleClick = onDoubleClick;
        return this;
    }

    /**
     * 设置树节点文本
     */
    public TreeCellFactory<T> setToString(Function<T, String> toString) {
        this.toString = toString;
        return this;
    }

    /**
     * 设置树节点图标
     */
    public TreeCellFactory<T> setIconSupplier(Function<TreeItem<T>, Image> iconSupplier) {
        this.iconSupplier = iconSupplier;
        return this;
    }

    @Override
    public TreeCell<T> call(TreeView<T> param) {
        TreeCell<T> treeCell = new TreeCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setCellText(item);
                    setCellIcon(this.getTreeItem());
                }
            }

            private void setCellIcon(TreeItem<T> treeItem) {
                if (iconSupplier != null) {
                    Image image = iconSupplier.apply(treeItem);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(16);
                    imageView.setFitHeight(16);
                    setGraphic(imageView);
                }
            }

            private void setCellText(T item) {
                setText(toString != null ? toString.apply(item) : String.valueOf(item));
            }
        };

        if (onDoubleClick != null) {
            treeCell.setOnMouseClicked(event -> {
                if (!treeCell.isEmpty() && event.getClickCount() == 2) {
                    onDoubleClick.accept(treeCell.getItem());
                }
            });
        }

        return treeCell;
    }
}
