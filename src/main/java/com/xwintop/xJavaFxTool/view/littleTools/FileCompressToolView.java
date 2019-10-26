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
 * @ClassName: FileCompressToolView
 * @Description: 文件解压缩工具
 * @author: xufeng
 * @date: 2019/10/26 0026 19:17
 */

@Getter
@Setter
public abstract class FileCompressToolView implements Initializable {
    @FXML
    protected TextField selectFileTextField;
    @FXML
    protected Button selectFileButton;
    @FXML
    protected Button selectFolderButton;
    @FXML
    protected CheckBox compressCheckBox;
    @FXML
    protected Button compressButton;
    @FXML
    protected TextField saveFilePathTextField;
    @FXML
    protected Button saveFilePathButton;
    @FXML
    protected ChoiceBox fileTypeChoiceBox;

}