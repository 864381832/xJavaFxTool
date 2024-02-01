package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.view.littleTools.RelationshipCalculatorView;
import com.xwintop.xJavaFxTool.services.littleTools.RelationshipCalculatorService;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Getter
@Setter
@Slf4j
public class RelationshipCalculatorController extends RelationshipCalculatorView {
    private RelationshipCalculatorService relationshipCalculatorService = new RelationshipCalculatorService(this);

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
        // 【丈夫】按钮的事件监听器
    void do_husbandButton_event(ActionEvent event) {
        setTextByButton(husbandButton);
    }

    @FXML
        // 【妻子】按钮的事件监听器
    void do_wifeButton_event(ActionEvent event) {
        setTextByButton(wifeButton);
    }

    @FXML
        // 【爸爸】按钮的事件监听器
    void do_fatherButton_event(ActionEvent event) {
        setTextByButton(fatherButton);
    }

    @FXML
        // 【妈妈】按钮的事件监听器
    void do_montherButton_event(ActionEvent event) {
        setTextByButton(montherButton);
    }

    @FXML
        // 【儿子】按钮的事件监听器
    void do_sonButton_event(ActionEvent event) {
        setTextByButton(sonButton);
    }

    @FXML
        // 【女儿】按钮的事件监听器
    void do_daughterButton_event(ActionEvent event) {
        setTextByButton(daughterButton);
    }

    @FXML
        // 【哥哥】按钮的事件监听器
    void do_bigBrotherButton_event(ActionEvent event) {
        setTextByButton(bigBrotherButton);
    }

    @FXML
        // 【弟弟】按钮的事件监听器
    void do_smallBrotherButton_event(ActionEvent event) {
        setTextByButton(smallBrotherButton);
    }

    @FXML
        // 【姐姐】按钮的事件监听器
    void do_bigSisterButton_event(ActionEvent event) {
        setTextByButton(bigSisterButton);
    }

    @FXML
        // 【妹妹】按钮的事件监听器
    void do_smallSisterButton_event(ActionEvent event) {
        setTextByButton(smallSisterButton);
    }

    @FXML
        // 【回退】按钮的事件监听器
    void do_rebackButton_event(ActionEvent event) {
        relationshipCalculatorService.do_rebackButton_event();
    }

    @FXML
        // 【清空】按钮的事件监听器
    void do_clearButton_event(ActionEvent event) {
        inputTextArea.setText("我");
        outputTextArea.setText("");
    }

    @FXML
        // 【计算】按钮的事件监听器
    void do_countButton_event(ActionEvent event) {
        relationshipCalculatorService.do_countButton_event();
    }

    /**
     * 操作结果：根据按钮设置文本
     *
     * @param button 按钮
     */
    public void setTextByButton(Button button) {
        relationshipCalculatorService.setTextByButton(button);
    }
}