package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Getter
@Setter
public abstract class ElementaryArithmeticProblemToolView implements Initializable {
    @FXML
    protected RadioButton maxNumber1RadioButton;
    @FXML
    protected ToggleGroup maxNumberToggleGroup;
    @FXML
    protected RadioButton maxNumber2RadioButton;
    @FXML
    protected RadioButton maxNumber3RadioButton;
    @FXML
    protected Spinner<Integer> maxNumberSpinner;
    @FXML
    protected CheckBox fushuCheckBox;
    @FXML
    protected CheckBox fuHao1CheckBox;
    @FXML
    protected CheckBox fuHao2CheckBox;
    @FXML
    protected CheckBox fuHao3CheckBox;
    @FXML
    protected CheckBox fuHao4CheckBox;
    @FXML
    protected CheckBox fuHao5CheckBox;
    @FXML
    protected Spinner<Integer> buildNumberSpinner1;
    @FXML
    protected Spinner<Integer> buildNumberSpinner2;
    @FXML
    protected RadioButton buildTypeRadioButton1;
    @FXML
    protected ToggleGroup daAnToggleGroup;
    @FXML
    protected RadioButton buildTypeRadioButton2;
    @FXML
    protected RadioButton buildTypeRadioButton3;
    @FXML
    protected TextArea jieguoTextArea;

}