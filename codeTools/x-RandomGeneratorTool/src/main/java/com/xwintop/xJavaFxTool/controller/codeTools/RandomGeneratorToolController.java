package com.xwintop.xJavaFxTool.controller.codeTools;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.xwintop.xJavaFxTool.services.codeTools.RandomGeneratorToolService;
import com.xwintop.xJavaFxTool.view.codeTools.RandomGeneratorToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: RandomGeneratorToolController
 * @Description: 随机数生成工具
 * @author: xufeng
 * @date: 2019/6/15 0015 0:52
 */

@Getter
@Setter
@Slf4j
public class RandomGeneratorToolController extends RandomGeneratorToolView {
    private RandomGeneratorToolService randomGeneratorToolService = new RandomGeneratorToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void generateUUID(ActionEvent event) {
        uuidResult.setText(IdUtil.simpleUUID());
    }

    @FXML
    private void generateNumber(ActionEvent event) {
        String flo = floor.getText();
        String cei = ceil.getText();
        int p = stringToInt(precision.getText());
        int f = stringToInt(flo);
        int c = stringToInt(cei);
        numberResult.setText(String.valueOf(RandomUtil.randomInt(f, c)));
    }

    @FXML
    private void generateEmail(ActionEvent event) {
        String email = RandomUtil.randomString(RandomUtil.randomInt(3, 10)) + "@" + RandomUtil.randomString(RandomUtil.BASE_CHAR, RandomUtil.randomInt(3, 5)) + "." + RandomUtil.randomString(RandomUtil.BASE_CHAR, RandomUtil.randomInt(1, 5));
        emailResult.setText(email);
    }

    @FXML
    private void generateLowerCase(ActionEvent event) {
        lowerCaseResult.setText(RandomUtil.randomString(RandomUtil.BASE_CHAR, getLength(lowerCaseLength.getText())));
    }

    @FXML
    private void generateUpperCase(ActionEvent event) {
        upperCaseResult.setText(RandomUtil.randomString(RandomUtil.BASE_CHAR, getLength(upperCaseLength.getText())).toUpperCase());
    }

    @FXML
    private void generateLetter(ActionEvent event) {
        letterResult.setText(RandomUtil.randomString(RandomUtil.BASE_CHAR + RandomUtil.BASE_CHAR.toUpperCase(), getLength(letterLength.getText())));
    }

    @FXML
    private void generateString(ActionEvent event) {
        stringResult.setText(RandomUtil.randomString(getLength(stringLength.getText())));
    }

    @FXML
    private void generateText(ActionEvent event) {
        textResult.setText(RandomUtil.randomString(RandomUtil.BASE_CHAR + RandomUtil.BASE_CHAR.toUpperCase() + "~!@#$%^&*()_+{}:\"<>?`-=[];'\\|,./", getLength(textLength.getText())));
    }

    public static int stringToInt(String integer) {
        int result = 0;
        try {
            result = Integer.parseInt(integer.trim());
        } catch (Exception e) {
            log.warn("转换异常" + e.getMessage());
        }
        return result > -1 ? result : 0;
    }

    private int getLength(String len) {
        int length = 0;
        try {
            length = Integer.parseInt(len.trim());
        } catch (Exception e) {
            log.warn("转换异常" + e.getMessage());
        }
        return length < 1 ? RandomUtil.randomInt(9, 16) : length;
    }
}