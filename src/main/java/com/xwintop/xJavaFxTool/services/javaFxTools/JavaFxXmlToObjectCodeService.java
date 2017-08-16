package com.xwintop.xJavaFxTool.services.javaFxTools;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import com.xwintop.xJavaFxTool.view.webTools.ShortURLView;

public class JavaFxXmlToObjectCodeService {
	public static String[] xmlToCode(String xmlStr) throws Exception {
		StringBuilder attrStrBuilder = new StringBuilder();// 创建属性值获取
		StringBuilder funStrBuilder = new StringBuilder();// 创建方法值获取
		StringBuilder classNameStrBuilder = new StringBuilder();// 创建类值获取
		Document document = DocumentHelper.parseText(xmlStr);
		Element root = document.getRootElement();
		getCodeByElement(root,attrStrBuilder,funStrBuilder,classNameStrBuilder);

		String[] packageString = classNameStrBuilder.toString().split("controller");
		
		String[] packageStringSplit = packageString[1].split("\\.");
		String classNameString = packageStringSplit[packageStringSplit.length-1].split("Controller")[0];
		String viewPackage = packageString[1].substring(0, packageString[1].lastIndexOf("."));
		
		StringBuilder controllerClassStrBuilder = new StringBuilder();
		controllerClassStrBuilder.append("package com.xwintop.xJavaFxTool.controller"+viewPackage+";\n");
		controllerClassStrBuilder.append("import com.xwintop.xJavaFxTool.view"+viewPackage+"."+classNameString+"View;\n");
		controllerClassStrBuilder.append("import java.net.URL;\n");
		controllerClassStrBuilder.append("import java.util.ResourceBundle;\n");
		controllerClassStrBuilder.append("import javafx.event.ActionEvent;\n");
		controllerClassStrBuilder.append("import javafx.fxml.FXML;\n");
		controllerClassStrBuilder.append("public class "+classNameString+"Controller extends "+classNameString+"View {");
//		@Override
//		public void initialize(URL location, ResourceBundle resources) {
//		}
		controllerClassStrBuilder.append("\n@Override\npublic void initialize(URL location, ResourceBundle resources) {\n");
		controllerClassStrBuilder.append("initView();\n");
		controllerClassStrBuilder.append("initEvent();\n");
		controllerClassStrBuilder.append("}");
		controllerClassStrBuilder.append("\n private void initView() {}");
		controllerClassStrBuilder.append("\n private void initEvent() {}");
		controllerClassStrBuilder.append(funStrBuilder.toString());
		controllerClassStrBuilder.append("}");
		
		
		StringBuilder classStrBuilder = new StringBuilder();
		classStrBuilder.append("package com.xwintop.xJavaFxTool.view"+viewPackage+";\n");
		classStrBuilder.append("import javafx.fxml.Initializable;\n");
		classStrBuilder.append("import javafx.scene.control.Button;\n");
		classStrBuilder.append("import javafx.scene.control.CheckBox;\n");
		classStrBuilder.append("import javafx.scene.control.TextField;\n");
		classStrBuilder.append("import javafx.fxml.FXML;\n");
		classStrBuilder.append("import javafx.scene.control.TextArea;\n");
		classStrBuilder.append("import javafx.scene.control.ChoiceBox;\n");
		classStrBuilder.append("public abstract class "+classNameString+"View implements Initializable {\n");
		classStrBuilder.append(attrStrBuilder.toString());
		classStrBuilder.append("\n}");
		
		return new String[]{controllerClassStrBuilder.toString(),classStrBuilder.toString()};
	}

	private static void getCodeByElement(Element root,StringBuilder attrStrBuilder,StringBuilder funStrBuilder,StringBuilder classNameStrBuilder) {
		List<DefaultAttribute> rootAttrList = root.attributes();
		for (DefaultAttribute rootAttr : rootAttrList) {
			if("id".equals(rootAttr.getName())){
//				@FXML
//				private TextField textField1;
				attrStrBuilder.append("@FXML\nprotected ");
				attrStrBuilder.append(root.getName()).append(" ");
				attrStrBuilder.append(rootAttr.getValue()).append(";\n");
			}else if("onAction".equals(rootAttr.getName())){
//				@FXML
//				private void xmlToSql(ActionEvent event){
//				}
				funStrBuilder.append("\n@FXML\nprivate void ");
				funStrBuilder.append(rootAttr.getValue().substring(1)).append("(ActionEvent event){\n}\n");
			}else if("controller".equals(rootAttr.getName())){
				classNameStrBuilder.append(rootAttr.getValue());
			}
		}
		List<Element> rootElementList = root.elements();
		for (Element rootElement : rootElementList) {
			getCodeByElement(rootElement,attrStrBuilder,funStrBuilder,classNameStrBuilder);
		}
	}
}
