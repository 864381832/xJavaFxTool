package com.xwintop.xcore.util.javafx;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Slf4j
public class JavaFxSystemUtil {

    /**
     * @deprecated 使用 {@link com.xwintop.xcore.javafx.FxApp#primaryStage}
     */
    @Deprecated
    public static Stage mainStage = null;

    /**
     * 打开目录
     *
     * @param directoryPath 目录路径
     */
    public static void openDirectory(String directoryPath) {
        try {
            Desktop.getDesktop().open(new File(directoryPath));
        } catch (IOException e) {
            log.error("打开目录异常：" + directoryPath, e);
        }
    }

    /**
     * 获取屏幕尺寸
     *
     * @param width  宽度比
     * @param height 高度比
     *
     * @return 屏幕尺寸
     */
    public static double[] getScreenSizeByScale(double width, double height) {
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getBounds();
        double screenWidth = screenBounds.getWidth() * width;
        double screenHeight = screenBounds.getHeight() * height;
        Rectangle2D bounds = screen.getVisualBounds();
        if (screenWidth > bounds.getWidth() || screenHeight > bounds.getHeight()) {//解决屏幕缩放问题
            screenWidth = bounds.getWidth();
            screenHeight = bounds.getHeight();
        }
        return new double[]{screenWidth, screenHeight};
    }
}
