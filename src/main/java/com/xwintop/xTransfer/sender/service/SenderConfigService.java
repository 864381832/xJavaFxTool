package com.xwintop.xTransfer.sender.service;

import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.messaging.IContext;

/**
 * @ClassName: SenderConfigService
 * @Description: 执行发送服务接口
 * @author: xufeng
 * @date: 2018/6/13 16:05
 */

public interface SenderConfigService {
    void executeSender(TaskConfig taskConfig, IContext iContext) throws Exception;

    void stopSender(TaskConfig taskConfig) throws Exception;
}
