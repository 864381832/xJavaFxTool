package com.xwintop.xJavaFxTool.view.games;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

@Getter
@Setter
public abstract class X2048View implements Initializable {
    @FXML
    protected Label tbScore;
    @FXML
    protected Slider sliderSize;
    @FXML
    protected Button btnReset;
    @FXML
    protected Pane playArea;
}