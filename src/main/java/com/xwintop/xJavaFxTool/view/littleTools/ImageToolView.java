package com.xwintop.xJavaFxTool.view.littleTools;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXSlider;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
    protected TableView tableViewMain;
    @FXML
    protected TableColumn nameTableColumn;
    @FXML
    protected TableColumn sizeTableColumn;
    @FXML
    protected TableColumn resolutionTableColumn;
    @FXML
    protected TableColumn fullPathTableColumn;
    @FXML
    protected ImageView showImageView;
    @FXML
    protected ImageView showCompressionImageView;
    @FXML
    protected JFXSlider qualitySlider;
    @FXML
    protected ChoiceBox<String> formatChoiceBox;
    @FXML
    protected CheckBox isResizeCheckBox;
    @FXML
    protected ChoiceBox<String> sizeTypeChoiceBox;
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