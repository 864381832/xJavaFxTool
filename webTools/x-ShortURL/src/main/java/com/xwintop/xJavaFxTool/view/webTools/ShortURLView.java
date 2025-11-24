package com.xwintop.xJavaFxTool.view.webTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public abstract class ShortURLView implements Initializable{
	@FXML
	protected AnchorPane mainAnchorPane;
	@FXML
	protected TextField longURLTextField;
	@FXML
	protected ChoiceBox<String> shortURLServiceChoiceBox;
	@FXML
	protected Button longURLCopyButton;
	@FXML
	protected Button convertButton;
	@FXML
	protected TextField shortURLTextField;
	@FXML
	protected Button shortURLCopyButton;
	@FXML
	protected Button revertButton;
	@FXML
	protected TextField aliasUrlTextField;
	@FXML
	protected CheckBox aliasUrlCheckBox;
	@FXML
	protected TextArea resultTextArea;
}
