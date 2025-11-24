package com.xwintop.xJavaFxTool.view.games;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: BullsAndCowsGameView
 * @Description: 猜数字小游戏
 * @author: xufeng
 * @date: 2019/8/24 0024 15:06
 */

@Getter
@Setter
public abstract class BullsAndCowsGameView implements Initializable {
    @FXML
    protected Label daojishiTimeLabel;
    @FXML
    protected TextField inputNumberTextField1;
    @FXML
    protected TextField inputNumberTextField2;
    @FXML
    protected TextField inputNumberTextField3;
    @FXML
    protected TextField inputNumberTextField4;
    @FXML
    protected VBox answerVBox1;
    @FXML
    protected VBox answerVBox2;
    @FXML
    protected Label recordNumberLabel;
    @FXML
    protected Label recordTimeLabel;
    @FXML
    protected Button enterButton;
    @FXML
    protected Label rightAnswersLabel;

}