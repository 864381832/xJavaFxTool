package com.xwintop.xJavaFxTool.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 用于加载插件的 ClassLoader
 */
public class PluginClassLoader extends URLClassLoader {

    private static URL fromFile(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public PluginClassLoader(ClassLoader parent, File appFile) {
        super(new URL[]{fromFile(appFile)}, parent);
    }

    public PluginClassLoader(File appFile) {
        this(getSystemClassLoader(), appFile);
    }
}
