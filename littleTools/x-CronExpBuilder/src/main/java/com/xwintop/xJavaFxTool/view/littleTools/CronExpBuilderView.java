package com.xwintop.xJavaFxTool.view.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FragmentController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public abstract class CronExpBuilderView implements Initializable {

    @FXML
    protected FragmentController secondController;

    @FXML
    protected FragmentController minuteController;

    @FXML
    protected FragmentController hourController;

    @FXML
    protected FragmentController dayController;

    @FXML
    protected FragmentController monthController;

    @FXML
    protected FragmentController weekdayController;

    @FXML
    protected TabPane mainTabPane;

    @FXML
    protected Tab tabSecond;

    @FXML
    protected Tab tabMinute;

    @FXML
    protected Tab tabHour;

    @FXML
    protected Tab tabDay;

    @FXML
    protected Tab tabMonth;

    @FXML
    protected Tab tabWeek;

    @FXML
    protected Tab tabYear;

    @FXML
    protected RadioButton radioButtonYear1;

    @FXML
    protected ToggleGroup toggleGroupYear;

    @FXML
    protected RadioButton radioButtonYear3;

    @FXML
    protected Spinner<Integer> yearStart_0;

    @FXML
    protected Spinner<Integer> yearEnd_0;

    @FXML
    protected RadioButton radioButtonYear2;

    @FXML
    protected TextField jTF_Cron_Second;

    @FXML
    protected TextField jTF_Cron_Minute;

    @FXML
    protected TextField jTF_Cron_Hour;

    @FXML
    protected TextField jTF_Cron_Day;

    @FXML
    protected TextField jTF_Cron_Month;

    @FXML
    protected TextField jTF_Cron_Week;

    @FXML
    protected TextField jTF_Cron_Year;

    @FXML
    protected TextArea jTF_Cron_Exp;

    @FXML
    protected TextField jTF_Schedule_Start;

    @FXML
    protected Button copyExpButton;

    @FXML
    protected Label lblParseError;

    @FXML
    protected ListView<String> lvNextExecutionTimes;
}
