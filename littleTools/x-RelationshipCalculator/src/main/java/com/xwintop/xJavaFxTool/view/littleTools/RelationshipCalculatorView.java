package com.xwintop.xJavaFxTool.view.littleTools;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

@Getter
@Setter
public abstract class RelationshipCalculatorView implements Initializable {
    @FXML
    protected ToggleGroup type;
    @FXML
    protected ToggleGroup sex;
    @FXML
    protected ToggleGroup methods;
    @FXML
    protected TextArea inputTextArea;
    @FXML
    protected Button fatherButton;
    @FXML
    protected Button montherButton;
    @FXML
    protected Button sonButton;
    @FXML
    protected Button daughterButton;
    @FXML
    protected Button bigBrotherButton;
    @FXML
    protected Button smallBrotherButton;
    @FXML
    protected Button bigSisterButton;
    @FXML
    protected Button smallSisterButton;
    @FXML
    protected Button husbandButton;
    @FXML
    protected Button wifeButton;
    @FXML
    protected Button count;
    @FXML
    protected Button countButton;
    @FXML
    protected Button reback;
    @FXML
    protected Button rebackButton;
    @FXML
    protected Button clear;
    @FXML
    protected Button clearButton;
    @FXML
    protected TextArea outputTextArea;

}