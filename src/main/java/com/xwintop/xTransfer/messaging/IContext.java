package com.xwintop.xTransfer.messaging;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IContext
 * @Description: 信息流传递上下文
 * @author: xufeng
 * @date: 2018/6/15 15:16
 */

public interface IContext {

    void setMessage(IMessage iMessage);

    void addMessage(IMessage iMessage);

    void setMessages(List<IMessage> iMessages);

    List<IMessage> getMessages();

    Object get(Object key);

    void put(Object key, Object value);

    Map getMap();
}
