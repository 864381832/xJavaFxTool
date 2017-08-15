package com.xwintop.xJavaFxTool.view.littleTools;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public abstract class QRCodeBuilderView implements Initializable {
	@FXML
	protected Button builderButton;
	@FXML
	protected Button snapshotButton;
	@FXML
	protected TextField contentTextField;
	@FXML
	protected ChoiceBox<String> codeFormatChoiceBox;
	@FXML
	protected ImageView codeImageView;
	@FXML
	protected ImageView codeImageView1;
	@FXML
	protected ColorPicker onColorColorPicker;
	@FXML
	protected ColorPicker offColorColorPicker;
	@FXML
	protected ChoiceBox<ErrorCorrectionLevel> errorCorrectionLevelChoiceBox;
	@FXML
	protected Button saveButton;
	@FXML
	protected Button logoButton;
	@FXML
	protected ChoiceBox<Integer> marginChoiceBox;
	@FXML
	protected ChoiceBox<String> formatImageChoiceBox;
	@FXML
	protected CheckBox logoCheckBox;
	@FXML
	protected Slider logoSlider;
}
