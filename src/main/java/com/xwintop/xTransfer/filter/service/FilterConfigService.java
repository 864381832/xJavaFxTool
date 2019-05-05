package com.xwintop.xTransfer.filter.service;

import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.messaging.IContext;

/**
 * @ClassName: FilterConfigService
 * @Description: 执行Filter服务接口
 * @author: xufeng
 * @date: 2018/6/13 16:15
 */

public interface FilterConfigService {
    void executeFilter(TaskConfig taskConfig, IContext iContext) throws Exception;

    void stopFilter(TaskConfig taskConfig) throws Exception;
}
