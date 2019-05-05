package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.sender.service.SenderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SenderManagerImpl
 * @Description: 获取发送服务管理类
 * @author: xufeng
 * @date: 2018/6/13 16:07
 */

@Service("senderManager")
@Slf4j
public class SenderManagerImpl implements SenderManager {
    @Override
    public Sender getSender(SenderConfig senderConfig) {
        Sender sender = null;
        try {
            String className = this.getClass().getPackage().getName() + "." + senderConfig.getServiceName().replaceFirst("sender", "Sender") + "Impl";
            sender = (Sender) Class.forName(className).newInstance();
        } catch (Exception e) {
            log.error("获取Sender失败：", e);
        }
        if (sender == null) {
            try {
                sender = (Sender) Class.forName(senderConfig.getServiceName()).newInstance();
            } catch (Exception e) {
                log.warn("获取SenderByClassName失败：", e);
            }
        }
        return sender;
    }
}
