package com.xwintop.xJavaFxTool.view.littleTools;

import com.jfoenix.controls.JFXSlider;
import com.xwintop.xJavaFxTool.utils.CorrectionLevel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

public abstract class QRCodeBuilderView implements Initializable {

    @FXML
    protected Button builderButton;

    @FXML
    protected Button snapshotButton;

    @FXML
    protected TextArea contentTextField;

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
    protected ChoiceBox<CorrectionLevel> errorCorrectionLevelChoiceBox;

    @FXML
    protected Button saveButton;

    @FXML
    protected Button logoButton;

    @FXML
    protected Button snapshotDesktopButton;

    @FXML
    protected ChoiceBox<Integer> marginChoiceBox;

    @FXML
    protected ChoiceBox<String> formatImageChoiceBox;

    @FXML
    protected CheckBox logoCheckBox;

    @FXML
    protected JFXSlider logoSlider;
}
