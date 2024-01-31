package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @ClassName: CoordinateTransformToolView
 * @Description: 坐标系转换工具
 * @author: xufeng
 * @date: 2021/1/18 13:37
 */

@Getter
@Setter
public abstract class CoordinateTransformToolView implements Initializable {

    @FXML
    protected TextArea sourceCoordinateTextArea;

    @FXML
    protected TextArea targetCoordinateTextArea;

    @FXML
    protected ChoiceBox<String> sourceCrsTypeChoiceBox;

    @FXML
    protected TextField sourceCrsDesc;

    @FXML
    protected TextField sourceOffsetX;

    @FXML
    protected TextField sourceOffsetY;

    @FXML
    protected TextArea sourceWkt;

    @FXML
    protected ChoiceBox<String> targetCrsTypeChoiceBox;

    @FXML
    protected TextField targetCrsDesc;

    @FXML
    protected TextField targetOffsetX;

    @FXML
    protected TextField targetOffsetY;

    @FXML
    protected TextArea targetWkt;

    @FXML
    protected Button transformButton;

}