package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public abstract class ZHConverterView implements Initializable {
	@FXML
	protected ChoiceBox<String> codeTypesChoiceBox;
	@FXML
	protected TextArea simplifiedTextArea;
	@FXML
	protected TextArea traditionalTextArea;
	@FXML
	protected Button changeButton;
	@FXML
	protected Button restoreButton;
	@FXML
	protected TextField pinyinTypeTextField;
	@FXML
	protected ChoiceBox<String> pinyinTypeChoiceBox;

}