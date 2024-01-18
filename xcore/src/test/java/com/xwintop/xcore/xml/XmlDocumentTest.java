package com.xwintop.xcore.xml;

import org.dom4j.Element;
import org.junit.jupiter.api.Test;

import java.io.File;

public class XmlDocumentTest {

    @Test
    public void testSetElement() throws Exception {
        File xmlFile = new File("src/test/resources/xml-document-test.xml");
        XmlDocument xmlDocument = XmlDocument.readFile(xmlFile);

        System.out.println("修改前文件内容：");
        System.out.println(xmlDocument.toXmlString());

        Element encoder = xmlDocument.selectSingleElement("/configuration/appender/encoder");

        xmlDocument.addOrReplaceChildElement(encoder, "charset", "UNKNOWN_CHARSET");
        xmlDocument.save("target/xml-document-test-set-replaced.xml");

        System.out.println();
        System.out.println("修改后文件内容：");
        System.out.println(xmlDocument.toXmlString());
    }
}
