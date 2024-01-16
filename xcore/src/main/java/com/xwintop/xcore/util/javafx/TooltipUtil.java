package com.xwintop.xcore.util.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.tools.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class TooltipUtil {
    public static void showToast(String message) {
        showToast((Node) null, message);
    }

    public static void showToast(Node node, String message) {
        Window window = Utils.getWindow(node);
        double x = 0;
        double y = 0;
        if (node != null) {
            x = GetScreenUtil.getScreenX(node) + GetScreenUtil.getWidth(node) / 2;
            y = GetScreenUtil.getScreenY(node) + GetScreenUtil.getHeight(node);
        } else {
            x = window.getX() + window.getWidth() / 2;
            y = window.getY() + window.getHeight();
//			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//            x = screenBounds.getWidth() /2;
//            y = screenBounds.getHeight();
        }
        showToast(window, message, 3000, x, y);
    }

    public static void showToast(Window window, String message, long time, double x, double y) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setAutoHide(true);
        tooltip.setOpacity(0.9d);
        tooltip.setWrapText(true);
        tooltip.show(window, x, y);
        tooltip.setAnchorX(tooltip.getAnchorX() - tooltip.getWidth() / 2);
        tooltip.setAnchorY(tooltip.getAnchorY() - tooltip.getHeight());
        if (time > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> tooltip.hide());
                }
            }, time);
        }
    }

    public static void showToast(String message, Pos pos) {
        showToast(null, message, null, 3, pos, null, null, true, true);
    }

    public static void showToast(String title, String message) {
        showToast(title, message, null, 3, Pos.BOTTOM_CENTER, null, null, true, true);
    }

    public static void showToast(String title, String message, Pos pos) {
        showToast(title, message, null, 3, pos, null, null, true, true);
    }

    public static void showToast(String title, String message, Node graphic, double hideTime, Pos pos,
                                 EventHandler<ActionEvent> onAction, Object owner, boolean isHideCloseButton, boolean isDarkStyle) {
        Notifications notificationBuilder = Notifications.create().title(title).text(message).graphic(graphic)
                .hideAfter(Duration.seconds(hideTime)).position(pos).onAction(onAction);
        if (owner != null) {
            notificationBuilder.owner(owner);
        }
        if (isHideCloseButton) {
            notificationBuilder.hideCloseButton();
        }
        if (isDarkStyle) {
            notificationBuilder.darkStyle();
        }
        Platform.runLater(() -> {
            notificationBuilder.show();
        });
    }
}
