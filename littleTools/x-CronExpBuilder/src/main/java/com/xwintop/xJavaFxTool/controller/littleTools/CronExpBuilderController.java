package com.xwintop.xJavaFxTool.controller.littleTools;

import static com.xwintop.xcore.util.javafx.JavaFxViewUtil.setSpinnerValueFactory;

import com.xwintop.xJavaFxTool.view.littleTools.CronExpBuilderView;
import com.xwintop.xcore.util.KeyValue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronExpression;


/**
 * Cron表达式生成工具
 *
 * @author xufeng
 * @author yiding.he@gmail.com
 */
public class CronExpBuilderController extends CronExpBuilderView {

    private TextField[] cronTextFields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();

        this.secondController.initialize(0, 59, "秒");
        this.secondController.setOnValueChanged(
            () -> jTF_Cron_Second.setText(this.secondController.generateCron())
        );

        this.minuteController.initialize(0, 59, "分钟");
        this.minuteController.setOnValueChanged(
            () -> jTF_Cron_Minute.setText(this.minuteController.generateCron())
        );

        this.hourController.initialize(0, 23, "小时");
        this.hourController.setOnValueChanged(
            () -> jTF_Cron_Hour.setText(this.hourController.generateCron())
        );

        this.dayController.initialize(1, 31, "日");
        this.dayController.setClearMark("?");
        this.dayController.setOnValueChanged(
            () -> jTF_Cron_Day.setText(this.dayController.generateCron())
        );

        this.monthController.initialize(1, 12, "月");
        this.monthController.setOnValueChanged(
            () -> jTF_Cron_Month.setText(this.monthController.generateCron())
        );

        List<KeyValue<String, Integer>> weekDayItems = Arrays.asList(
            new KeyValue<>("日", 1),
            new KeyValue<>("一", 2),
            new KeyValue<>("二", 3),
            new KeyValue<>("三", 4),
            new KeyValue<>("四", 5),
            new KeyValue<>("五", 6),
            new KeyValue<>("六", 7)
        );
        this.weekdayController.initialize(weekDayItems, "周内日");
        this.weekdayController.setClearMark("?");
        this.weekdayController.setOnValueChanged(
            () -> jTF_Cron_Week.setText(this.weekdayController.generateCron())
        );
    }

    private void initView() {
        cronTextFields = new TextField[]{
            jTF_Cron_Second, jTF_Cron_Minute, jTF_Cron_Hour, jTF_Cron_Day,
            jTF_Cron_Month, jTF_Cron_Week, jTF_Cron_Year
        };
        Calendar calendar = Calendar.getInstance();
        setSpinnerValueFactory(yearStart_0, 2012, 3000, calendar.get(Calendar.YEAR));
        setSpinnerValueFactory(yearEnd_0, 2013, 3000, calendar.get(Calendar.YEAR) + 1);

        jTF_Schedule_Start.setText(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    private void initEvent() {

        for (TextField textField : cronTextFields) {
            textField.textProperty().addListener((observable, oldValue, text) ->
                jTF_Cron_Exp.setText(Stream
                    .of(cronTextFields)
                    .map(TextInputControl::getText)
                    .collect(Collectors.joining(" "))
                )
            );
        }

        jTF_Cron_Exp.textProperty().addListener(((observable, oldValue, cron) -> {
            if (StringUtils.isNotBlank(cron)) {
                generateNextExecutionTimes(cron);
            }
        }));

        try {
            addRadioButtonYearListener();
            addSpinnerYearListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void addSpinnerYearListener() throws Exception {
        String checkType = "Year";
        ToggleGroup toggleGroup = (ToggleGroup) FieldUtils.readField(this, "toggleGroup" + checkType, true);
        String[] strings = new String[]{"Start_", "End_"};
        for (int i = 0; i < 2; i++) {
            final int ii = i;
            Spinner<Integer> spinnerStart = (Spinner<Integer>) FieldUtils.readField(
                this, checkType.toLowerCase() + strings[i % 2] + (i / 2), true);
            spinnerStart.getEditor().textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    toggleGroup.getToggles().get(ii / 2 + 1).setSelected(false);
                    toggleGroup.getToggles().get(ii / 2 + 1).setSelected(true);
                }
            );
        }
    }

    // 单独添加年RadioButton监听事件
    private void addRadioButtonYearListener() throws Exception {
        String checkType = "Year";
        ToggleGroup toggleGroup = (ToggleGroup) FieldUtils.readField(this, "toggleGroup" + checkType, true);
        for (Toggle toggle : toggleGroup.getToggles()) {
            toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    return;
                }
                RadioButton radioButton = (RadioButton) ((BooleanProperty) observable).getBean();
                try {
                    TextField textField = jTF_Cron_Year;
                    if (radioButton == toggleGroup.getToggles().get(0)) {
                        textField.setText("");
                    } else if (radioButton == toggleGroup.getToggles().get(1)) {
                        Spinner<Integer> spinnerStart = (Spinner<Integer>) FieldUtils.readField(
                            CronExpBuilderController.this, checkType.toLowerCase() + "Start_0", true);
                        Spinner<Integer> spinnerEnd = (Spinner<Integer>) FieldUtils
                            .readField(CronExpBuilderController.this, checkType.toLowerCase() + "End_0", true);
                        String string = spinnerStart.getValue() + "-" + spinnerEnd.getValue();
                        textField.setText(string);
                    } else if (radioButton == toggleGroup.getToggles().get(2)) {
                        textField.setText("*");
                    }
                } catch (Exception e) {
                }
            });
        }
    }

    private void generateNextExecutionTimes(String cronExpString) {

        Date dd = new Date();
        lvNextExecutionTimes.getItems().clear();
        jTF_Schedule_Start.setText(DateFormatUtils.format(dd, "yyyy-MM-dd HH:mm:ss"));

        try {
            CronExpression exp = new CronExpression(cronExpString);
            for (int i = 0; i < 20; i++) {
                dd = exp.getNextValidTimeAfter(dd);
                lvNextExecutionTimes.getItems().add(DateFormatUtils.format(dd, "yyyy-MM-dd HH:mm:ss"));
            }
            lblParseError.setText("");
        } catch (Exception e) {
            lblParseError.setText(e.getMessage());
        }
    }

    @FXML
    private void copyExpAction() throws Exception {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // 获得系统剪贴板
        clipboard.setContents(new StringSelection(jTF_Cron_Exp.getText()), null);
    }

    public void copyAllNextExecutionTimes() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // 获得系统剪贴板
        clipboard.setContents(new StringSelection(
            String.join("\n", lvNextExecutionTimes.getItems())
        ), null);
    }
}