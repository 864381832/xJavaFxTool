package com.xwintop.xJavaFxTool.view.games;

import com.xwintop.xJavaFxTool.controller.games.sudoku.SCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class SudokuView implements Initializable {
    @FXML
    protected CheckMenuItem noteItem;
    @FXML
    protected CheckMenuItem showCandidates;
    @FXML
    protected CheckMenuItem showSelectedRC;
    @FXML
    protected CheckMenuItem showSelectedBlock;

    @FXML
    protected ImageView noteIcon;
    @FXML
    protected Text times;
    @FXML
    protected Text timer;
    @FXML
    protected ChoiceBox<String> levelChoiceBox;
    @FXML
    protected List<SCell> cells;
}
