package com.xwintop.xTransfer.task.service;

import com.xwintop.xTransfer.task.entity.TaskConfig;

import java.util.List;

public interface TaskConfigService {
    public boolean addTaskConfig(TaskConfig taskConfig) throws Exception;

    public boolean updateTaskConfig(TaskConfig taskConfig) throws Exception;

    public boolean deleteTaskConfigs(List<String> idList) throws Exception;

    public boolean deleteTaskConfig(TaskConfig taskConfig) throws Exception;

    public List<TaskConfig> queryDefaultConfig() throws Exception;

    /**
     * 执行任务
     * @param idList TaskId集合
     * @return 是否执行成功
     * @throws Exception
     */
    public boolean executeTask(List<String> idList) throws Exception;
    public boolean executeTask(String... ids) throws Exception;

    /**
     * 手动执行任务
     * @param id
     * @return
     * @throws Exception
     */
    public boolean runTask(String id) throws Exception;

}
