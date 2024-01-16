package com.xwintop.xcore.util.javafx;

import com.xwintop.xcore.XCoreException;
import javafx.fxml.FXMLLoader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * 调用者最好自己提供 ClassLoader 用于加载自身的资源文件，
 * 否则使用 xcore 的 ClassLoader 加载可能会由于安全原因而失败
 */
public class FxmlUtil {

    /**
     * @deprecated Use {@link #loadFxmlFromResource(ClassLoader, String)} )} instead.
     */
    public static FXMLLoader loadFxmlFromResource(String resourcePath) {
        return loadFxmlFromResource(FxmlUtil.class.getClassLoader(), resourcePath, null);
    }

    /**
     * @deprecated Use {@link #loadFxmlFromResource(ClassLoader, String, ResourceBundle)} instead.
     */
    public static FXMLLoader loadFxmlFromResource(String resourcePath, ResourceBundle resourceBundle) {
        return loadFxmlFromResource(FxmlUtil.class.getClassLoader(), resourcePath, resourceBundle);
    }

    public static FXMLLoader loadFxmlFromResource(ClassLoader classLoader, String resourcePath) {
        return loadFxmlFromResource(classLoader, resourcePath, null);
    }

    public static FXMLLoader loadFxmlFromResource(
        ClassLoader classLoader, String resourcePath, ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(classLoader.getResource(StringUtils.removeStart(resourcePath, "/")));
            fxmlLoader.setResources(resourceBundle);
            fxmlLoader.load();
            return fxmlLoader;
        } catch (IOException e) {
            throw new XCoreException(e);
        }
    }
}
