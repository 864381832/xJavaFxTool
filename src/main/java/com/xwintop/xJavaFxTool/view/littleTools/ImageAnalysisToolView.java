package com.xwintop.xJavaFxTool.view.littleTools;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXComboBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @ClassName: ImageAnalysisToolView
 * @Description: 图片解析工具View
 * @author: xufeng
 * @date: 2018/6/25 13:36
 */

@Getter
@Setter
public abstract class ImageAnalysisToolView implements Initializable {
    @FXML
    protected TextField atlasPathTextField;
    @FXML
    protected Button atlasPathButton;
    @FXML
    protected TextField imagePathTextField;
    @FXML
    protected Button imagePathButton;
    @FXML
    protected TextField outputPathTextField;
    @FXML
    protected Button outputPathButton;
    @FXML
    protected Button analysisAtlasButton;
    @FXML
    protected JFXComboBox<String> analysisOrientationComboBox;
    @FXML
    protected JFXComboBox<Integer> analysisNumberComboBox;
    @FXML
    protected Button analysisImageButton;

}