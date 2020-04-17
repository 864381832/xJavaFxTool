package com.xwintop.xJavaFxTool.plugin;

import static org.apache.commons.lang.StringUtils.defaultString;

import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 用来解析插件文件中的 toolFxmlLoaderConfiguration.xml
 */
@Slf4j
public class PluginParser {

    public static final String ENTRY_NAME = "config/toolFxmlLoaderConfiguration.xml";

    /**
     * 解析插件文件，补完 pluginJarInfo 属性
     */
    public static void parse(File pluginFile, PluginJarInfo pluginJarInfo) {
        parse(pluginFile, pluginJarInfo, null);
    }

    /**
     * 解析插件文件，补完 pluginJarInfo 属性
     */
    public static void parse(File pluginFile, PluginJarInfo pluginJarInfo, ClassLoader classLoader) {

        if (!pluginFile.exists()) {
            log.error("插件 {} 文件不存在: {}", pluginJarInfo.getName(), pluginFile.getAbsolutePath());
            return;
        }

        try (JarFile jarFile = new JarFile(pluginFile)) {

            JarEntry entry = jarFile.getJarEntry(ENTRY_NAME);
            if (entry == null) {
                return;
            }

            Element root = createRootElement(jarFile, entry);
            List<Element> menuElements = selectElements(root, "/root/ToolFxmlLoaderConfiguration[@isMenu='true']");
            Element pluginElement = selectSingleElement(root, "/root/ToolFxmlLoaderConfiguration[not(@isMenu)]");

            Map<String, String> menuTitles = new HashMap<>();
            for (Element menuElement : menuElements) {
                menuTitles.put(menuElement.attributeValue("menuId"), menuElement.attributeValue("title"));
            }

            String resourceBundleName = getChildNodeText(pluginElement, "resourceBundleName");
            ClassLoader tmpClassLoader = classLoader == null ? new PluginClassLoader(pluginFile) : classLoader;
            ResourceBundle pluginResourceBundle =
                ResourceBundle.getBundle(resourceBundleName, Config.defaultLocale, tmpClassLoader);

            String menuId = getChildNodeText(pluginElement, "menuParentId");
            String url = getChildNodeText(pluginElement, "url");
            String controllerType = getChildNodeText(pluginElement, "controllerType");
            String title = pluginResourceBundle.getString(
                defaultString(getChildNodeText(pluginElement, "title"), "Title")
            );
            String menuTitle = menuTitles.get(menuId);

            pluginJarInfo.setMenuParentTitle(menuTitle);
            pluginJarInfo.setBundleName(resourceBundleName);
            pluginJarInfo.setFxmlPath(url);
            pluginJarInfo.setControllerType(controllerType);
            pluginJarInfo.setTitle(title);

            pluginJarInfo.setName(StringUtils.defaultString(pluginJarInfo.getName(), title));

        } catch (IOException | DocumentException e) {
            throw new AppException(e);
        }
    }

    private static String getChildNodeText(Element element, String childNode) {
        return element.selectSingleNode("child::" + childNode).getText();
    }

    private static Element createRootElement(JarFile jarFile, JarEntry entry) throws IOException, DocumentException {
        InputStream input = jarFile.getInputStream(entry);
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(input);
        return document.getRootElement();
    }

    private static List<Element> selectElements(Element root, String xpath) {
        return root.selectNodes(xpath).stream()
            .filter(n -> n instanceof Element).map(n -> (Element) n)
            .collect(Collectors.toList());
    }

    private static Element selectSingleElement(Element root, String xpath) {
        return root.selectNodes(xpath).stream()
            .filter(n -> n instanceof Element).map(n -> (Element) n)
            .findFirst().orElse(null);
    }
}
