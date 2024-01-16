package com.xwintop.xcore.javafx;

import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.icon;

/**
 * 用于存储 App 全局的相关属性
 */
public class FxApp {

    // 获得 App 主窗体
    public static Stage primaryStage;

    // 获得 App 图标，用于主窗体和对话框上
    public static Image appIcon;

    // 获得全局 CSS，用于主窗体和对话框上
    public static final List<String> styleSheets = new ArrayList<>();

    // 初始化全局属性
    public static void init(Stage primaryStage, String iconPath) {
        FxApp.primaryStage = primaryStage;
        JavaFxSystemUtil.mainStage = primaryStage;  // 为兼容性保留

        if (StringUtils.isNotBlank(iconPath)) {
            FxApp.appIcon = icon(iconPath);
            primaryStage.getIcons().add(FxApp.appIcon);
        }
    }

    // 给窗体设置全局图标
    public static void setupIcon(Stage stage) {
        if (appIcon != null) {
            stage.getIcons().clear();
            stage.getIcons().add(appIcon);
        }
    }

    // 给窗体设置 owner
    public static void setupModality(Stage stage) {
        if (stage != null && FxApp.primaryStage != null && FxApp.primaryStage.isShowing()) {
            stage.initOwner(FxApp.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
        }
    }

    // 给窗体设置 owner
    public static void setupModality(Dialog<?> stage) {
        if (stage != null && FxApp.primaryStage != null && FxApp.primaryStage.isShowing()) {
            stage.initOwner(FxApp.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
        }
    }

    /**
     * 如果当前线程属于 UI 线程，则执行 runnable，否则调用 Platform.runLater() 来执行 runnable。
     * 这样能保证 runnable 是在 UI 线程上执行。
     *
     * @param runnable 需要在 UI 线程执行的任务
     */
    public static void runLater(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
