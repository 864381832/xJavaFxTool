package com.xwintop.xJavaFxTool.controller.assistTools;

import com.xwintop.xJavaFxTool.services.assistTools.WechatJumpGameToolService;
import com.xwintop.xJavaFxTool.view.assistTools.WechatJumpGameToolView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * @ClassName: WechatJumpGameToolController
 * @Description: 微信跳一跳助手
 * @author: xufeng
 * @date: 2018/2/6 10:31
 */

@Getter
@Setter
@Log4j
public class WechatJumpGameToolController extends WechatJumpGameToolView {
    private WechatJumpGameToolService wechatJumpGameToolService = new WechatJumpGameToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
        autoRestartCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                wechatJumpGameToolService.setRestart(newValue);
            }
        });
    }

    private void initService() {
        wechatJumpGameToolService.init();
    }

    @FXML
    private void adbConnectAction(ActionEvent event) {
        wechatJumpGameToolService.adbConnectAction();
    }

    @FXML
    private void adbRunAction(ActionEvent event) {
        wechatJumpGameToolService.adbRunAction();
    }
}