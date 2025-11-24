package com.xwintop.xJavaFxTool.view.assistTools;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;
import lombok.Setter;
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
    protected TextArea textTextArea;
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