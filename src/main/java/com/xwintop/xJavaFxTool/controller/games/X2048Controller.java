package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.services.games.X2048Service;
import com.xwintop.xJavaFxTool.view.games.X2048View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: X2048Controller
 * @Description: 2048游戏
 * @author: xufeng
 * @date: 2019/4/25 0025 23:24
 */

@Getter
@Setter
@Slf4j
public class X2048Controller extends X2048View implements EventHandler<KeyEvent> {
    private X2048Service x2048Service = new X2048Service(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
    }

    private void initService() {
        x2048Service.Init();
        Platform.runLater(()->{
            x2048Service.ImplInit(4);
        });
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            x2048Service.OnKeyPressed(event);
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            x2048Service.OnKeyReleased(event);
        }
    }

    public void OnReset(ActionEvent actionEvent) {
        x2048Service.ImplInit(x2048Service.getSize());
    }

    public void OnSwipeDown(SwipeEvent swipeEvent) {
        x2048Service.ProcessCode(KeyCode.DOWN);
    }

    public void OnSwipeLeft(SwipeEvent swipeEvent) {
        x2048Service.ProcessCode(KeyCode.LEFT);
    }

    public void OnSwipeRight(SwipeEvent swipeEvent) {
        x2048Service.ProcessCode(KeyCode.RIGHT);
    }

    public void OnSwipeUp(SwipeEvent swipeEvent) {
        x2048Service.ProcessCode(KeyCode.UP);
    }

    public void OnMousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (!x2048Service.isMMousePressed()) {
                x2048Service.setMMousePressed(true);
                x2048Service.setMPressedPoint(new X2048Service.Point((int) mouseEvent.getX(), (int) mouseEvent.getY()));
            }
        }
    }

    public void OnMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (x2048Service.isMMousePressed()) {
                x2048Service.setMMousePressed(false);
                x2048Service.setMReleasedPoint(new X2048Service.Point((int) mouseEvent.getX(), (int) mouseEvent.getY()));
                x2048Service.MouseManipulation();
            }
        }

    }
}