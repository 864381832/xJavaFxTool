package com.xwintop.xTransfer.receiver.service.impl;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.receiver.service.ReceiverConfigService;
import com.xwintop.xTransfer.receiver.service.ReceiverManager;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @ClassName: ReceiverConfigServiceImpl
 * @Description: 接收器配置服务
 * @author: xufeng
 * @date: 2018/3/14 17:16
 */

@Service("receiverConfigService")
@Slf4j
public class ReceiverConfigServiceImpl implements ReceiverConfigService {
    @Autowired
    private ReceiverManager receiverManager = Singleton.get(ReceiverManagerImpl.class);

    private Map<String, Receiver> cache = new ConcurrentHashMap<>();

    @Override
    public void executeReceiver(TaskConfig taskConfig, MessageHandler messageHandler) throws Exception {
        List<ReceiverConfig> receiverConfigList = taskConfig.getReceiverConfig();
        ThreadPoolTaskExecutor executor = null;
        int receiverIndex = 0;
        Map<String, Object> params = new HashMap<>();
        params.put(TaskQuartzJob.JOBID, taskConfig.getName());
        params.put(TaskQuartzJob.JOBSEQ, taskConfig.getProperty(TaskQuartzJob.JOBSEQ));
        if (receiverConfigList.size() == 0) {
            IMessage msg = new DefaultMessage();
            IContext ctx = new DefaultContext();
            ctx.setMessage(msg);
            log.info("执行receiver为空：" + taskConfig.getName());
            messageHandler.handle(ctx);
            return;
        }
        for (ReceiverConfig receiverConfig : receiverConfigList) {
            if (StringUtils.isBlank(receiverConfig.getId())) {
                receiverConfig.setId(taskConfig.getName() + "_" + receiverIndex);
            } else {
                if (!receiverConfig.getId().startsWith(taskConfig.getName())) {
                    receiverConfig.setId(taskConfig.getName() + "_" + receiverConfig.getId());
                }
            }
            receiverIndex++;
            if (!receiverConfig.isEnable()) {
                continue;
            }
            try {
                if (receiverConfig.isAsync()) {//异步线程执行
                    if (executor == null) {
                        executor = new ThreadPoolTaskExecutor();
                        executor.initialize();
                    }
                    Future future = executor.submit((Callable<Object>) () -> {
                        ReceiverConfigServiceImpl.this.receive(receiverConfig, messageHandler, params);
                        return true;
                    });
                    future.get();
                } else {
                    this.receive(receiverConfig, messageHandler, params);
                }
            } catch (Exception e) {
                log.error("receiver异常:", e);
                this.destroyReceiver(receiverConfig.getId());
                if (receiverConfig.isExceptionExit()) {//发生异常时退出任务
                    throw e;
                }
            }
        }
    }

    private void receive(ReceiverConfig receiverConfig, MessageHandler messageHandler, Map params) throws Exception {
        Receiver receiver = cache.get(receiverConfig.getId());
        if (receiver == null) {
            receiver = receiverManager.getReceiver(receiverConfig);
            if (receiver == null) {
                throw new Exception("未找到对应的Receiver：" + receiverConfig.toString());
            }
            receiver.setReceiverConfig(receiverConfig);
            receiver.setMessageHandler(messageHandler);
            cache.put(receiverConfig.getId(), receiver);
        }
        receiver.receive(params);
    }

    @Override
    public void stopReceiver(TaskConfig taskConfig) {
        int receiverIndex = 0;
        for (ReceiverConfig receiverConfig : taskConfig.getReceiverConfig()) {
            if (StringUtils.isBlank(receiverConfig.getId())) {
                receiverConfig.setId(taskConfig.getName() + "_" + receiverIndex);
            } else {
                if (!receiverConfig.getId().startsWith(taskConfig.getName())) {
                    receiverConfig.setId(taskConfig.getName() + "_" + receiverConfig.getId());
                }
            }
            receiverIndex++;
            this.destroyReceiver(receiverConfig.getId());
            cache.remove(receiverConfig.getId());
        }
    }

    private void destroyReceiver(String receiverId) {
        Receiver receiver = cache.get(receiverId);
        if (receiver != null) {
            receiver.destroy();
        }
    }
}
