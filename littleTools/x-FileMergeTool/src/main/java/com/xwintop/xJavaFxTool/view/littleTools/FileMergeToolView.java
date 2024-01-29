package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: FileMergeToolView
 * @Description: 文件合并工具
 * @author: xufeng
 * @date: 2019/6/11 17:16
 */

@Getter
@Setter
public abstract class FileMergeToolView implements Initializable {
    @FXML
    protected TextField selectFileTextField;
    @FXML
    protected Button selectFileButton;
    @FXML
    protected Button selectFolderButton;
    @FXML
    protected CheckBox includeHandCheckBox;
    @FXML
    protected Button mergeButton;
    @FXML
    protected TextField saveFilePathTextField;
    @FXML
    protected Button saveFilePathButton;
    @FXML
    protected TextField sheetSelectTextField;
    @FXML
    protected ChoiceBox<String> fileTypeChoiceBox;
}