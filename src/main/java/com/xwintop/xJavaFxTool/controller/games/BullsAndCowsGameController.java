package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.services.games.BullsAndCowsGameService;
import com.xwintop.xJavaFxTool.view.games.BullsAndCowsGameView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: BullsAndCowsGameController
 * @Description: 猜数字小游戏
 * @author: xufeng
 * @date: 2019/8/24 0024 15:05
 */

@Getter
@Setter
@Slf4j
public class BullsAndCowsGameController extends BullsAndCowsGameView {
    private BullsAndCowsGameService bullsAndCowsGameService = new BullsAndCowsGameService(this);

    private TextField[] inputNumberTextFields = null;
    private TextField inputNumberTextField;

    private HBox[] answerHboxs = new HBox[20];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        for (int i = 0; i < 20; i++) {
            Label[] labels = new Label[7];
            labels[0] = new Label((i + 1) + "：");
            labels[0].setPrefWidth(50);
            labels[0].setAlignment(Pos.CENTER_RIGHT);
            labels[1] = new Label("  ");
            labels[1].setTextFill(Color.RED);
            labels[1].setAlignment(Pos.CENTER);
            labels[1].setPrefWidth(60);
            labels[2] = new Label("数字对");
            labels[3] = new Label("  ");
            labels[3].setAlignment(Pos.CENTER);
            labels[3].setTextFill(Color.RED);
            labels[3].setPrefWidth(20);
            labels[4] = new Label("个，   位置对");
            labels[5] = new Label("  ");
            labels[5].setAlignment(Pos.CENTER);
            labels[5].setTextFill(Color.RED);
            labels[5].setPrefWidth(20);
            labels[6] = new Label("位");
            for (Label label : labels) {
                label.setFont(Font.font(20));
            }
            answerHboxs[i] = new HBox();
            answerHboxs[i].getChildren().addAll(labels);
            if (i < 10) {
                answerVBox1.getChildren().add(answerHboxs[i]);
            } else {
                answerVBox2.getChildren().add(answerHboxs[i]);
            }
        }
    }

    private void initEvent() {
        inputNumberTextFields = new TextField[]{inputNumberTextField1, inputNumberTextField2, inputNumberTextField3, inputNumberTextField4};
        for (TextField numberTextField : inputNumberTextFields) {
            numberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (StringUtils.isEmpty(newValue)) {
                    return;
                }
                try {
                    int number = Integer.parseInt(newValue);
                    if (number > 9 || number < 0) {
                        numberTextField.setText(oldValue);
                    }
                } catch (Exception e) {
                    log.warn("输入非数字：" + newValue);
                    numberTextField.setText(oldValue);
                }
                for (int i = 0; i < 4; i++) {
                    if (inputNumberTextFields[i] == numberTextField) {
                        final int index = (i == 3 ? 0 : i + 1);
                        Platform.runLater(() -> {
                            inputNumberTextFields[index].requestFocus();
                        });
                        break;
                    }
                }
            });
            numberTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    inputNumberTextField = numberTextField;
                }
            });
        }
        Platform.runLater(() -> {
            inputNumberTextField1.requestFocus();
        });
    }

    private void initService() {
        bullsAndCowsGameService.initAnswerNumber();
        bullsAndCowsGameService.initRecordData();
    }

    @FXML
    private void inputNumberOnAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        if ("清除".equals(button.getText())) {
            for (TextField inputNumberTextField : inputNumberTextFields) {
                inputNumberTextField.setText(null);
            }
        } else if ("确定".equals(button.getText())) {
            bullsAndCowsGameService.enterInputNumber();
        } else if ("重玩".equals(button.getText())) {
            bullsAndCowsGameService.initAnswerNumber();
            button.setText("确定");
            rightAnswersLabel.setVisible(false);
        } else {
            inputNumberTextField.setText(button.getText());
        }
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) {
        System.out.println("移除所以线程");
        if (bullsAndCowsGameService.getTimer() != null) {
            bullsAndCowsGameService.getTimer().cancel();
        }
    }
}