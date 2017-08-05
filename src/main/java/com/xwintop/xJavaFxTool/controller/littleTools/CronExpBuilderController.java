package com.xwintop.xJavaFxTool.controller.littleTools;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class CronExpBuilderController implements Initializable {
	@FXML
	private Tab tabSecond;
	@FXML
	private RadioButton radioButtonSecond1;
	@FXML
	private ToggleGroup toggleGroupSecond;
	@FXML
	private RadioButton radioButtonSecond2;
	@FXML
	private Spinner<Integer> secondStart_0;
	@FXML
	private Spinner<Integer> secondEnd_0;
	@FXML
	private RadioButton radioButtonSecond3;
	@FXML
	private Spinner<Integer> secondStart_1;
	@FXML
	private Spinner<Integer> secondEnd_1;
	@FXML
	private RadioButton radioButtonSecond4;
	@FXML
	private Tab tabMinute;
	@FXML
	private RadioButton radioButtonMinute1;
	@FXML
	private ToggleGroup toggleGroupMinute;
	@FXML
	private RadioButton radioButtonMinute2;
	@FXML
	private Spinner<Integer> minuteStart_0;
	@FXML
	private Spinner<Integer> minuteEnd_0;
	@FXML
	private RadioButton radioButtonMinute3;
	@FXML
	private Spinner<Integer> minuteStart_1;
	@FXML
	private Spinner<Integer> minuteEnd_1;
	@FXML
	private RadioButton radioButtonMinute4;
	@FXML
	private Tab tabHour;
	@FXML
	private RadioButton radioButtonHour1;
	@FXML
	private ToggleGroup toggleGroupHour;
	@FXML
	private RadioButton radioButtonHour2;
	@FXML
	private Spinner<Integer> hourStart_0;
	@FXML
	private Spinner<Integer> hourEnd_0;
	@FXML
	private RadioButton radioButtonHour3;
	@FXML
	private Spinner<Integer> hourStart_1;
	@FXML
	private Spinner<Integer> hourEnd_1;
	@FXML
	private RadioButton radioButtonHour4;
	@FXML
	private Tab tabDay;
	@FXML
	private RadioButton radioButtonDay1;
	@FXML
	private ToggleGroup toggleGroupDay;
	@FXML
	private RadioButton radioButtonDay3;
	@FXML
	private Spinner<Integer> dayStart_0;
	@FXML
	private Spinner<Integer> dayEnd_0;
	@FXML
	private RadioButton radioButtonDay4;
	@FXML
	private Spinner<Integer> dayStart_1;
	@FXML
	private Spinner<Integer> dayEnd_1;
	@FXML
	private RadioButton radioButtonDay7;
	@FXML
	private RadioButton radioButtonDay2;
	@FXML
	private RadioButton radioButtonDay5;
	@FXML
	private Spinner<Integer> dayStart_2;
	@FXML
	private RadioButton radioButtonDay6;
	@FXML
	private Tab tabMonth;
	@FXML
	private RadioButton radioButtonMonth1;
	@FXML
	private ToggleGroup toggleGroupMonth;
	@FXML
	private RadioButton radioButtonMonth3;
	@FXML
	private Spinner<Integer> monthStart_0;
	@FXML
	private Spinner<Integer> monthEnd_0;
	@FXML
	private RadioButton radioButtonMonth4;
	@FXML
	private Spinner<Integer> monthStart_1;
	@FXML
	private Spinner<Integer> monthEnd_1;
	@FXML
	private RadioButton radioButtonMonth6;
	@FXML
	private RadioButton radioButtonMonth2;
	@FXML
	private RadioButton radioButtonMonth5;
	@FXML
	private Tab tabWeek;
	@FXML
	private RadioButton radioButtonWeek1;
	@FXML
	private ToggleGroup toggleGroupWeek;
	@FXML
	private RadioButton radioButtonWeek3;
	@FXML
	private Spinner<Integer> weekStart_0;
	@FXML
	private Spinner<Integer> weekEnd_0;
	@FXML
	private RadioButton radioButtonWeek4;
	@FXML
	private Spinner<Integer> weekStart_1;
	@FXML
	private Spinner<Integer> weekEnd_1;
	@FXML
	private RadioButton radioButtonWeek6;
	@FXML
	private RadioButton radioButtonWeek2;
	@FXML
	private Spinner<Integer> weekStart_2;
	@FXML
	private RadioButton radioButtonWeek5;
	@FXML
	private Tab tabYear;
	@FXML
	private RadioButton radioButtonYear1;
	@FXML
	private ToggleGroup toggleGroupYear;
	@FXML
	private RadioButton radioButtonYear3;
	@FXML
	private Spinner<Integer> yearStart_0;
	@FXML
	private Spinner<Integer> yearEnd_0;
	@FXML
	private RadioButton radioButtonYear2;
	@FXML
	private TextField jTF_Cron_Second;
	@FXML
	private TextField jTF_Cron_Minute;
	@FXML
	private TextField jTF_Cron_Hour;
	@FXML
	private TextField jTF_Cron_Day;
	@FXML
	private TextField jTF_Cron_Month;
	@FXML
	private TextField jTF_Cron_Week;
	@FXML
	private TextField jTF_Cron_Year;
	@FXML
	private TextField jTF_Cron_Exp;
	@FXML
	private Button button_Parse;
	@FXML
	private TextField jTF_Schedule_Start;
	@FXML
	private Button copyExpButton;
	@FXML
	private TextArea jTA_Schedule_Next;

	
	private CheckBox[] secendCheckBox = new CheckBox[60];
	private CheckBox[] minuteCheckBox = new CheckBox[60];
	private CheckBox[] hourCheckBox = new CheckBox[24];
	private CheckBox[] dayCheckBox = new CheckBox[31];
	private CheckBox[] monthCheckBox = new CheckBox[12];
	private CheckBox[] weekCheckBox = new CheckBox[7];
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		Calendar calendar = Calendar.getInstance();
		JavaFxViewUtil.setSpinnerValueFactory(secondStart_0, 1, 58);
		JavaFxViewUtil.setSpinnerValueFactory(secondEnd_0, 2, 59);
		JavaFxViewUtil.setSpinnerValueFactory(secondStart_1, 0, 59);
		JavaFxViewUtil.setSpinnerValueFactory(secondEnd_1, 1, 59);
		JavaFxViewUtil.setSpinnerValueFactory(minuteStart_0, 1, 58,calendar.get(Calendar.MINUTE));
		JavaFxViewUtil.setSpinnerValueFactory(minuteEnd_0, 2, 59,calendar.get(Calendar.MINUTE)+1);
		JavaFxViewUtil.setSpinnerValueFactory(minuteStart_1, 0, 59,calendar.get(Calendar.MINUTE));
		JavaFxViewUtil.setSpinnerValueFactory(minuteEnd_1, 1, 59);
		JavaFxViewUtil.setSpinnerValueFactory(hourStart_0, 0, 23,calendar.get(Calendar.HOUR));
		JavaFxViewUtil.setSpinnerValueFactory(hourEnd_0, 2, 23,calendar.get(Calendar.HOUR)+1);
		JavaFxViewUtil.setSpinnerValueFactory(hourStart_1, 0, 23,calendar.get(Calendar.HOUR));
		JavaFxViewUtil.setSpinnerValueFactory(hourEnd_1, 1, 23);
		JavaFxViewUtil.setSpinnerValueFactory(dayStart_0, 1, 31,calendar.get(Calendar.DAY_OF_MONTH));
		JavaFxViewUtil.setSpinnerValueFactory(dayEnd_0, 2, 31,calendar.get(Calendar.DAY_OF_MONTH)+1);
		JavaFxViewUtil.setSpinnerValueFactory(dayStart_1, 1, 31,calendar.get(Calendar.DAY_OF_MONTH));
		JavaFxViewUtil.setSpinnerValueFactory(dayEnd_1, 1, 31);
		JavaFxViewUtil.setSpinnerValueFactory(dayStart_2, 1, 31,calendar.get(Calendar.DAY_OF_MONTH));
		JavaFxViewUtil.setSpinnerValueFactory(monthStart_0, 1, 12,calendar.get(Calendar.MONTH)+1);
		JavaFxViewUtil.setSpinnerValueFactory(monthEnd_0, 2, 12,calendar.get(Calendar.MONTH)+1);
		JavaFxViewUtil.setSpinnerValueFactory(monthStart_1, 1, 12,calendar.get(Calendar.DAY_OF_MONTH));
		JavaFxViewUtil.setSpinnerValueFactory(monthEnd_1, 1, 12);
		JavaFxViewUtil.setSpinnerValueFactory(weekStart_0, 1, 7);
		JavaFxViewUtil.setSpinnerValueFactory(weekEnd_0, 2, 7);
		JavaFxViewUtil.setSpinnerValueFactory(weekStart_1, 1, 4);
		JavaFxViewUtil.setSpinnerValueFactory(weekEnd_1, 1, 7,calendar.get(Calendar.DAY_OF_WEEK));
		JavaFxViewUtil.setSpinnerValueFactory(weekStart_2, 1, 7);
		JavaFxViewUtil.setSpinnerValueFactory(yearStart_0, 2012, 3000,calendar.get(Calendar.YEAR));
		JavaFxViewUtil.setSpinnerValueFactory(yearEnd_0, 2013, 3000,calendar.get(Calendar.YEAR)+1);
		try {
		for (int i = 0; i < 60; i++) {
				secendCheckBox[i] = new CheckBox(String.format("%02d", i));
				secendCheckBox[i].setLayoutX(32 + i % 10 * 60);
				secendCheckBox[i].setLayoutY(120 + i / 10 * 20);
				((AnchorPane) tabSecond.getContent()).getChildren().add(secendCheckBox[i]);
				minuteCheckBox[i] = new CheckBox(String.format("%02d", i));
				minuteCheckBox[i].setLayoutX(32 + i % 10 * 60);
				minuteCheckBox[i].setLayoutY(120 + i / 10 * 20);
				((AnchorPane) tabMinute.getContent()).getChildren().add(minuteCheckBox[i]);
				if(i<24) {
					hourCheckBox[i] = new CheckBox(String.format("%02d", i));
					hourCheckBox[i].setLayoutX(32 + i % 10 * 60);
					hourCheckBox[i].setLayoutY(120 + i / 10 * 20);
					((AnchorPane) tabHour.getContent()).getChildren().add(hourCheckBox[i]);
				}
				if(i<31) {
					dayCheckBox[i] = new CheckBox(String.format("%2d", i+1));
					dayCheckBox[i].setLayoutX(32 + i % 12 * 60);
					dayCheckBox[i].setLayoutY(200 + i / 12 * 20);
					((AnchorPane) tabDay.getContent()).getChildren().add(dayCheckBox[i]);
				}
				if(i<12) {
					monthCheckBox[i] = new CheckBox(String.format("%2d", i+1));
					monthCheckBox[i].setLayoutX(32 + i % 12 * 50);
					monthCheckBox[i].setLayoutY(146);
					((AnchorPane) tabMonth.getContent()).getChildren().add(monthCheckBox[i]);
				}
				if(i<7) {
					weekCheckBox[i] = new CheckBox(String.format("%2d", i+1));
					weekCheckBox[i].setLayoutX(32 + i % 10 * 60);
					weekCheckBox[i].setLayoutY(180 + i / 10 * 20);
					((AnchorPane) tabWeek.getContent()).getChildren().add(weekCheckBox[i]);
				}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initEvent() {
	}

	@FXML
	private void parseActionPerformed(ActionEvent event) {
	}

	@FXML
	private void copyExpAction(ActionEvent event) {
		jTA_Schedule_Next.setText(jTF_Cron_Exp.getText());
	}
}