package com.xwintop.xTransfer.receiver.service;

import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.messaging.MessageHandler;

/**
 * @ClassName: ReceiverConfigService
 * @Description: 执行收取消息服务接口
 * @author: xufeng
 * @date: 2018/6/13 16:09
 */

public interface ReceiverConfigService {
    void executeReceiver(TaskConfig taskConfig, MessageHandler messageHandler) throws Exception;

    void stopReceiver(TaskConfig taskConfig) throws Exception;
}
