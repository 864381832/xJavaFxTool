package com.xwintop.xTransfer.sender.service.impl;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.common.ExceptionMsgBackup;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.sender.service.SenderConfigService;
import com.xwintop.xTransfer.sender.service.SenderManager;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @ClassName: SenderConfigServiceImpl
 * @Description: 执行发送服务实现类
 * @author: xufeng
 * @date: 2018/6/13 16:06
 */

@Service("senderConfigService")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SenderConfigServiceImpl implements SenderConfigService {
    private SenderManager senderManager = Singleton.get(SenderManagerImpl.class);

    private Map<String, Sender> cache = new ConcurrentHashMap<>();

    @Override
    public void executeSender(TaskConfig taskConfig, IContext iContext) throws Exception {
        List<SenderConfig> senderConfigList = taskConfig.getSenderConfig();
        if (senderConfigList == null || senderConfigList.isEmpty()) {
            return;
        }
        ThreadPoolTaskExecutor executor = null;
        int senderIndex = 0;
        Map<String, Object> params = new HashMap<>();
        params.put(TaskQuartzJob.JOBID, taskConfig.getName());
        params.put(TaskQuartzJob.JOBSEQ, taskConfig.getProperty(TaskQuartzJob.JOBSEQ));
        for (SenderConfig senderConfig : senderConfigList) {
            if (StringUtils.isBlank(senderConfig.getId())) {
                senderConfig.setId(taskConfig.getName() + "_" + senderIndex);
            } else {
                if (!senderConfig.getId().startsWith(taskConfig.getName())) {
                    senderConfig.setId(taskConfig.getName() + "_" + senderConfig.getId());
                }
            }
            senderIndex++;
            if (!senderConfig.isEnable()) {
                continue;
            }
            try {
                if (senderConfig.isAsync()) {//异步线程执行
                    if (executor == null) {
                        executor = new ThreadPoolTaskExecutor();
                        executor.initialize();
                    }
                    Future future = executor.submit((Callable<Object>) () -> {
                        SenderConfigServiceImpl.this.send(senderConfig, iContext, params);
                        return true;
                    });
                    future.get();
                } else {
                    this.send(senderConfig, iContext, params);
                }
            } catch (Exception e) {
                log.error("executeSender异常:", e);
                this.destroySender(senderConfig.getId());
                ExceptionMsgBackup.msgBackup(senderConfig.getId(), iContext);
                if (senderConfig.isExceptionExit()) {//发生异常时退出任务
                    throw e;
                }
            }
        }
    }

    private void send(SenderConfig senderConfig, IContext iContext, Map params) throws Exception {
        for (IMessage iMessage : iContext.getMessages()) {
            Sender sender = cache.get(senderConfig.getId());
            if (sender == null) {
                sender = senderManager.getSender(senderConfig);
                if (sender == null) {
                    throw new Exception("未找到对应的Sender：" + senderConfig.toString());
                }
                sender.setSenderConfig(senderConfig);
                cache.put(senderConfig.getId(), sender);
            }
            if (StringUtils.isNotBlank(senderConfig.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(senderConfig.getFileNameFilterRegex())) {
                    log.info("Sender:" + senderConfig.getId() + "跳过fileName：" + iMessage.getFileName());
                    continue;
                }
            }
            sender.send(iMessage, params);
        }
    }

    @Override
    public void stopSender(TaskConfig taskConfig) throws Exception {
        int senderIndex = 0;
        for (SenderConfig senderConfig : taskConfig.getSenderConfig()) {
            if (StringUtils.isBlank(senderConfig.getId())) {
                senderConfig.setId(taskConfig.getName() + "_" + senderIndex);
            } else {
                if (!senderConfig.getId().startsWith(taskConfig.getName())) {
                    senderConfig.setId(taskConfig.getName() + "_" + senderConfig.getId());
                }
            }
            senderIndex++;
            this.destroySender(senderConfig.getId());
            cache.remove(senderConfig.getId());
        }
    }

    private void destroySender(String senderId) throws Exception {
        Sender sender = cache.get(senderId);
        if (sender != null) {
            sender.destroy();
        }
    }
}
