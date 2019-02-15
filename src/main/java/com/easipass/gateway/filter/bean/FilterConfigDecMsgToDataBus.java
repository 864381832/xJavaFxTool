package com.easipass.gateway.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigDecMsgToDataBus
 * @Description: 报关单传输发送配置
 * @author: xufeng
 * @date: 2018/5/30 17:18
 */

@Data
public class FilterConfigDecMsgToDataBus implements FilterConfig {
    private String serviceName = "filterDecMsgToDataBus";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private Map args = new HashMap();//自定义参数

    private String topic;

    private String sender;//发送方
    private String receiver;//接收方
    private String appCode;//应用标识
    private String type;//消息类型标识 被包装报文的类型（xml、ediflat等），这个应该在DBus中备案。
    private String format;//消息遵循的标准体系标识。例如EDIFLAT，EDIFACT等。这个标识需要在DBus中备案。
    private String version;//封套消息格式版本(用于标识封套自己的格式版本，非被封装报文和消息的版本。当前为1.0,非必填，默认为1.0。)
    private String encoding;//需要与报文内容编码一致（留空则默认取msg中encoding）
    private String mimeType = "text/plain";//报文媒体类型

    private String plogType;//应用消息处理类型
    private String plogInfo;//应用消息处理说明
}
