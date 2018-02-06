package com.xwintop.xJavaFxTool.controller.assistTools;

import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.assistTools.WechatJumpGameToolView;
import com.xwintop.xJavaFxTool.services.assistTools.WechatJumpGameToolService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        JavaFxViewUtil.setSpinnerValueFactory(waitTimeSpinner, 1, Integer.MAX_VALUE);
    }

    private void initEvent() {
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