package com.xwintop.xcore.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 使用方法参见单元测试
 */
public class HttpClientUtil {
    public static void openBrowseURL(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void openBrowseURLThrowsException(String url) throws IOException, URISyntaxException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI(url));
    }
}
