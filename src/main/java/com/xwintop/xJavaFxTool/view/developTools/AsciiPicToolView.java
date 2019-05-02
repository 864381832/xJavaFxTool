package com.xwintop.xJavaFxTool.view.developTools;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

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
    protected Button buildBannerButton;
    @FXML
    protected Button buildBase4Button;
    @FXML
    protected JFXComboBox<String> imageSizeComboBox;
    @FXML
    protected ImageView imageImageView;
    @FXML
    protected Button saveImageButton;
    @FXML
    protected TextArea codeTextArea;
    @FXML
    protected Button saveImageToExcelButton;
}