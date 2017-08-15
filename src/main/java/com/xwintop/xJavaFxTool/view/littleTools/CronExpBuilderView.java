package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public abstract class CronExpBuilderView implements Initializable {
	@FXML
	protected TabPane mainTabPane;
	@FXML
	protected Tab tabSecond;
	@FXML
	protected RadioButton radioButtonSecond1;
	@FXML
	protected ToggleGroup toggleGroupSecond;
	@FXML
	protected RadioButton radioButtonSecond2;
	@FXML
	protected Spinner<Integer> secondStart_0;
	@FXML
	protected Spinner<Integer> secondEnd_0;
	@FXML
	protected RadioButton radioButtonSecond3;
	@FXML
	protected Spinner<Integer> secondStart_1;
	@FXML
	protected Spinner<Integer> secondEnd_1;
	@FXML
	protected RadioButton radioButtonSecond4;
	@FXML
	protected Tab tabMinute;
	@FXML
	protected RadioButton radioButtonMinute1;
	@FXML
	protected ToggleGroup toggleGroupMinute;
	@FXML
	protected RadioButton radioButtonMinute2;
	@FXML
	protected Spinner<Integer> minuteStart_0;
	@FXML
	protected Spinner<Integer> minuteEnd_0;
	@FXML
	protected RadioButton radioButtonMinute3;
	@FXML
	protected Spinner<Integer> minuteStart_1;
	@FXML
	protected Spinner<Integer> minuteEnd_1;
	@FXML
	protected RadioButton radioButtonMinute4;
	@FXML
	protected Tab tabHour;
	@FXML
	protected RadioButton radioButtonHour1;
	@FXML
	protected ToggleGroup toggleGroupHour;
	@FXML
	protected RadioButton radioButtonHour2;
	@FXML
	protected Spinner<Integer> hourStart_0;
	@FXML
	protected Spinner<Integer> hourEnd_0;
	@FXML
	protected RadioButton radioButtonHour3;
	@FXML
	protected Spinner<Integer> hourStart_1;
	@FXML
	protected Spinner<Integer> hourEnd_1;
	@FXML
	protected RadioButton radioButtonHour4;
	@FXML
	protected Tab tabDay;
	@FXML
	protected RadioButton radioButtonDay1;
	@FXML
	protected ToggleGroup toggleGroupDay;
	@FXML
	protected RadioButton radioButtonDay3;
	@FXML
	protected Spinner<Integer> dayStart_0;
	@FXML
	protected Spinner<Integer> dayEnd_0;
	@FXML
	protected RadioButton radioButtonDay4;
	@FXML
	protected Spinner<Integer> dayStart_1;
	@FXML
	protected Spinner<Integer> dayEnd_1;
	@FXML
	protected RadioButton radioButtonDay7;
	@FXML
	protected RadioButton radioButtonDay2;
	@FXML
	protected RadioButton radioButtonDay5;
	@FXML
	protected Spinner<Integer> dayStart_2;
	@FXML
	protected RadioButton radioButtonDay6;
	@FXML
	protected Tab tabMonth;
	@FXML
	protected RadioButton radioButtonMonth1;
	@FXML
	protected ToggleGroup toggleGroupMonth;
	@FXML
	protected RadioButton radioButtonMonth3;
	@FXML
	protected Spinner<Integer> monthStart_0;
	@FXML
	protected Spinner<Integer> monthEnd_0;
	@FXML
	protected RadioButton radioButtonMonth4;
	@FXML
	protected Spinner<Integer> monthStart_1;
	@FXML
	protected Spinner<Integer> monthEnd_1;
	@FXML
	protected RadioButton radioButtonMonth2;
	@FXML
	protected RadioButton radioButtonMonth5;
	@FXML
	protected Tab tabWeek;
	@FXML
	protected RadioButton radioButtonWeek1;
	@FXML
	protected ToggleGroup toggleGroupWeek;
	@FXML
	protected RadioButton radioButtonWeek3;
	@FXML
	protected Spinner<Integer> weekStart_0;
	@FXML
	protected Spinner<Integer> weekEnd_0;
	@FXML
	protected RadioButton radioButtonWeek4;
	@FXML
	protected Spinner<Integer> weekStart_1;
	@FXML
	protected Spinner<Integer> weekEnd_1;
	@FXML
	protected RadioButton radioButtonWeek6;
	@FXML
	protected RadioButton radioButtonWeek2;
	@FXML
	protected Spinner<Integer> weekStart_2;
	@FXML
	protected RadioButton radioButtonWeek5;
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
	protected TextField jTF_Cron_Exp;
	@FXML
	protected Button button_Parse;
	@FXML
	protected TextField jTF_Schedule_Start;
	@FXML
	protected Button copyExpButton;
	@FXML
	protected TextArea jTA_Schedule_Next;
}
