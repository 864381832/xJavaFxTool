package com.xwintop.xJavaFxTool.view.littleTools;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public abstract class TimeToolView implements Initializable {
    @FXML
    protected TextField textFileldTimeStr;
    @FXML
    protected JFXComboBox<String> choiceBoxTimeZone;
    @FXML
    protected Button buttonCopy;
    @FXML
    protected Button buttonConvert;
    @FXML
    protected TextField textFileldTimeStr2;
    @FXML
    protected JFXComboBox<String> choiceBoxTimeFormatter;
    @FXML
    protected Button buttonCopy2;
    @FXML
    protected Button buttonRevert;
    @FXML
    protected TextField startTimeTextField;
    @FXML
    protected TextField endTimeTextField;
    @FXML
    protected Button calculatePoorButton;
    @FXML
    protected TextField startTime2TextField;
    @FXML
    protected TextField addTimeTextField;
    @FXML
    protected ChoiceBox<String> addTimeChoiceBox;
    @FXML
    protected Button addTimeButton;
    @FXML
    protected TextArea textAreaResult;

}