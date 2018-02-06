package com.xwintop.xJavaFxTool.view.assistTools;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
/**
 * @ClassName: TextToSpeechToolView
 * @Description: 语音转换工具
 * @author: xufeng
 * @date: 2018/2/6 15:53
 */

@Getter
@Setter
public abstract class TextToSpeechToolView implements Initializable {
    @FXML
    protected JFXTextArea textTextArea;
    @FXML
    protected JFXRadioButton per0RadioButton;
    @FXML
    protected ToggleGroup perToggleGroup;
    @FXML
    protected JFXRadioButton per1RadioButton;
    @FXML
    protected JFXRadioButton per3RadioButton;
    @FXML
    protected JFXRadioButton per4RadioButton;
    @FXML
    protected JFXSlider spdSlider;
    @FXML
    protected JFXSlider volSlider;
    @FXML
    protected Button playButton;

}