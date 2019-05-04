package com.xwintop.xTransfer.receiver.bean;

import java.io.Serializable;

/**
 * @ClassName: ReceiverConfig
 * @Description: 接收器配置
 * @author: xufeng
 * @date: 2018/5/28 16:32
 */
public interface ReceiverConfig extends Serializable {
    String getServiceName();
    String getId();
    void setId(String id);
    boolean isEnable();//是否开启
    boolean isAsync();//是否异步执行
    boolean isExceptionExit();//是否发生异常时退出任务
}
