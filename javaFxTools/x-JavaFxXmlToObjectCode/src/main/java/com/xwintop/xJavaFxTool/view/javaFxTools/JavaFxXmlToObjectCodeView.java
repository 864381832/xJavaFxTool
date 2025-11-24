package com.xwintop.xJavaFxTool.view.javaFxTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: JavaFxXmlToObjectCodeView
 * @Description: javafx代码生成工具
 * @author: xufeng
 * @date: 2020/4/9 15:03
 */

@Getter
@Setter
public abstract class JavaFxXmlToObjectCodeView implements Initializable {
    @FXML
    protected TextArea textArea1;
    @FXML
    protected TextArea textArea2;
    @FXML
    protected TextArea textArea3;
    @FXML
    protected TextArea textArea4;
    @FXML
    protected Button xmlToCodeButton;
    @FXML
    protected Button buildCodeFileButton;
    @FXML
    protected Button buildPluginProjectButton;
    @FXML
    protected TextField projectTypeNameTextField;
    @FXML
    protected TextField projectNameTextField;
    @FXML
    protected TextField codeFileOutputPathTextField;
    @FXML
    protected Button selectCodeFileOutputPathButton;
}
