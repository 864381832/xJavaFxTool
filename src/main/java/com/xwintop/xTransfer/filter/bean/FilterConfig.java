package com.xwintop.xTransfer.filter.bean;

import java.io.Serializable;

/**
 * @ClassName: FilterConfig
 * @Description: 中间层过滤类配置
 * @author: xufeng
 * @date: 2018/5/28 16:32
 */

public interface FilterConfig extends Serializable {
    String getServiceName();
    String getId();
    void setId(String id);
    boolean isEnable();//是否开启
    boolean isAsync();//是否异步执行
    boolean isExceptionExit();//是否发生异常时退出任务
    String getFileNameFilterRegex();//文件名过滤正则表达式
}
