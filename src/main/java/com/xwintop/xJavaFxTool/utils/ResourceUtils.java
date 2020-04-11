package com.xwintop.xJavaFxTool.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang.StringUtils;

public class ResourceUtils {

    public static URL getResource(String... candidates) {
        for (String candidate : candidates) {
            if (StringUtils.isNotBlank(candidate)) {
                URL url = ResourceUtils.class.getResource(candidate);
                if (url != null) {
                    return url;
                }
            }
        }
        return null;
    }

    public static String readResource(String resource, Charset charset) throws URISyntaxException, IOException {
        Path path = Paths.get(ResourceUtils.class.getResource(resource).toURI());
        return new String(Files.readAllBytes(path), charset);
    }
}
