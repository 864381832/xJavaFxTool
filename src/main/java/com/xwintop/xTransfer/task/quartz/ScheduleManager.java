package com.xwintop.xTransfer.task.quartz;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.task.dao.TaskConfigDao;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.filter.service.FilterConfigService;
import com.xwintop.xTransfer.filter.service.impl.FilterConfigServiceImpl;
import com.xwintop.xTransfer.task.quartz.enums.QuartzTypeEnum;
import com.xwintop.xTransfer.receiver.service.ReceiverConfigService;
import com.xwintop.xTransfer.receiver.service.impl.ReceiverConfigServiceImpl;
import com.xwintop.xTransfer.sender.service.SenderConfigService;
import com.xwintop.xTransfer.sender.service.impl.SenderConfigServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.listeners.SchedulerListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

/**
 * @ClassName: ScheduleManager
 * @Description: 调度器管理类
 * @author: xufeng
 * @date: 2018/6/13 16:13
 */

@Getter
@Setter
@Slf4j
@Service("scheduleManager")
public class ScheduleManager {
    private SchedulerFactory schedulerFactoryBean = Singleton.get(StdSchedulerFactory.class);
//    @Autowired
//    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private TaskConfigDao taskConfigDao = Singleton.get(TaskConfigDao.class);

    @Autowired
    private ReceiverConfigService receiverConfigService = Singleton.get(ReceiverConfigServiceImpl.class);

    @Autowired
    private FilterConfigService filterConfigService = Singleton.get(FilterConfigServiceImpl.class);

    @Autowired
    private SenderConfigService senderConfigService = Singleton.get(SenderConfigServiceImpl.class);

    //初始化调度任务
    public void initScheduleJob() throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getListenerManager().addSchedulerListener(new SchedulerListenerSupport() {
            @Override
            public void jobDeleted(JobKey jobKey) {
                log.debug("任务监听：jobDeleted " + jobKey.toString());
                TaskConfig taskConfig = taskConfigDao.getOne(jobKey.getName());
                if (taskConfig != null) {
                    try {
                        ScheduleManager.this.stopTask(taskConfig);
                    } catch (Exception e) {
                        log.error("停止任务异常：", e);
                    }
                }
            }

            @Override
            public void jobUnscheduled(TriggerKey triggerKey) {
                log.debug("任务监听：jobUnscheduled " + triggerKey.toString());
            }

            @Override
            public void triggerFinalized(Trigger trigger) {
                log.debug("任务监听：triggerFinalized " + trigger.getJobKey().toString());
            }
        });
    }

    public boolean scheduleJob(TaskConfig taskConfig) throws Exception {
        Trigger trigger = this.createTrigger(taskConfig);
        JobBuilder jobBuilder = JobBuilder.newJob();
        if (taskConfig.getIsStatefulJob()) {
            jobBuilder.ofType(TaskQuartzStatefulJob.class);
        } else {
            jobBuilder.ofType(TaskQuartzJob.class);
        }
//        jobBuilder.usingJobData("name", taskConfig.getName());
        jobBuilder.usingJobData("taskConfig", new Yaml().dump(taskConfig));
        JobDetail jobDetail = jobBuilder.withIdentity(taskConfig.getName(), taskConfig.getTaskType()).build();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
        return true;
    }

    public void updateScheduleJob(TaskConfig taskConfig) throws Exception {
        this.deleteJob(taskConfig);
        this.scheduleJob(taskConfig);
    }

    public void deleteJob(TaskConfig taskConfig) throws Exception {
        deleteJob(taskConfig.getName(), taskConfig.getTaskType());
//        this.stopTask(taskConfig);
    }

    public void deleteJob(String schedulerKeyName, String schedulerKeyGroup) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.unscheduleJob(new TriggerKey(schedulerKeyName, schedulerKeyGroup));
        scheduler.deleteJob(new JobKey(schedulerKeyName, schedulerKeyGroup));
    }

    public Trigger createTrigger(TaskConfig taskConfig) throws Exception {
        return createTrigger(taskConfig.getName(), taskConfig.getTaskType(), taskConfig.getTriggerType(), taskConfig.getTriggerCron(), taskConfig.getIntervalTime(), taskConfig.getExecuteTimes());
    }

    public Trigger createTrigger(String name, String group, String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        return createTrigger(name, group, QuartzTypeEnum.getEnum(quartzType), cronText, interval, repeatCount);
    }

    public Trigger createTrigger(String name, String group, QuartzTypeEnum quartzType, String cronText, int interval, int repeatCount) throws Exception {
        ScheduleBuilder scheduleBuilder = null;
        if (QuartzTypeEnum.SIMPLE.equals(quartzType)) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)// 时间间隔
                    .withRepeatCount(repeatCount);// 重复次数
        } else if (QuartzTypeEnum.CRON.equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                throw new Exception("cron表达式不能为空。");
            }
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronText);
        }
        // 描叙触发Job执行的时间触发规则,Trigger实例化一个触发器
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(name, group)// 给触发器一个名字和组名
                .startNow()// 立即执行
                .withSchedule(scheduleBuilder).build();// 产生触发器
        return trigger;
    }

    public void stopTask(TaskConfig taskConfig) throws Exception {
        receiverConfigService.stopReceiver(taskConfig);
        filterConfigService.stopFilter(taskConfig);
        senderConfigService.stopSender(taskConfig);
    }
}
