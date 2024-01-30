package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: CharsetDetectToolView
 * @Description: 编码检测工具
 * @author: xufeng
 * @date: 2019/4/27 0027 18:14
 */

@Getter
@Setter
public abstract class CharsetDetectToolView implements Initializable {
    @FXML
    protected TextField detectPathTextField;
    @FXML
    protected CheckBox includeSubdirectoryCheckBox;
    @FXML
    protected Button detectPathButton;
    @FXML
    protected Button detectButton;
    @FXML
    protected TextField detectSizeTextField;
    @FXML
    protected TextField fileNameContainsTextField;
    @FXML
    protected TextField fileNameNotContainsTextField;
    @FXML
    protected CheckBox fileNameSupportRegexCheckBox;
    @FXML
    protected TextArea resultTextArea;

}