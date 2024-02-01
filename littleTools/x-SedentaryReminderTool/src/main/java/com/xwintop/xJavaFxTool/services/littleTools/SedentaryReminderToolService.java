package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.SedentaryReminderToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.geometry.Pos;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName: SedentaryReminderToolService
 * @Description: 久坐提醒工具
 * @author: xufeng
 * @date: 2019/6/12 0012 23:08
 */

@Getter
@Setter
@Slf4j
public class SedentaryReminderToolService {
    private SedentaryReminderToolController sedentaryReminderToolController;
    private Timer timer;

    public SedentaryReminderToolService(SedentaryReminderToolController sedentaryReminderToolController) {
        this.sedentaryReminderToolController = sedentaryReminderToolController;
    }

    public void remindAction() {
        Integer time = sedentaryReminderToolController.getTimeSpinner().getValue();
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TooltipUtil.showToast("久坐提醒", sedentaryReminderToolController.getMessageTextArea().getText(), Pos.BOTTOM_RIGHT);
                }
            }, time * 1000 * 60, time * 1000 * 60);
        }
    }

    public void stopAction() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}