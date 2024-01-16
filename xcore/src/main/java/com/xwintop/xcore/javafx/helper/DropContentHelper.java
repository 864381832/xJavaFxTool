package com.xwintop.xcore.javafx.helper;

import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 令控件能够接受拖拽进来的数据。使用方法参考单元测试
 */
public class DropContentHelper {

    /**
     * 允许控件接受文本
     */
    public static <T extends Node> void acceptText(T node, BiConsumer<T, String> onAccept) {
        node.setOnDragOver(event -> {
            Dragboard board = event.getDragboard();
            if (board.hasString() || board.hasHtml() || board.hasRtf() || board.hasUrl()) {
                event.acceptTransferModes(TransferMode.ANY);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        });
        node.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                onAccept.accept(node, dragboard.getString());
            } else if (dragboard.hasHtml()) {
                onAccept.accept(node, dragboard.getHtml());
            } else if (dragboard.hasRtf()) {
                onAccept.accept(node, dragboard.getRtf());
            } else if (dragboard.hasUrl()) {
                onAccept.accept(node, dragboard.getUrl());
            }
        });
    }

    /**
     * 允许控件接受文件
     */
    public static <T extends Node> void acceptFile(T node, BiConsumer<T, List<File>> onAccept) {
        node.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        });
        node.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                onAccept.accept(node, dragboard.getFiles());
            }
        });
    }

    /**
     * 允许控件接受定制的内容
     *
     * @param node      控件
     * @param predicate 判断内容是否可接受
     * @param onAccept  接受内容的操作
     * @param <T>       控件类型
     */
    public static <T extends Node> void accept(
        T node, Predicate<Dragboard> predicate, BiConsumer<T, Dragboard> onAccept
    ) {
        node.setOnDragOver(event -> {
            if (predicate.test(event.getDragboard())) {
                event.acceptTransferModes(TransferMode.ANY);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        });
        node.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            onAccept.accept(node, dragboard);
        });
    }
}
