package com.xwintop.xJavaFxTool.utils;

import java.net.URL;
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
}
