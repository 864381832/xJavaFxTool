package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public abstract class EscapeCharacterView implements Initializable {
	@FXML
	protected ChoiceBox<String> characterTypesChoiceBox;
	@FXML
	protected Button switchButton;
	@FXML
	protected Button restoreButton;
	@FXML
	protected Button cleanAllButton;
	@FXML
	protected TextArea unescapeTextArea;
	@FXML
	protected TextArea escapeTextArea;
	@FXML
	protected TextArea helpTextArea;

}