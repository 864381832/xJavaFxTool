package com.xwintop.xcore.javafx.helper;

import java.util.function.Supplier;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;

public class MenuHelper {

    public static ContextMenu contextMenu(MenuItem... menuItems) {
        return new ContextMenu(menuItems);
    }

    public static MenuItem menuItem(String text, Runnable action) {
        return menuItem(text, (Supplier<ImageView>) null, action);
    }

    public static MenuItem menuItem(String text, ImageView icon, Runnable action) {
        return menuItem(text, () -> icon, action);
    }

    public static MenuItem menuItem(String text, Supplier<ImageView> iconSupplier, Runnable action) {
        final MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(event -> action.run());
        if (iconSupplier != null) {
            menuItem.setGraphic(iconSupplier.get());
        }
        return menuItem;
    }

    public static SeparatorMenuItem separator() {
        return new SeparatorMenuItem();
    }
}
