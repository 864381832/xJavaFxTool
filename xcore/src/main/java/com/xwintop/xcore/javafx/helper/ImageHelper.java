package com.xwintop.xcore.javafx.helper;

import com.xwintop.xcore.XCoreException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.image.Image;

public class ImageHelper {

    public static final String CLASSPATH_PREFIX = "classpath:";

    public static final String HTTP_PREFIX = "http:";

    public static final String HTTPS_PREFIX = "https:";

    public static final String FILE_PREFIX = "file:";

    /**
     * 根据 path 得到 image 对象。
     *
     * @param path 如果以 classpath: 开头则从资源中读取，
     *             如果以 http: https: 或 file: 开头则作为 URL 读取，
     *             否则作为本地文件路径读取。
     *
     * @return Image 对象
     */
    public static Image image(String path) {
        try {
            final String lowerCasePath = path.toLowerCase();

            Image image;
            if (lowerCasePath.startsWith(CLASSPATH_PREFIX)) {
                image = new Image(ImageHelper.class.getResourceAsStream(path.substring(CLASSPATH_PREFIX.length())));
            } else if (lowerCasePath.startsWith(HTTP_PREFIX)
                || lowerCasePath.startsWith(HTTPS_PREFIX)
                || lowerCasePath.startsWith(FILE_PREFIX)) {
                image = new Image(path);
            } else {
                image = new Image(Files.newInputStream(Paths.get(path)));
            }
            return image;
        } catch (IOException e) {
            throw new XCoreException(e);
        }
    }
}
