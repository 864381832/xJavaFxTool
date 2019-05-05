package com.xwintop.xTransfer.task.service.impl;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.datasource.service.impl.DataSourceConfigServiceImpl;
import com.xwintop.xTransfer.task.dao.TaskConfigDao;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.task.quartz.ScheduleManager;
import com.xwintop.xTransfer.task.service.TaskConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service("taskConfigService")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TaskConfigServiceImpl implements TaskConfigService {
    @Autowired
    private ScheduleManager scheduleManager = Singleton.get(ScheduleManager.class);
    @Autowired
    private TaskConfigDao taskConfigDao = Singleton.get(TaskConfigDao.class);

    @Override
    public boolean addTaskConfig(TaskConfig taskConfig) throws Exception {
        if (taskConfig.getIsEnable()) {
            this.runTaskConfig(taskConfig);
        }
        return taskConfigDao.saveAndFlush(taskConfig) != null;
    }

    @Override
    public boolean updateTaskConfig(TaskConfig taskConfig) throws Exception {
        if (taskConfig.getIsEnable()) {
            scheduleManager.updateScheduleJob(taskConfig);
        } else {
            scheduleManager.deleteJob(taskConfig);
        }
        taskConfigDao.update(taskConfig);
        return true;
    }

    @Override
    public boolean deleteTaskConfigs(List<String> idList) throws Exception {
        List<TaskConfig> taskConfigList = taskConfigDao.findAllById(idList);
        for (TaskConfig taskConfig : taskConfigList) {
            this.deleteTaskConfig(taskConfig);
        }
        return true;
    }

    @Override
    public boolean deleteTaskConfig(TaskConfig taskConfig) throws Exception {
        scheduleManager.deleteJob(taskConfig);
        taskConfigDao.delete(taskConfig.getName());
        return false;
    }

    @Override
    public List<TaskConfig> queryDefaultConfig() throws Exception {
        return taskConfigDao.findAll();
    }

    @Override
    public boolean executeTask(List<String> idList) throws Exception {
        if (idList == null || idList.isEmpty()) {
            return false;
        }
        List<TaskConfig> taskConfigList = taskConfigDao.findAllById(idList);
        for (TaskConfig taskConfig : taskConfigList) {
            runTaskConfig(taskConfig);
        }
        return true;
    }

    @Override
    public boolean executeTask(String... ids) throws Exception {
        if (ids == null) {
            return false;
        }
        return executeTask(Arrays.asList(ids));
    }

    @Override
    public boolean runTask(String id) throws Exception {
        TaskConfig taskConfig = taskConfigDao.getOne(id);
        taskConfig.setName(taskConfig.getName() + System.currentTimeMillis());
        taskConfig.setTriggerType("simple");
        taskConfig.setIntervalTime(0);
        taskConfig.setExecuteTimes(0);
        return runTaskConfig(taskConfig);
    }

    private boolean runTaskConfig(TaskConfig taskConfig) throws Exception {
        if (taskConfig == null) {
            return false;
        }
        scheduleManager.scheduleJob(taskConfig);
        return true;
    }

    /**
     * 初始化TaskSchedule数据
     *
     * @throws Exception
     */
    @PostConstruct
    public void initTaskSchedule() throws Exception {
        Singleton.get(DataSourceConfigServiceImpl.class).initDataSourceConfig();
        log.debug("初始化TaskConfig开始");
        scheduleManager.initScheduleJob();
        taskConfigDao.loadConfigsFromFs();
        log.debug("初始化TaskSchedule开始");
        List<TaskConfig> taskConfigList = taskConfigDao.findAll();
        for (TaskConfig taskConfig : taskConfigList) {
            if (taskConfig.getIsEnable()) {
                this.runTaskConfig(taskConfig);
            }
        }
        log.debug("初始化TaskSchedule结束");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    taskConfigDao.reloadTaskConfigFromFs(TaskConfigServiceImpl.this);
                } catch (Exception e) {
                    log.error("加载配置文件失败：", e);
                }
            }
        }, 5000, 5000);
    }
}
