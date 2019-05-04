package com.xwintop.xTransfer.receiver.service;

import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;

/**
 * @ClassName: ReceiverManager
 * @Description: 获取接收服务管理接口
 * @author: xufeng
 * @date: 2018/6/13 16:09
 */

public interface ReceiverManager {
    Receiver getReceiver(ReceiverConfig receiverConfig);
}
