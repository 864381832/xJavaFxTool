package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.SedentaryReminderToolService;
import com.xwintop.xJavaFxTool.view.littleTools.SedentaryReminderToolView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: SedentaryReminderToolController
 * @Description: 久坐提醒工具
 * @author: xufeng
 * @date: 2019/6/12 0012 23:08
 */

@Getter
@Setter
@Slf4j
public class SedentaryReminderToolController extends SedentaryReminderToolView {
    private SedentaryReminderToolService sedentaryReminderToolService = new SedentaryReminderToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setSpinnerValueFactory(timeSpinner, 1, Integer.MAX_VALUE, 10);
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void remindAction(ActionEvent event) {
        if ("开启提醒".equals(remindButton.getText())) {
            sedentaryReminderToolService.remindAction();
            remindButton.setText("停止提醒");
        } else {
            sedentaryReminderToolService.stopAction();
            remindButton.setText("开启提醒");
        }
    }
}