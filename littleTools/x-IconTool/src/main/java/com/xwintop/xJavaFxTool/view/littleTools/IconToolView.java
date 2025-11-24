package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import lombok.Data;
import net.coobird.thumbnailator.geometry.Positions;

@Data
public abstract class IconToolView implements Initializable {
	@FXML
	protected TextField iconFilePathTextField;
	@FXML
	protected Button chooseOriginalPathButton;
	@FXML
	protected TextField iconTargetPathTextField;
	@FXML
	protected Button chooseTargetPathButton;
	@FXML
	protected CheckBox isCornerCheckBox;
	@FXML
	protected Slider cornerSizeSlider;
	@FXML
	protected CheckBox isKeepAspectRatioCheckBox;
	@FXML
	protected Button buttonSaveConfigure;
	@FXML
	protected Button otherSaveConfigureButton;
	@FXML
	protected Button loadingConfigureButton;
	@FXML
	protected ChoiceBox<String> iconFormatChoiceBox;
	@FXML
	protected TextField iconNameTextField;
	@FXML
	protected CheckBox iosIconCheckBox;
	@FXML
	protected CheckBox androidCheckBox;
	@FXML
	protected Button buildIconButton;
	@FXML
	protected ImageView iconImageView;
	@FXML
	protected Button buildIconTargetImageButton;
	@FXML
	protected ImageView iconTargetImageView;
	@FXML
	protected Spinner<Integer> widthSpinner;
	@FXML
	protected Spinner<Integer> heightSpinner;
	@FXML
	protected Button addSizeButton;
	@FXML
	protected Button resettingSizeButton;
	@FXML
	protected Button allSelectButton;
	@FXML
	protected Button inverseButton;
	@FXML
	protected Button allNotSelectButton;
	@FXML
	protected FlowPane iconSizeFlowPane;
	@FXML
	protected CheckBox isWatermarkCheckBox;
	@FXML
	protected TextField watermarkPathTextField;
	@FXML
	protected Button selectWatermarkButton;
	@FXML
	protected ImageView watermarkImageView;
	@FXML
	protected ChoiceBox<Positions> watermarkPositionChoiceBox;
	@FXML
	protected Slider watermarkOpacitySlider;
	@FXML
	protected Slider outputQualitySlider;

}