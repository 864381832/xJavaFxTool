package com.xwintop.xJavaFxTool.controller.assistTools;

import com.xwintop.xJavaFxTool.services.assistTools.TextToSpeechToolService;
import com.xwintop.xJavaFxTool.view.assistTools.TextToSpeechToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * @ClassName: TextToSpeechToolController
 * @Description: 语音转换工具
 * @author: xufeng
 * @date: 2018/2/6 15:52
 */

@Getter
@Setter
@Log4j
public class TextToSpeechToolController extends TextToSpeechToolView {
    private TextToSpeechToolService textToSpeechToolService = new TextToSpeechToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        per0RadioButton.setUserData("0");
        per1RadioButton.setUserData("1");
        per3RadioButton.setUserData("3");
        per4RadioButton.setUserData("4");
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void playAction(ActionEvent event) throws Exception {
        if("播放".equals(playButton.getText())){
            playButton.setText("停止");
            textToSpeechToolService.playAction();
        }else{
            playButton.setText("播放");
            textToSpeechToolService.stopPlayAction();
        }
    }

    @FXML
    void saveAudioAction(ActionEvent event) throws Exception {
        textToSpeechToolService.saveAudioAction();
    }
}