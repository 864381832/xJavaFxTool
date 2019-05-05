package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.receiver.service.ReceiverManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ReceiverManagerImpl
 * @Description: 获取接收服务管理实现类
 * @author: xufeng
 * @date: 2018/6/13 16:11
 */

@Service("receiverManager")
@Slf4j
public class ReceiverManagerImpl implements ReceiverManager {
    @Override
    public Receiver getReceiver(ReceiverConfig receiverConfig) {
        Receiver receiver = null;
        try {
            String className = this.getClass().getPackage().getName() + "." + receiverConfig.getServiceName().replaceFirst("receiver", "Receiver") + "Impl";
            receiver = (Receiver) Class.forName(className).newInstance();
        } catch (Exception e) {
            log.warn("获取Receiver失败：", e);
        }
        if (receiver == null) {
            try {
                receiver = (Receiver) Class.forName(receiverConfig.getServiceName()).newInstance();
            } catch (Exception e) {
                log.warn("获取ReceiverByClassName失败：", e);
            }
        }
        return receiver;
    }
}
