package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: SedentaryReminderToolView
 * @Description: 久坐提醒工具
 * @author: xufeng
 * @date: 2019/6/12 0012 23:08
 */

@Getter
@Setter
public abstract class SedentaryReminderToolView implements Initializable {
    @FXML
    protected Spinner<Integer> timeSpinner;
    @FXML
    protected TextArea messageTextArea;
    @FXML
    protected Button remindButton;
}