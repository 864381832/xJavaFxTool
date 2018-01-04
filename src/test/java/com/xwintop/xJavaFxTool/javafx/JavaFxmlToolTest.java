package com.xwintop.xJavaFxTool.javafx;

import com.xwintop.xJavaFxTool.services.javaFxTools.JavaFxXmlToObjectCodeService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

public class JavaFxmlToolTest {
    private String projectPath = "E:\\ideaWorkspaces\\xwintop\\xJavaFxTool\\src\\main\\";
    private String resources = projectPath + "resources\\com\\xwintop\\xJavaFxTool\\fxmlView\\";
    private String xmlPath = "codeTools\\ColorCodeConverterTool";
    private String srcPath = projectPath + "java\\com\\xwintop\\xJavaFxTool\\";
    @Test
    public void buildJava() throws Exception {
        projectPath = getClass().getResource("/").getPath().replace("target/test-classes","src/main");
        String xmlStr = FileUtils.readFileToString(new File(resources+xmlPath+".fxml"),"utf-8");
        String[] strings = JavaFxXmlToObjectCodeService.xmlToCode(xmlStr);
        FileUtils.writeStringToFile(new File(srcPath+"controller/"+xmlPath+"Controller.java"),strings[0],"utf-8");
        FileUtils.writeStringToFile(new File(srcPath+"view/"+xmlPath+"View.java"),strings[1],"utf-8");
        FileUtils.writeStringToFile(new File(srcPath+"services/"+xmlPath+"Service.java"),strings[2],"utf-8");
    }
}
