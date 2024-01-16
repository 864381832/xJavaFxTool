package com.xwintop.xcore.util.javafx;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * @ClassName: JavaFxDragResizer
 * @Description: 设置控件自定义大小
 * @author: xufeng
 * @date: 2021/12/25 14:40
 */

public class JavaFxDragResizer {

    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 10;
    private final Region region;
    private double y;
    private double x;
    private boolean initMinHeight;
    private boolean initMinWidth;
    private boolean draggableZoneX, draggableZoneY;
    private boolean dragging;
    private boolean draggableX = true;
    private boolean draggableY = true;

    private JavaFxDragResizer(Region aRegion) {
        region = aRegion;
        this.setOnMouse();
    }

    private JavaFxDragResizer(Region aRegion, boolean draggableX, boolean draggableY) {
        region = aRegion;
        this.draggableX = draggableX;
        this.draggableY = draggableY;
        this.setOnMouse();
    }

    public static void makeResizableX(Region region) {
        new JavaFxDragResizer(region, true, false);
    }

    public static void makeResizableY(Region region) {
        new JavaFxDragResizer(region, false, true);
    }

    public static void makeResizable(Region region) {
        new JavaFxDragResizer(region);
    }

    protected void setOnMouse() {
        region.setOnMousePressed(event -> this.mousePressed(event));
        region.setOnMouseDragged(event -> this.mouseDragged(event));
        region.setOnMouseMoved(event -> this.mouseOver(event));
        region.setOnMouseReleased(event -> this.mouseReleased(event));
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event) {
        if (isInDraggableZone(event) || dragging) {
            if (draggableZoneY && draggableZoneX) {
                region.setCursor(Cursor.SE_RESIZE);
            } else if (draggableZoneY) {
                region.setCursor(Cursor.S_RESIZE);
            } else if (draggableZoneX) {
                region.setCursor(Cursor.E_RESIZE);
            }
        } else {
            region.setCursor(Cursor.DEFAULT);
        }
    }


    //had to use 2 variables for the controll, tried without, had unexpected behaviour (going big was ok, going small nope.)
    protected boolean isInDraggableZone(MouseEvent event) {
        draggableZoneY = draggableY && (boolean) (event.getY() > (region.getHeight() - RESIZE_MARGIN));
        draggableZoneX = draggableX && (boolean) (event.getX() > (region.getWidth() - RESIZE_MARGIN));
        return (draggableZoneY || draggableZoneX);
    }

    protected void mouseDragged(MouseEvent event) {
        if (!dragging) {
            return;
        }
        if (draggableY && draggableZoneY) {
            double mousey = event.getY();
            double newHeight = region.getMinHeight() + (mousey - y);
            region.setMinHeight(newHeight);
            y = mousey;
        }
        if (draggableX && draggableZoneX) {
            double mousex = event.getX();
            double newWidth = region.getMinWidth() + (mousex - x);
            region.setMinWidth(newWidth);
            x = mousex;
        }
    }

    protected void mousePressed(MouseEvent event) {
        // ignore clicks outside of the draggable margin
        if (!isInDraggableZone(event)) {
            return;
        }
        dragging = true;
        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight());
            initMinHeight = true;
        }
        y = event.getY();
        if (!initMinWidth) {
            region.setMinWidth(region.getWidth());
            initMinWidth = true;
        }
        x = event.getX();
    }
}