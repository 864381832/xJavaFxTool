package com.xwintop.xJavaFxTool.view.games;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import java.lang.*;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

@Getter
@Setter
public abstract class ChineseChessView implements Initializable {
    @FXML
    protected Pane checkerBoard;//棋盘画布对象
    @FXML
    protected Button startButton;
    @FXML
    protected Button reStartButton;
    @FXML
    protected Button showRecordButton;
    @FXML
    protected Button saveRecordButton;

}