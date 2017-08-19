package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public abstract class ZHConverterView implements Initializable {
	@FXML
	protected ChoiceBox<String> codeTypesChoiceBox;
	@FXML
	protected JFXTextArea simplifiedTextArea;
	@FXML
	protected JFXTextArea traditionalTextArea;
	@FXML
	protected Button changeButton;
	@FXML
	protected Button restoreButton;
	@FXML
	protected JFXCheckBox pinyinTypeCheckBox;
	@FXML
	protected ChoiceBox<String> pinyinTypeChoiceBox;

}