package com.xwintop.xTransfer.messaging;

/**
 * @ClassName: MessageHandler
 * @Description: 传输消息回调接口
 * @author: xufeng
 * @date: 2018/6/13 16:14
 */

public interface MessageHandler {
    void handle(IContext ctx) throws Exception;
}
