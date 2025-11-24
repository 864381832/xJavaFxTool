package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.JavaServiceController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName: JavaServiceService
 * @Description: java服务安装工具
 * @author: xufeng
 * @date: 2020/9/22 13:14
 */

@Getter
@Setter
@Slf4j
public class JavaServiceService {
    private JavaServiceController javaServiceController;

    public JavaServiceService(JavaServiceController javaServiceController) {
        this.javaServiceController = javaServiceController;
    }

    public void installAction() throws Exception {
        File winSwFile = new File(javaServiceController.getJarPathTextField().getText(), "WinSW.NET4.exe");
        if (!winSwFile.exists()) {
            Path path = Paths.get(Object.class.getResource("/data/WinSW/WinSW.NET4.exe").toURI());
            FileUtils.copyFile(path.toFile(), winSwFile);
        }
        //生成配置文件
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("configuration");
        document.setRootElement(rootElement);
        rootElement.addElement("id").addText(javaServiceController.getIdTextField().getText());
        rootElement.addElement("name").addText(javaServiceController.getNameTextField().getText());
        rootElement.addElement("description").addText(javaServiceController.getDescriptionTextField().getText());
        rootElement.addElement("executable").addText(javaServiceController.getExecutableTextField().getText());
        rootElement.addElement("arguments").addText(javaServiceController.getArgumentsTextField().getText());
        rootElement.addElement("startmode").addText(javaServiceController.getStartmodeTextField().getText());
        rootElement.addElement("logpath").addText(javaServiceController.getLogpathTextField().getText());
        rootElement.addElement("logmode").addText(javaServiceController.getLogmodeTextField().getText());
        OutputFormat formater = OutputFormat.createPrettyPrint();
        formater.setSuppressDeclaration(true);
        formater.setNewLineAfterDeclaration(false);
        XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(new File(javaServiceController.getJarPathTextField().getText(), "WinSW.NET4.xml")), formater);
        xmlWriter.write(document);
        xmlWriter.close();

        //安装服务
        execWinsw(" install", "安装成功");
    }

    public void startAction() throws Exception {
        execWinsw(" start", "启动成功");
    }

    public void restartAction() throws Exception {
        execWinsw(" restart", "重启成功");
    }

    public void stopAction() throws Exception {
        execWinsw(" stop", "停止成功");
    }

    public void uninstallAction() throws Exception {
        execWinsw(" uninstall", "卸载成功");
    }

    private void execWinsw(String command, String showMessage) throws Exception {
        File winSwFile = new File(javaServiceController.getJarPathTextField().getText(), "WinSW.NET4.exe");
        Process process = Runtime.getRuntime().exec(winSwFile.getAbsolutePath() + command);
        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue == 0) {
            TooltipUtil.showToast(showMessage);
        }
    }
}