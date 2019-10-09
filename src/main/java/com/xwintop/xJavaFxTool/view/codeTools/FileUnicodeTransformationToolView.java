package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;
/**
 * @ClassName: FileUnicodeTransformationToolView
 * @Description: 文件编码转换工具
 * @author: xufeng
 * @date: 2019/10/9 15:01
 */

@Getter
@Setter
public abstract class FileUnicodeTransformationToolView implements Initializable {
    @FXML
    protected TextField detectPathTextField;
    @FXML
    protected Button detectPathButton;
    @FXML
    protected CheckBox includeSubdirectoryCheckBox;
    @FXML
    protected Button transformationButton;
    @FXML
    protected ChoiceBox<String> showHideFileChoice;
    @FXML
    protected TextField fileNameContainsTextField;
    @FXML
    protected TextField fileNameNotContainsTextField;
    @FXML
    protected CheckBox fileNameSupportRegexCheckBox;
    @FXML
    protected ComboBox<String> oldFileUnicodeComboBox;
    @FXML
    protected ComboBox<String> newFileUnicodeComboBox;
    @FXML
    protected TextField newFilePathTextField;
    @FXML
    protected TextArea resultTextArea;

}