package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @ClassName: ScheduleManager
 * @Description: 自动任务管理类
 * @author: xufeng
 * @date: 2018/1/25 16:35
 */

@Getter
@Setter
@Slf4j
public class ActionScheduleUtil {
    private static final String[] quartzChoiceBoxStrings = new String[]{"简单表达式", "Cron表达式"};
    private SchedulerFactory sf = new StdSchedulerFactory();
    private String schedulerKeyGroup = "x";
    private String schedulerKeyName = "x";

    private TextField cronTextField;
    private ChoiceBox<String> quartzChoiceBox;
    private Spinner<Integer> intervalSpinner;
    private Spinner<Integer> repeatCountSpinner;

    private Runnable runnable;

    //添加调度控件
    public void setScheduleNode(Pane pane) {
        quartzChoiceBox = new ChoiceBox();
        quartzChoiceBox.setPrefWidth(100);
        quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
        quartzChoiceBox.getSelectionModel().select(0);
        StackPane stackPane = new StackPane();
        cronTextField = new TextField("* * * * * ?");
        cronTextField.setVisible(false);
        stackPane.getChildren().add(cronTextField);
        HBox simpleScheduleAnchorPane = new HBox();
        simpleScheduleAnchorPane.setAlignment(Pos.CENTER_LEFT);
        simpleScheduleAnchorPane.setSpacing(5);
        stackPane.getChildren().add(simpleScheduleAnchorPane);
        intervalSpinner = new Spinner<>();
        intervalSpinner.setEditable(true);
        intervalSpinner.setPrefWidth(66);
        intervalSpinner.setTooltip(new Tooltip("单位为秒"));
        repeatCountSpinner = new Spinner<>();
        repeatCountSpinner.setEditable(true);
        repeatCountSpinner.setPrefWidth(66);
        repeatCountSpinner.setTooltip(new Tooltip("-1为永久任务"));
        simpleScheduleAnchorPane.getChildren().add(new Label("间隔"));
        simpleScheduleAnchorPane.getChildren().add(intervalSpinner);
        simpleScheduleAnchorPane.getChildren().add(new Label("次数"));
        simpleScheduleAnchorPane.getChildren().add(repeatCountSpinner);
        JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 1, Integer.MAX_VALUE);
        JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);
        quartzChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (quartzChoiceBoxStrings[0].equals(newValue)) {
                    cronTextField.setVisible(false);
                    simpleScheduleAnchorPane.setVisible(true);
                } else if (quartzChoiceBoxStrings[1].equals(newValue)) {
                    cronTextField.setVisible(true);
                    simpleScheduleAnchorPane.setVisible(false);
                }
            }
        });
        Button runQuartzButton = new Button("定时运行");
        runQuartzButton.setOnAction(event -> {
            if ("定时运行".equals(runQuartzButton.getText())) {
                try {
                    ActionScheduleUtil.this.runQuartzAction();
                    runQuartzButton.setText("停止运行");
                } catch (Exception e) {
                    log.error("运行错误！", e);
                    TooltipUtil.showToast("运行错误：" + e.getMessage());
                }
            } else {
                try {
                    ActionScheduleUtil.this.stopQuartzAction();
                    runQuartzButton.setText("定时运行");
                } catch (Exception e) {
                    log.error("停止错误！", e);
                    TooltipUtil.showToast("停止错误：" + e.getMessage());
                }
            }
        });
        pane.getChildren().add(quartzChoiceBox);
        pane.getChildren().add(stackPane);
        pane.getChildren().add(runQuartzButton);
    }

    //设置调度执行事件
    public void setJobAction(Runnable runnable) {
        this.runnable = runnable;
    }

    public void runQuartzAction() throws Exception {
//        schedulerKeyGroup = jobClass.toString();
        schedulerKeyName = schedulerKeyGroup + System.currentTimeMillis();
        JobDetail jobDetail = JobBuilder.newJob(ActionJob.class).withIdentity(schedulerKeyName, schedulerKeyGroup).build();
        jobDetail.getJobDataMap().put("runnable", runnable);
        ScheduleBuilder scheduleBuilder = null;
        if (quartzChoiceBoxStrings[0].equals(quartzChoiceBox.getValue())) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(intervalSpinner.getValue())// 时间间隔
                    .withRepeatCount(repeatCountSpinner.getValue());// 重复次数
        } else if (quartzChoiceBoxStrings[1].equals(quartzChoiceBox.getValue())) {
            if (StringUtils.isEmpty(cronTextField.getText())) {
                throw new Exception("cron表达式不能为空。");
            }
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronTextField.getText());
        }
        // 描叙触发Job执行的时间触发规则,Trigger实例化一个触发器
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(schedulerKeyName, schedulerKeyGroup)// 给触发器一个名字和组名
                .startNow()// 立即执行
                .withSchedule(scheduleBuilder).build();// 产生触发器

        // 运行容器，使用SchedulerFactory创建Scheduler实例
        Scheduler scheduler = sf.getScheduler();
        // 向Scheduler添加一个job和trigger
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    public void stopQuartzAction() throws Exception {
        Scheduler sched = sf.getScheduler();
        sched.unscheduleJob(new TriggerKey(schedulerKeyName, schedulerKeyGroup));
        sched.deleteJob(new JobKey(schedulerKeyName, schedulerKeyGroup));
    }
}
