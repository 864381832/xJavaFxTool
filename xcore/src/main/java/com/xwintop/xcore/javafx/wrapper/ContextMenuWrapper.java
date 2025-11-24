package com.xwintop.xcore.javafx.wrapper;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

public class ContextMenuWrapper {

    public static ContextMenuWrapper of(ContextMenu contextMenu) {
        return new ContextMenuWrapper(contextMenu);
    }

    private final ContextMenu contextMenu;

    private ContextMenuWrapper(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public <T extends Node> T wrap(T node) {
        node.setOnContextMenuRequested(event -> {
            if (contextMenu != null && event.getSource() instanceof Node) {
                contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
        return node;
    }
}
