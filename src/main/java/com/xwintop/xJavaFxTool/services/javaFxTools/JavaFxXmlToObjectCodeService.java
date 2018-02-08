package com.xwintop.xJavaFxTool.services.javaFxTools;

import com.xwintop.xcore.util.StrUtil;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultAttribute;

import java.util.List;

/** 
 * @ClassName: JavaFxXmlToObjectCodeService 
 * @Description: JavaFxXml生成代码工具类
 * @author: xufeng
 * @date: 2017年11月10日 下午5:41:35  
 */
@Slf4j
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
		String classNameStringLoCase = StrUtil.fristToLoCase(classNameString);
		String viewPackage = packageString[1].substring(0, packageString[1].lastIndexOf("."));
		
		StringBuilder controllerClassStrBuilder = new StringBuilder();//控制层类字符串
		controllerClassStrBuilder.append("package com.xwintop.xJavaFxTool.controller"+viewPackage+";\n");
		controllerClassStrBuilder.append("import com.xwintop.xJavaFxTool.view"+viewPackage+"."+classNameString+"View;\n");
		controllerClassStrBuilder.append("import com.xwintop.xJavaFxTool.services"+viewPackage+"."+classNameString+"Service;\n");
		controllerClassStrBuilder.append("import lombok.Getter;\n");
		controllerClassStrBuilder.append("import lombok.Setter;\n");
		controllerClassStrBuilder.append("import lombok.extern.slf4j.Slf4j;\n");
		controllerClassStrBuilder.append("import java.net.URL;\n");
		controllerClassStrBuilder.append("import java.util.ResourceBundle;\n");
		controllerClassStrBuilder.append("import javafx.event.ActionEvent;\n");
		controllerClassStrBuilder.append("import javafx.fxml.FXML;\n");
		controllerClassStrBuilder.append("@Getter\n@Setter\n@Slf4j\n");
		controllerClassStrBuilder.append("public class "+classNameString+"Controller extends "+classNameString+"View {\n");
		controllerClassStrBuilder.append("private "+classNameString+"Service "+classNameStringLoCase+"Service = new "+classNameString+"Service(this);\n");
//		@Override
//		public void initialize(URL location, ResourceBundle resources) {
//		}
		controllerClassStrBuilder.append("\n@Override\npublic void initialize(URL location, ResourceBundle resources) {\n");
		controllerClassStrBuilder.append("initView();\n");
		controllerClassStrBuilder.append("initEvent();\n");
		controllerClassStrBuilder.append("initService();\n");
		controllerClassStrBuilder.append("}");
		controllerClassStrBuilder.append("\n private void initView() {}");
		controllerClassStrBuilder.append("\n private void initEvent() {}");
		controllerClassStrBuilder.append("\n private void initService() {}");
		controllerClassStrBuilder.append(funStrBuilder.toString());
		controllerClassStrBuilder.append("}");
		
		
		StringBuilder classStrBuilder = new StringBuilder();//视图view类字符串
		classStrBuilder.append("package com.xwintop.xJavaFxTool.view"+viewPackage+";\n");
		classStrBuilder.append("import lombok.Getter;\n");
		classStrBuilder.append("import lombok.Setter;\n");
		classStrBuilder.append("import javafx.fxml.Initializable;\n");
		classStrBuilder.append("import javafx.fxml.FXML;\n");
		@SuppressWarnings("unchecked")
		List<Node> importList = document.content();
		for(Node node:importList) {
			if("import".equals(node.getName())) {
				classStrBuilder.append("import "+node.getText()+";\n");
			}
		}
		classStrBuilder.append("@Getter\n@Setter\n");
		classStrBuilder.append("public abstract class "+classNameString+"View implements Initializable {\n");
		classStrBuilder.append(attrStrBuilder.toString());
		classStrBuilder.append("\n}");
		
		StringBuilder serviceClassStrBuilder = new StringBuilder();//控制层类字符串
		serviceClassStrBuilder.append("package com.xwintop.xJavaFxTool.services"+viewPackage+";\n");
		serviceClassStrBuilder.append("import com.xwintop.xJavaFxTool.controller"+viewPackage+"."+classNameString+"Controller;\n");
		serviceClassStrBuilder.append("import lombok.Getter;\n");
		serviceClassStrBuilder.append("import lombok.Setter;\n");
		serviceClassStrBuilder.append("import lombok.extern.slf4j.Slf4j;\n");
		serviceClassStrBuilder.append("@Getter\n@Setter\n@Slf4j\n");
		serviceClassStrBuilder.append("public class "+classNameString+"Service{\n");
		serviceClassStrBuilder.append("private "+classNameString+"Controller "+classNameStringLoCase+"Controller;\n");
		serviceClassStrBuilder.append("public "+classNameString+"Service("+classNameString+"Controller "+classNameStringLoCase+"Controller) {\n");
		serviceClassStrBuilder.append("this."+classNameStringLoCase+"Controller = "+classNameStringLoCase+"Controller;\n}\n");
		serviceClassStrBuilder.append("}");
		
		return new String[]{controllerClassStrBuilder.toString(),classStrBuilder.toString(),serviceClassStrBuilder.toString()};
	}

	@SuppressWarnings("unchecked")
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
//				private void xmlToSql(ActionEvent event){}
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
