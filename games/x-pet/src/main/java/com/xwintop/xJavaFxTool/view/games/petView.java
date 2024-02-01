package com.xwintop.xJavaFxTool.view.games;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

@Getter
@Setter
public abstract class petView implements Initializable {
    @FXML
    protected Button btnReset;
}