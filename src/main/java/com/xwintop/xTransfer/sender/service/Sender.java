package com.xwintop.xTransfer.sender.service;

import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.sender.bean.SenderConfig;

import java.util.Map;

/**
 * @ClassName: Sender
 * @Description: 发送服务接口
 * @author: xufeng
 * @date: 2018/6/13 16:05
 */

public interface Sender {
    Boolean send(IMessage msg, Map params) throws Exception;

    void setSenderConfig(SenderConfig senderConfig) throws Exception;

    void destroy() throws Exception;
}
