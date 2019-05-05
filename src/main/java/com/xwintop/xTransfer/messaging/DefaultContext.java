package com.xwintop.xTransfer.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @ClassName: DefaultContext
 * @Description: 信息流传递上下文实现类
 * @author: xufeng
 * @date: 2018/6/15 16:24
 */

public class DefaultContext implements IContext {
    protected List<IMessage> iMessageList = new ArrayList<>();
    protected HashMap map = new HashMap();

    @Override
    public void setMessage(IMessage iMessage) {
        iMessageList.clear();
        iMessageList.add(iMessage);
    }

    @Override
    public void addMessage(IMessage iMessage) {
        iMessageList.add(iMessage);
    }

    @Override
    public void setMessages(List<IMessage> iMessages) {
        this.iMessageList = iMessages;
    }

    @Override
    public List<IMessage> getMessages() {
        return iMessageList;
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        map.put(key, value);
    }

    @Override
    public Map getMap() {
        return map;
    }
}
