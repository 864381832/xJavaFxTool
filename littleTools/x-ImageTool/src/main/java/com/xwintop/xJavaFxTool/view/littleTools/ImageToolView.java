package com.xwintop.xJavaFxTool.view.littleTools;

import com.jfoenix.controls.JFXSlider;
import com.xwintop.xJavaFxTool.services.littleTools.ImageInfo;
import com.xwintop.xJavaFxTool.services.littleTools.OutputFormats;
import com.xwintop.xJavaFxTool.services.littleTools.ResizeMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ImageToolView implements Initializable {

    @FXML
    protected Button addImageButton;

    @FXML
    protected Button openFolderButton;

    @FXML
    protected Button imageCompressionButton;

    @FXML
    protected TableView<ImageInfo> tableViewMain;

    @FXML
    protected ImageView originalImageView;

    @FXML
    protected ImageView outputImageView;

    @FXML
    protected JFXSlider qualitySlider;
    @FXML
    protected JFXSlider rotateSlider;

    @FXML
    protected ChoiceBox<OutputFormats> formatChoiceBox;

    @FXML
    protected CheckBox isResizeCheckBox;

    @FXML
    protected ChoiceBox<ResizeMode> resizeModeChoiceBox;

    @FXML
    protected Spinner<Integer> scaleWidthSpinner;

    @FXML
    protected Spinner<Integer> scaleHeightSpinner;

    @FXML
    protected CheckBox keepAspectRatioCheckBox;

    @FXML
    protected TextField outputFolderTextField;

    @FXML
    protected Button outputFolderButton;

    @FXML
    protected CheckBox sameFolderAsInput;

    @FXML
    protected TextField suffixNameTextField;

}
