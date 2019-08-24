package com.xwintop.xJavaFxTool.services.games;

import com.xwintop.xJavaFxTool.controller.games.BullsAndCowsGameController;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName: BullsAndCowsGameService
 * @Description: 猜数字小游戏
 * @author: xufeng
 * @date: 2019/8/24 0024 15:06
 */

@Getter
@Setter
@Slf4j
public class BullsAndCowsGameService {
    private BullsAndCowsGameController bullsAndCowsGameController;
    private int daojishiTime = 0;
    private int[] answerNumbers = new int[4];
    private int enterAnswerNumber = 0;
    private Timer timer;

    private int recordNumber = 0;
    private int recordTime = 0;

    public BullsAndCowsGameService(BullsAndCowsGameController bullsAndCowsGameController) {
        this.bullsAndCowsGameController = bullsAndCowsGameController;
    }

    public void initRecordData() {
        try {
            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(ConfigureUtil.getConfigureFile("BullsAndCowsGameConfigure.properties"));
            recordNumber = xmlConfigure.getInt("recordNumber", 0);
            recordTime = xmlConfigure.getInt("recordTime", 0);
            this.setRecordData();
        } catch (Exception e) {
            log.error("加载配置失败！");
        }
    }

    public void initAnswerNumber() {
        daojishiTime = 0;
        enterAnswerNumber = 0;
        for (int i = 0; i < answerNumbers.length; i++) {
            Random ran = new Random();
            int a = ran.nextInt(10);
            if (i - 1 != -1) {
                for (int j = 0; j < i; j++) {
                    if (a == answerNumbers[j]) {
                        i--;
                        break;
                    } else {
                        answerNumbers[i] = a;
                    }
                }
            } else {
                answerNumbers[i] = a;
            }
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                daojishiTime++;
                int hours = daojishiTime / 60 / 60;
                int minutes = daojishiTime / 60;
                int seconds = daojishiTime % 60;
                Platform.runLater(() -> {
                    bullsAndCowsGameController.getDaojishiTimeLabel().setText("计时:  " + hours + "  小时  " + minutes + "  分  " + seconds + "  秒");
                });
            }
        }, 0, 1000);
        Platform.runLater(() -> {
            for (int i = 0; i < 20; i++) {
                HBox answerHbox = bullsAndCowsGameController.getAnswerHboxs()[i];
                ((Label) answerHbox.getChildren().get(1)).setText("");
                ((Label) answerHbox.getChildren().get(3)).setText("");
                ((Label) answerHbox.getChildren().get(5)).setText("");
            }
        });
    }

    public void enterInputNumber() {
        String inputNumberString = "";
        int[] inputNumsArray = new int[4];
        for (int i = 0; i < 4; i++) {
            TextField inputNumberTextField = bullsAndCowsGameController.getInputNumberTextFields()[i];
            if (StringUtils.isEmpty(inputNumberTextField.getText())) {
                AlertUtil.showInfoAlert("请输入四个0-9的数字！");
                return;
            }
            inputNumsArray[i] = Integer.parseInt(inputNumberTextField.getText());
            inputNumberString += inputNumsArray[i];
        }
        int aA = 0;
        int bB = 0;
        for (int i = 0; i < answerNumbers.length; i++) {
            if (inputNumsArray[i] == answerNumbers[i]) {
                aA += 1;
            }
            for (int j = 0; j < 4; j++) {
                if (inputNumsArray[j] == answerNumbers[i]) {
                    bB += 1;
                    break;
                }
            }
        }
        HBox answerHbox = bullsAndCowsGameController.getAnswerHboxs()[enterAnswerNumber];
        ((Label) answerHbox.getChildren().get(1)).setText(inputNumberString);
        ((Label) answerHbox.getChildren().get(5)).setText("" + aA);
        ((Label) answerHbox.getChildren().get(3)).setText("" + bB);
        enterAnswerNumber++;
        if (aA == 4) {
            timer.cancel();
            timer = null;
            AlertUtil.showInfoAlert("恭喜你，答对了！答案是：" + inputNumberString);
            bullsAndCowsGameController.getRightAnswersLabel().setText("正确答案：" + inputNumberString);
            bullsAndCowsGameController.getEnterButton().setText("重玩");
            bullsAndCowsGameController.getRightAnswersLabel().setVisible(true);
            if (recordNumber == 0 || enterAnswerNumber < recordNumber) {
                recordNumber = enterAnswerNumber;
            }
            if (daojishiTime == 0 || recordTime < daojishiTime) {
                recordTime = daojishiTime;
            }
            setRecordData();
        }
        if (enterAnswerNumber == 10) {
            AlertUtil.showInfoAlert("您还有10次机会！");
        }
        if (enterAnswerNumber >= 20) {
            timer.cancel();
            timer = null;
            AlertUtil.showInfoAlert("挑战失败，点击\"重玩\"继续游戏！");
            bullsAndCowsGameController.getRightAnswersLabel().setText("正确答案：" + answerNumbers[0] + answerNumbers[1] + answerNumbers[2] + answerNumbers[3]);
            bullsAndCowsGameController.getEnterButton().setText("重玩");
            bullsAndCowsGameController.getRightAnswersLabel().setVisible(true);
        }
        System.out.println("答案是：" + answerNumbers[0] + answerNumbers[1] + answerNumbers[2] + answerNumbers[3]);
    }

    private void setRecordData() {
        int hours = recordTime / 60 / 60;
        int minutes = recordTime / 60;
        int seconds = recordTime % 60;
        Platform.runLater(() -> {
            bullsAndCowsGameController.getRecordNumberLabel().setText("最少次数：第  " + recordNumber + " 次完成");
            bullsAndCowsGameController.getRecordTimeLabel().setText("最短时间:  " + hours + "  小时  " + minutes + "  分  " + seconds + "  秒");
        });
        try {
            File file = ConfigureUtil.getConfigureFile("BullsAndCowsGameConfigure.properties");
            FileUtils.touch(file);
            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(ConfigureUtil.getConfigureFile("BullsAndCowsGameConfigure.properties"));
            xmlConfigure.setProperty("recordNumber", recordNumber);
            xmlConfigure.setProperty("recordTime", recordTime);
            xmlConfigure.save();
        } catch (Exception e) {
            log.error("保存配置失败！", e);
        }
    }
}