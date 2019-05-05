package com.xwintop.xTransfer.task.quartz;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.filter.service.FilterConfigService;
import com.xwintop.xTransfer.filter.service.impl.FilterConfigServiceImpl;
import com.xwintop.xTransfer.messaging.MessageHandler;
import com.xwintop.xTransfer.receiver.service.ReceiverConfigService;
import com.xwintop.xTransfer.receiver.service.impl.ReceiverConfigServiceImpl;
import com.xwintop.xTransfer.sender.service.SenderConfigService;
import com.xwintop.xTransfer.sender.service.impl.SenderConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.yaml.snakeyaml.Yaml;

/**
 * @ClassName: TaskQuartzJob
 * @Description: 服务执行job
 * @author: xufeng
 * @date: 2018/6/1 13:33
 */

@Slf4j
public class TaskQuartzJob implements Job {
    public static final String JOBID = "jobId";//任务名
    public static final String JOBSEQ = "jobSeq";//每次任务的唯一序号
    @Autowired
    private ReceiverConfigService receiverConfigService = Singleton.get(ReceiverConfigServiceImpl.class);

    @Autowired
    private FilterConfigService filterConfigService = Singleton.get(FilterConfigServiceImpl.class);

    @Autowired
    private SenderConfigService senderConfigService = Singleton.get(SenderConfigServiceImpl.class);

//    @Autowired
//    private TaskConfigDao taskConfigDao;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        String jobSeq = jobExecutionContext.getFireInstanceId();//每次任务的唯一序号
        log.debug("开始执行TaskQuartzJob:" + jobKey + "----" + jobSeq);
//        String taskId = jobExecutionContext.getMergedJobDataMap().getString("name");
//        TaskConfig taskConfig = taskConfigDao.getOne(taskId);
        String taskConfigString = jobExecutionContext.getMergedJobDataMap().getString("taskConfig");
        TaskConfig taskConfig = new Yaml().loadAs(taskConfigString, TaskConfig.class);
        taskConfig.setProperty(TaskQuartzJob.JOBSEQ, jobSeq);
        try {
            MessageHandler messageHandler = ctx -> {
                filterConfigService.executeFilter(taskConfig, ctx);
                senderConfigService.executeSender(taskConfig, ctx);
            };
            receiverConfigService.executeReceiver(taskConfig, messageHandler);
        } catch (Exception e) {
            log.error("执行TaskQuartzJob异常:" + e.getMessage());
        }
        log.debug("完成执行TaskQuartzJob:" + jobKey + "----" + jobSeq);
    }
}
