package com.easipass.gateway.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigXibToDataBus
 * @Description: xib封套发送至数据总线
 * @author: xufeng
 * @date: 2018/8/8 19:24
 */


@Data
public class FilterConfigXibToDataBus implements FilterConfig {
    private String serviceName = "filterXibToDataBus";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private Map args = new HashMap();//自定义参数

    private String topic;
}
