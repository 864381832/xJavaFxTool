package com.xwintop.xcore.xml;

import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * 对 dom4j 的常用操作做进一步封装
 */
public class XmlDocument {

    /**
     * 读取指定的文件，创建一个新的 XmlDocument 对象
     */
    public static XmlDocument readFile(File file) throws DocumentException {
        return new XmlDocument(file);
    }

    /**
     * 创建一个空的 XmlDocument 对象
     */
    public static XmlDocument newDocument() throws DocumentException {
        return new XmlDocument(null);
    }

    ///////////////////////////////////////////////////////////////////

    /**
     * 创建一个新的 XML 元素
     *
     * @param name    元素名称
     * @param content 元素内容
     */
    private static Element element(String name, String content) {
        Element element = DocumentFactory.getInstance().createElement(name);
        element.setText(content);
        return element;
    }

    //////////////////////////////////////////////////////////////

    private final File currentFile;

    private final Document document;

    private XmlDocument(File file) throws DocumentException {
        this.currentFile = file;
        this.document = new SAXReader().read(file);
    }

    /**
     * 获取实际的 Document 对象
     */
    public Document getDocument() {
        return document;
    }

    /**
     * 通过 XPATH 表达式在文档中查询符合的 Element 列表
     */
    public List<Element> selectElements(String xpath) {
        return this.document.selectNodes(xpath).stream()
            .filter(node -> node instanceof Element)
            .map(node -> (Element) node)
            .collect(Collectors.toList());
    }

    /**
     * 通过 XPATH 表达式在文档中查询符合的第一个 Element
     */
    public Element selectSingleElement(String xpath) {
        return this.document.selectNodes(xpath).stream()
            .filter(node -> node instanceof Element)
            .map(node -> (Element) node)
            .findFirst().orElse(null);
    }

    /**
     * 在指定元素中修改子元素内容，如果不存在则自动添加子元素
     *
     * @param parent       父元素
     * @param childName    子元素名称
     * @param childContent 子元素的（新）内容
     */
    public void addOrReplaceChildElement(Element parent, String childName, String childContent) {
        List<Node> nodes = parent.content();
        int found = -1;

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (Objects.equals(node.getName(), childName)) {
                found = i;
                break;
            }
        }

        Element element = element(childName, childContent);

        if (found > -1) {
            nodes.remove(found);
            nodes.add(found, element);
        } else {
            nodes.add(element);
        }
    }

    /**
     * 获取 XML 文档内容
     */
    public String toXmlString() {
        return document.asXML();
    }

    /**
     * 将 XML 文档回写到到当初读取的文件中
     */
    public void save() throws IOException {
        if (currentFile == null) {
            throw new IllegalStateException("No file to save.");
        }
        save(currentFile);
    }

    /**
     * 将 XML 文档写入到指定的文件中
     */
    public void save(File file) throws IOException {
        if (file != null) {
            Charset charset = Charset.forName(defaultString(document.getXMLEncoding(), "UTF-8"));
            FileUtils.writeStringToFile(file, document.asXML(), charset);
        }
    }

    /**
     * 将 XML 文档写入到指定的文件中
     */
    public void save(String filePath) throws IOException {
        save(new File(filePath));
    }
}
