package com.xwintop.xJavaFxTool.manager;

import com.xwintop.xcore.util.javafx.TooltipUtil;
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
public class ScheduleManager {
    private SchedulerFactory sf = new StdSchedulerFactory();
    private String schedulerKeyGroup = "x";
    private String schedulerKeyName = "x" + System.currentTimeMillis();

    public enum QuartzType {
        SIMPLE //简单表达式
        , CRON //Cron表达式
    }

    public void runQuartzAction(Class<? extends Job> jobClass, String quartzType, Object jobDataMap, String cronText, int interval, int repeatCount) throws Exception {
        if ("简单表达式".equals(quartzType)) {
            this.runQuartzAction(jobClass, jobDataMap, interval, repeatCount);
        } else if ("Cron表达式".equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                TooltipUtil.showToast("cron表达式不能为空。");
                throw new Exception("cron表达式不能为空。");
            }
            this.runQuartzAction(jobClass, jobDataMap, cronText);
        }
    }

    public void runQuartzAction(Class<? extends Job> jobClass, Object jobDataMap, int interval, int repeatCount) throws Exception {
        runQuartzAction(jobClass, jobDataMap, QuartzType.SIMPLE, null, interval, repeatCount);
    }

    public void runQuartzAction(Class<? extends Job> jobClass, Object jobDataMap, String cronText) throws Exception {
        runQuartzAction(jobClass, jobDataMap, QuartzType.CRON, cronText, 0, 0);
    }

    public void runQuartzAction(Class<? extends Job> jobClass, Object jobDataMap, QuartzType quartzType, String cronText, int interval, int repeatCount) throws Exception {
        schedulerKeyGroup = jobClass.toString();
        schedulerKeyName = schedulerKeyGroup + System.currentTimeMillis();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(schedulerKeyName, schedulerKeyGroup)
                .build();
        jobDetail.getJobDataMap().put("Service", jobDataMap);
        ScheduleBuilder scheduleBuilder = null;
        if (QuartzType.SIMPLE.equals(quartzType)) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)// 时间间隔
                    .withRepeatCount(repeatCount);// 重复次数（将执行6次）
        } else if (QuartzType.CRON.equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                throw new Exception("cron表达式不能为空。");
            }
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronText);
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
