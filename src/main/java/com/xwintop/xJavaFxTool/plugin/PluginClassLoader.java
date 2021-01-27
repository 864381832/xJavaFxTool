package com.xwintop.xJavaFxTool.plugin;

import io.github.classgraph.ClassGraph;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于加载插件的 ClassLoader
 */
public class PluginClassLoader extends URLClassLoader {

    public static PluginClassLoader create(File jarFile) {
        return create(getSystemClassLoader(), jarFile);
    }

    public static PluginClassLoader create(ClassLoader parent, File jarFile) {
        List<URI> uris = new ArrayList<>(new ClassGraph().getClasspathURIs());
        uris.add(jarFile.toURI());

        URL[] urls = uris.stream().map(uri -> {
            try {
                return uri.toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toArray(URL[]::new);

        return new PluginClassLoader(urls, parent);
    }

    private PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
