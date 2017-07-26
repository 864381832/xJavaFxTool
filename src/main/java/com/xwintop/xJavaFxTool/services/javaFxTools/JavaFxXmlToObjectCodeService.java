package com.xwintop.xJavaFxTool.services.javaFxTools;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import com.xwintop.xJavaFxTool.services.epmsTools.XmlToSqlService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class JavaFxXmlToObjectCodeService {
	public static String xmlToCode(String xmlStr) throws Exception {
		StringBuilder attrStrBuilder = new StringBuilder();// 创建属性值获取
		StringBuilder funStrBuilder = new StringBuilder();// 创建方法值获取
		StringBuilder classNameStrBuilder = new StringBuilder();// 创建类值获取
		Document document = DocumentHelper.parseText(xmlStr);
		Element root = document.getRootElement();
		getCodeByElement(root,attrStrBuilder,funStrBuilder,classNameStrBuilder);

		StringBuilder classStrBuilder = new StringBuilder();
		classStrBuilder.append("import javafx.fxml.Initializable;\n");
		classStrBuilder.append("import java.net.URL;\n");
		classStrBuilder.append("import java.util.ResourceBundle;\n");
		classStrBuilder.append("import javafx.event.ActionEvent;\n");
		classStrBuilder.append("import javafx.scene.control.Button;\n");
		classStrBuilder.append("import javafx.scene.control.TextField;\n");
		classStrBuilder.append("import javafx.fxml.FXML;\n");
		classStrBuilder.append("import javafx.scene.control.TextArea;\n");
		classStrBuilder.append(classNameStrBuilder.toString());
		classStrBuilder.append(attrStrBuilder.toString());
//		@Override
//		public void initialize(URL location, ResourceBundle resources) {
//		}
		classStrBuilder.append("\n@Override\npublic void initialize(URL location, ResourceBundle resources) {\n}\n");
		classStrBuilder.append(funStrBuilder.toString());
		classStrBuilder.append("\n}");
		
		return classStrBuilder.toString();
	}

	private static void getCodeByElement(Element root,StringBuilder attrStrBuilder,StringBuilder funStrBuilder,StringBuilder classNameStrBuilder) {
		List<DefaultAttribute> rootAttrList = root.attributes();
		for (DefaultAttribute rootAttr : rootAttrList) {
			if("id".equals(rootAttr.getName())){
//				@FXML
//				private TextField textField1;
				attrStrBuilder.append("@FXML\nprivate ");
				attrStrBuilder.append(root.getName()).append(" ");
				attrStrBuilder.append(rootAttr.getValue()).append(";\n");
			}else if("onAction".equals(rootAttr.getName())){
//				@FXML
//				private void xmlToSql(ActionEvent event){
//				}
				funStrBuilder.append("\n@FXML\nprivate void ");
				funStrBuilder.append(rootAttr.getValue().substring(1)).append("(ActionEvent event){\n}\n");
			}else if("controller".equals(rootAttr.getName())){
//				public class GeneratingCodeController extends AnchorPane implements Initializable {
				classNameStrBuilder.append("public class ");
				String[] rootAttrStrings = rootAttr.getValue().split("\\.");
				classNameStrBuilder.append(rootAttrStrings[rootAttrStrings.length-1]).append(" implements Initializable {\n");
			}
		}
		List<Element> rootElementList = root.elements();
		for (Element rootElement : rootElementList) {
			getCodeByElement(rootElement,attrStrBuilder,funStrBuilder,classNameStrBuilder);
		}
	}
}
