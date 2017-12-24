package com.xwintop.xJavaFxTool.view.developTools;

import com.jfoenix.controls.JFXComboBox;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * @ClassName: AsciiPicToolView
 * @Description: 图片转ascii
 * @author: xufeng
 * @date: 2017/12/24 0024 23:16
 */

@Getter
@Setter
public abstract class AsciiPicToolView implements Initializable {
    @FXML
    protected TextField filePathTextField;
    @FXML
    protected Button filePathButton;
    @FXML
    protected Button buildButton;
    @FXML
    protected TextArea asciiTextArea;
    @FXML
    protected JFXComboBox<String> imageSizeComboBox;

}