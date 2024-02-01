package com.xwintop.xJavaFxTool.services.javaFxTools;

import com.xwintop.xJavaFxTool.controller.javaFxTools.JavaFxXmlToObjectCodeController;
import com.xwintop.xJavaFxTool.utils.PluginProgectPomBuildTool;
import com.xwintop.xcore.util.StrUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import java.io.File;
import java.util.List;

/**
 * @ClassName: JavaFxXmlToObjectCodeService
 * @Description: JavaFxXml生成代码工具类
 * @author: xufeng
 * @date: 2017年11月10日 下午5:41:35
 */

@Getter
@Setter
@Slf4j
public class JavaFxXmlToObjectCodeService {
    private JavaFxXmlToObjectCodeController javaFxXmlToObjectCodeController;

    public JavaFxXmlToObjectCodeService(JavaFxXmlToObjectCodeController javaFxXmlToObjectCodeController) {
        this.javaFxXmlToObjectCodeController = javaFxXmlToObjectCodeController;
    }

    //将转换后代码存放至文件中
    public void buildCodeFileAction() throws Exception {
        if (StringUtils.isEmpty(javaFxXmlToObjectCodeController.getTextArea1().getText())) {
            TooltipUtil.showToast("请输入fxml内容！");
            return;
        }
        String[] codeStrings = xmlToCode(javaFxXmlToObjectCodeController.getTextArea1().getText());
        String codeFileOutputPath = StringUtils.defaultIfEmpty(javaFxXmlToObjectCodeController.getCodeFileOutputPathTextField().getText(), "./executor");
        File controllerFile = new File(codeFileOutputPath, codeStrings[3] + "Controller.java");
        FileUtils.writeStringToFile(controllerFile, codeStrings[0], "utf-8");
        File viewFile = new File(codeFileOutputPath, codeStrings[3] + "View.java");
        FileUtils.writeStringToFile(viewFile, codeStrings[1], "utf-8");
        File serviceFile = new File(codeFileOutputPath, codeStrings[3] + "Service.java");
        FileUtils.writeStringToFile(serviceFile, codeStrings[2], "utf-8");
        TooltipUtil.showToast("生成代码成功，存放在：" + controllerFile.getAbsolutePath());
    }

