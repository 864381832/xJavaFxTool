package com.xwintop.xTransfer.sender.service;

import com.xwintop.xTransfer.sender.bean.SenderConfig;

/**
 * @ClassName: SenderManager
 * @Description: 获取发送服务管理接口
 * @author: xufeng
 * @date: 2018/6/13 16:05
 */

public interface SenderManager {
    Sender getSender(SenderConfig senderConfig);
}
