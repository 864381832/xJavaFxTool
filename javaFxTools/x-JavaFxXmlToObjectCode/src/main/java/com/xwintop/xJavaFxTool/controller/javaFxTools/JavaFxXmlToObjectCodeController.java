package com.xwintop.xJavaFxTool.controller.javaFxTools;

import com.xwintop.xJavaFxTool.services.javaFxTools.JavaFxXmlToObjectCodeService;
import com.xwintop.xJavaFxTool.view.javaFxTools.JavaFxXmlToObjectCodeView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: JavaFxXmlToObjectCodeController
 * @Description: javaFx Xml转对象工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:24
 */

@Getter
@Setter
@Slf4j
public class JavaFxXmlToObjectCodeController extends JavaFxXmlToObjectCodeView {
    private JavaFxXmlToObjectCodeService javaFxXmlToObjectCodeService = new JavaFxXmlToObjectCodeService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(codeFileOutputPathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void xmlToCodeOnAction(ActionEvent event) throws Exception {
        if (StringUtils.isEmpty(textArea1.getText())) {
            TooltipUtil.showToast("请输入fxml内容！");
            return;
        }
        String[] string = javaFxXmlToObjectCodeService.xmlToCode(textArea1.getText());
        textArea2.setText(string[0]);
        textArea3.setText(string[1]);
        textArea4.setText(string[2]);
    }

    @FXML
    private void buildCodeFileAction(ActionEvent event) throws Exception {
        javaFxXmlToObjectCodeService.buildCodeFileAction();
    }

    @FXML
    private void showExampleCodeAction(ActionEvent event) throws Exception {
        URL url = Object.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/javaFxTools/JavaFxXmlToObjectCode.fxml");
        TextArea textArea = new TextArea(FileUtils.readFileToString(new File(url.toURI()), "utf-8"));
        textArea.setWrapText(true);
        JavaFxViewUtil.openNewWindow("JavaFxXmlToObjectCode.fxml", textArea);
    }

    @FXML
    private void buildPluginProjectAction(ActionEvent event) throws Exception {
        javaFxXmlToObjectCodeService.buildPluginProjectAction();
    }

    @FXML
    private void selectCodeFileOutputPathAction(ActionEvent event) throws Exception {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            codeFileOutputPathTextField.setText(file.getPath());
        }
    }
}