    //将转换后代码加上插件结构存放至文件中
    public void buildPluginProjectAction() throws Exception {
        if (StringUtils.isEmpty(javaFxXmlToObjectCodeController.getTextArea1().getText())) {
            TooltipUtil.showToast("请输入fxml内容！");
            return;
        }
        String[] codeStrings = xmlToCode(javaFxXmlToObjectCodeController.getTextArea1().getText());
        String codeFileOutputPath = StringUtils.defaultIfEmpty(javaFxXmlToObjectCodeController.getCodeFileOutputPathTextField().getText(), "./executor");
        String projectTypeName = StringUtils.defaultIfEmpty(javaFxXmlToObjectCodeController.getProjectTypeNameTextField().getText(), "javaFxTools");
        codeFileOutputPath = StringUtils.appendIfMissing(codeFileOutputPath, "/", "/", "\\");
        String pluginProgectName = StringUtils.defaultIfEmpty(javaFxXmlToObjectCodeController.getProjectNameTextField().getText(), "testJavaFx");
        if (codeStrings != null) {
            pluginProgectName = codeStrings[3];
            projectTypeName = codeStrings[4];
        }
        String pluginProgectPath = codeFileOutputPath + "x-" + pluginProgectName;
        File pluginProgectNameFile = new File(pluginProgectPath);
        if (pluginProgectNameFile.exists()) {
            boolean isTrue = AlertUtil.showConfirmAlert("该项目已经存在,是否覆盖代码？");
            if (!isTrue) {
                return;
            }
        }
        pluginProgectNameFile.mkdirs();
        String resources = "resources/com/xwintop/xJavaFxTool/";
//        String xmlPath = "javaFxTools/";
        String srcPath = "java/com/xwintop/xJavaFxTool/";
        pluginProgectPath = pluginProgectPath + "/src/main/";

        if (StringUtils.isNotEmpty(javaFxXmlToObjectCodeController.getTextArea1().getText())) {
            File controllerFile = new File(pluginProgectPath + srcPath + "controller/" + projectTypeName, codeStrings[3] + "Controller.java");
            FileUtils.writeStringToFile(controllerFile, codeStrings[0], "utf-8");
            File viewFile = new File(pluginProgectPath + srcPath + "view/" + projectTypeName, codeStrings[3] + "View.java");
            FileUtils.writeStringToFile(viewFile, codeStrings[1], "utf-8");
            File serviceFile = new File(pluginProgectPath + srcPath + "services/" + projectTypeName, codeStrings[3] + "Service.java");
            FileUtils.writeStringToFile(serviceFile, codeStrings[2], "utf-8");
            File fxmlFile = new File(pluginProgectPath + resources + "fxmlView/" + projectTypeName, codeStrings[3] + ".fxml");
            FileUtils.writeStringToFile(fxmlFile, javaFxXmlToObjectCodeController.getTextArea1().getText(), "utf-8");
            TooltipUtil.showToast("生成代码成功，存放在：" + controllerFile.getAbsolutePath());
        }

        FileUtils.writeStringToFile(new File(pluginProgectNameFile, "pom.xml"), PluginProgectPomBuildTool.getPom_xml(pluginProgectName), "utf-8");
        FileUtils.writeStringToFile(new File(pluginProgectNameFile, "README.md"), pluginProgectName, "utf-8");
        FileUtils.writeStringToFile(new File(pluginProgectNameFile, "README_EN.md"), pluginProgectName, "utf-8");
        FileUtils.writeStringToFile(new File(pluginProgectNameFile, ".gitignore"), PluginProgectPomBuildTool.getGitignore(pluginProgectName), "utf-8");
        FileUtils.writeStringToFile(new File(pluginProgectPath + srcPath + pluginProgectName + "Main.java"), PluginProgectPomBuildTool.getMain_java(pluginProgectName, projectTypeName), "utf-8");
        new File(pluginProgectPath + srcPath + "job").mkdirs();
        new File(pluginProgectPath + srcPath + "manager").mkdirs();
        new File(pluginProgectPath + srcPath + "model").mkdirs();
        new File(pluginProgectPath + srcPath + "utils").mkdirs();

        FileUtils.writeStringToFile(new File(pluginProgectPath + "resources/config/toolFxmlLoaderConfiguration.xml"), PluginProgectPomBuildTool.getToolFxmlLoaderConfiguration_xml(pluginProgectName, projectTypeName), "utf-8");
        FileUtils.writeStringToFile(new File(pluginProgectPath + "resources/locale/" + pluginProgectName + ".properties"), PluginProgectPomBuildTool.getLocal_properties(pluginProgectName), "utf-8");
        FileUtils.writeStringToFile(new File(pluginProgectPath + "resources/locale/" + pluginProgectName + "_en_US.properties"), PluginProgectPomBuildTool.getLocal_en_US_properties(pluginProgectName), "utf-8");

    }

