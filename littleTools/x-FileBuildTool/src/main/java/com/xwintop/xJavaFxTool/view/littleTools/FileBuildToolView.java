package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName: FileBuildToolView
 * @Description: 文件生成工具
 * @author: xufeng
 * @date: 2020/4/18 0018 18:05
 */

@Getter
@Setter
public abstract class FileBuildToolView implements Initializable {
    @FXML
    protected CheckBox isShowCheckBox;
    @FXML
    protected Button buildMoreFileButton;
    @FXML
    protected Button buildOneFileButton;
    @FXML
    protected TextField fileNameTextField;
    @FXML
    protected TextField oneFileSpaceTextField;
    @FXML
    protected TextField outputFilePathTextField;
    @FXML
    protected Button outputFilePathButton;
    @FXML
    protected TextArea fileTemplateTextArea;
    @FXML
    protected TableView<Map<String, String>> fieldTableView;
}
