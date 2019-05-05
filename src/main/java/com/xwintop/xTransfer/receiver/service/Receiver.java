package com.xwintop.xTransfer.receiver.service;

import com.xwintop.xTransfer.messaging.MessageHandler;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;

import java.util.Map;

/**
 * @ClassName: Receiver
 * @Description: 消息收取服务接口
 * @author: xufeng
 * @date: 2018/6/13 16:10
 */

public interface Receiver {
    void receive(Map params) throws Exception;

    void setReceiverConfig(ReceiverConfig receiverConfig);

    void setMessageHandler(MessageHandler messageHandler);

    void destroy();//销毁对象
}