    //将fxml文件转换为java代码
    public String[] xmlToCode(String xmlStr) throws Exception {
        StringBuilder attrStrBuilder = new StringBuilder();// 创建属性值获取
        StringBuilder funStrBuilder = new StringBuilder();// 创建方法值获取
        StringBuilder classNameStrBuilder = new StringBuilder();// 创建类值获取
        Document document = DocumentHelper.parseText(xmlStr);
        Element root = document.getRootElement();
        getCodeByElement(root, attrStrBuilder, funStrBuilder, classNameStrBuilder);

        String[] packageString = classNameStrBuilder.toString().split("controller");

        String[] packageStringSplit = packageString[1].split("\\.");
        String classNameString = packageStringSplit[packageStringSplit.length - 1].split("Controller")[0];
        String classNameStringLoCase = StrUtil.firstToLoCase(classNameString);
        String viewPackage = packageString[1].substring(0, packageString[1].lastIndexOf("."));

        StringBuilder controllerClassStrBuilder = new StringBuilder();//控制层类字符串
        controllerClassStrBuilder.append("package com.xwintop.xJavaFxTool.controller" + viewPackage + ";\n");
        controllerClassStrBuilder.append("import com.xwintop.xJavaFxTool.view" + viewPackage + "." + classNameString + "View;\n");
        controllerClassStrBuilder.append("import com.xwintop.xJavaFxTool.services" + viewPackage + "." + classNameString + "Service;\n");
        controllerClassStrBuilder.append("import lombok.Getter;\n");
        controllerClassStrBuilder.append("import lombok.Setter;\n");
        controllerClassStrBuilder.append("import lombok.extern.slf4j.Slf4j;\n");
        controllerClassStrBuilder.append("import java.net.URL;\n");
        controllerClassStrBuilder.append("import java.util.ResourceBundle;\n");
        controllerClassStrBuilder.append("import javafx.event.ActionEvent;\n");
        controllerClassStrBuilder.append("import javafx.fxml.FXML;\n");
        controllerClassStrBuilder.append("@Getter\n@Setter\n@Slf4j\n");
        controllerClassStrBuilder.append("public class " + classNameString + "Controller extends " + classNameString + "View {\n");
        controllerClassStrBuilder.append("private " + classNameString + "Service " + classNameStringLoCase + "Service = new " + classNameString + "Service(this);\n");
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
        classStrBuilder.append("package com.xwintop.xJavaFxTool.view" + viewPackage + ";\n");
        classStrBuilder.append("import lombok.Getter;\n");
        classStrBuilder.append("import lombok.Setter;\n");
        classStrBuilder.append("import javafx.fxml.Initializable;\n");
        classStrBuilder.append("import javafx.fxml.FXML;\n");
        List<Node> importList = document.content();
        for (Node node : importList) {
            if ("import".equals(node.getName())) {
                classStrBuilder.append("import " + node.getText() + ";\n");
            }
        }
        classStrBuilder.append("@Getter\n@Setter\n");
        classStrBuilder.append("public abstract class " + classNameString + "View implements Initializable {\n");
        classStrBuilder.append(attrStrBuilder.toString());
        classStrBuilder.append("\n}");

        StringBuilder serviceClassStrBuilder = new StringBuilder();//控制层类字符串
        serviceClassStrBuilder.append("package com.xwintop.xJavaFxTool.services" + viewPackage + ";\n");
        serviceClassStrBuilder.append("import com.xwintop.xJavaFxTool.controller" + viewPackage + "." + classNameString + "Controller;\n");
        serviceClassStrBuilder.append("import lombok.Getter;\n");
        serviceClassStrBuilder.append("import lombok.Setter;\n");
        serviceClassStrBuilder.append("import lombok.extern.slf4j.Slf4j;\n");
        serviceClassStrBuilder.append("@Getter\n@Setter\n@Slf4j\n");
        serviceClassStrBuilder.append("public class " + classNameString + "Service{\n");
        serviceClassStrBuilder.append("private " + classNameString + "Controller " + classNameStringLoCase + "Controller;\n");
        serviceClassStrBuilder.append("public " + classNameString + "Service(" + classNameString + "Controller " + classNameStringLoCase + "Controller) {\n");
        serviceClassStrBuilder.append("this." + classNameStringLoCase + "Controller = " + classNameStringLoCase + "Controller;\n}\n");
        serviceClassStrBuilder.append("}");

        return new String[]{controllerClassStrBuilder.toString(), classStrBuilder.toString(), serviceClassStrBuilder.toString(), classNameString, viewPackage.substring(1)};
    }

    private void getCodeByElement(Element root, StringBuilder attrStrBuilder, StringBuilder funStrBuilder, StringBuilder classNameStrBuilder) {
        List<Attribute> rootAttrList = root.attributes();
        for (Attribute rootAttr : rootAttrList) {
            if ("id".equals(rootAttr.getName())) {
//				@FXML
//				private TextField textField1;
                attrStrBuilder.append("@FXML\nprotected ");
                attrStrBuilder.append(root.getName()).append(" ");
                attrStrBuilder.append(rootAttr.getValue()).append(";\n");
            } else if ("onAction".equals(rootAttr.getName())) {
//				@FXML
//				private void xmlToSql(ActionEvent event){}
                funStrBuilder.append("\n@FXML\nprivate void ");
                funStrBuilder.append(rootAttr.getValue().substring(1)).append("(ActionEvent event){\n}\n");
            } else if ("controller".equals(rootAttr.getName())) {
                classNameStrBuilder.append(rootAttr.getValue());
            }
        }
        List<Element> rootElementList = root.elements();
        for (Element rootElement : rootElementList) {
            getCodeByElement(rootElement, attrStrBuilder, funStrBuilder, classNameStrBuilder);
        }
    }
}
