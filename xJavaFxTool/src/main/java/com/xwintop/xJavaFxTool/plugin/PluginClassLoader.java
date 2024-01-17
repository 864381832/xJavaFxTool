package com.xwintop.xJavaFxTool.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 用于加载插件的 ClassLoader
 */
public class PluginClassLoader extends URLClassLoader {

    public static PluginClassLoader create(File jarFile) {
        return create(getSystemClassLoader(), jarFile);
    }

    public static PluginClassLoader create(ClassLoader parent, File jarFile) {
        URL[] urls = null;
        try {
            urls = new URL[]{jarFile.toURI().toURL()};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new PluginClassLoader(urls, parent);
    }

    private PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
