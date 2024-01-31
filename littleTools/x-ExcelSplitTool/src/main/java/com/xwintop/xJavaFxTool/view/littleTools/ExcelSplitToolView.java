package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: ExcelSplitToolView
 * @Description: Excel拆分工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:19
 */

@Getter
@Setter
public abstract class ExcelSplitToolView implements Initializable {
    @FXML
    protected TextField selectFileTextField;
    @FXML
    protected TextField sheetSelectTextField;
    @FXML
    protected ChoiceBox<String> fileTypeChoiceBox;
    @FXML
    protected Button selectFileButton;
    @FXML
    protected CheckBox includeHandCheckBox;
    @FXML
    protected RadioButton splitType1RadioButton;
    @FXML
    protected ToggleGroup splitTypeToggleGroup;
    @FXML
    protected Spinner<Integer> splitType1Spinner;
    @FXML
    protected RadioButton splitType2RadioButton;
    @FXML
    protected Spinner<Integer> splitType2Spinner;
    @FXML
    protected RadioButton outputType1RadioButton;
    @FXML
    protected ToggleGroup outputTypeToggleGroup;
    @FXML
    protected RadioButton outputType2RadioButton;
    @FXML
    protected Button splitButton;
    @FXML
    protected RadioButton splitType3RadioButton;
    @FXML
    protected TextField splitType3TextField;
    @FXML
    protected TextField saveFilePathTextField;
    @FXML
    protected Button saveFilePathButton;
}