package com.xwintop.xcore.javafx.stage;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.iconView;

import com.jfoenix.controls.JFXDecorator;
import com.xwintop.xcore.javafx.FxApp;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StageWrapper {

    /**
     * 创建对话框窗体
     *
     * @param owner            父窗体
     * @param title            标题
     * @param icon             图标（可选）
     * @param root             对话框内容
     * @param fullScreenButton 是否显示全屏按钮
     * @param maximizeButton   是否显示最大化按钮
     * @param minimizeButton   是否显示最小化按钮
     *
     * @return 新建的窗体对象
     */
    public static Stage jfxStage(
        Stage owner, String title, Image icon, Parent root,
        boolean fullScreenButton, boolean maximizeButton, boolean minimizeButton
    ) {
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.setResizable(true);

        if (icon != null) {
            newStage.getIcons().add(icon);
        }

        wrapStage(
            newStage, owner, title, icon, root,
            fullScreenButton, maximizeButton, minimizeButton
        );

        return newStage;
    }

    /**
     * 将普通窗体封装为 JFXDecorator 风格
     */
    public static void wrapStage(
        Stage newStage, Stage owner, String title, Image icon, Parent root,
        boolean fullScreenButton, boolean maximizeButton, boolean minimizeButton
    ) {
        if (owner != null) {
            newStage.initOwner(owner);
            newStage.initModality(Modality.WINDOW_MODAL);

            // 对话框位置跟随父窗体位置
            newStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
                newStage.setX(owner.getX() + owner.getWidth() / 2 - newStage.getWidth() / 2);
                newStage.setY(owner.getY() + owner.getHeight() / 2 - newStage.getHeight() / 2);
            });
        } else {
            newStage.initModality(Modality.APPLICATION_MODAL);
        }

        JFXDecorator decorator = new JFXDecorator(
            newStage, root, fullScreenButton, maximizeButton, minimizeButton
        );
        decorator.setCustomMaximize(true);
        decorator.setTitle(title);

        if (icon != null) {
            decorator.setGraphic(iconView(icon, 16));
        }

        Scene scene = new Scene(decorator);
        scene.getStylesheets().addAll(FxApp.styleSheets);

        newStage.setScene(scene);
    }

}
