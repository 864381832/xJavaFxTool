package com.xwintop.xcore.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

/**
 * 使用方法参见单元测试
 */
public class HttpClientUtil {

    public static final Duration DEFAULT_CALL_TIMEOUT = Duration.ofMinutes(2);

    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofMinutes(2);

    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofMinutes(2);

    public static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ofMinutes(2);

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
