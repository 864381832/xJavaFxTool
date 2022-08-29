package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.utils.Config.Keys;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName: StageUtils
 * @Description: 更新场景工具类（解决点击任务栏图标无法最小化问题）
 * @author: xufeng
 * @date: 2020/1/2 15:56
 */

@Slf4j
public class StageUtils {
    //加载Stage边框位置
    public static void loadPrimaryStageBound(Stage stage) {
        try {
            if (!Config.getBoolean(Keys.RememberWindowLocation, true)) {
                return;
            }

            double left = Config.getDouble(Keys.MainWindowLeft, stage.getX());
            double top = Config.getDouble(Keys.MainWindowTop, -1);
            double width = Config.getDouble(Keys.MainWindowWidth, -1);
            double height = Config.getDouble(Keys.MainWindowHeight, -1);

            List<Screen> list = Screen.getScreens();
            double minX = 0;
            for (Screen screen : list) {
                Rectangle2D screenRectangle2 = screen.getBounds();
                if (screenRectangle2.getMinX() < minX) {
                    minX = screenRectangle2.getMinX();
                }
            }

            if (left > minX) {
                stage.setX(left);
            }
            if (top > 0) {
                stage.setY(top);
            }
            if (width > 0) {
                stage.setWidth(width);
            }
            if (height > 0) {
                stage.setHeight(height);
            }
        } catch (Exception e) {
            log.error("初始化界面位置失败：", e);
        }
    }

    //保存Stage边框位置
    public static void savePrimaryStageBound(Stage stage) {
        if (!Config.getBoolean(Keys.RememberWindowLocation, true)) {
            return;
        }
        if (stage == null || stage.isIconified()) {
            return;
        }
        try {
            Config.set(Keys.MainWindowLeft, stage.getX());
            Config.set(Keys.MainWindowTop, stage.getY());
            Config.set(Keys.MainWindowWidth, stage.getWidth());
            Config.set(Keys.MainWindowHeight, stage.getHeight());
        } catch (Exception e) {
            log.error("初始化界面位置失败：", e);
        }
    }
}

