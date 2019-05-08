package com.xwintop.xTransfer.sender.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SenderConfigRabbitMq
 * @Description: RabbitMq发送配置
 * @author: xufeng
 * @date: 2018/4/11 11:05
 */

@Getter
@Setter
@ToString
public class SenderConfigRabbitMq implements SenderConfig {
    private String serviceName = "senderRabbitMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String host = "localhost";//RabbitMQ服务器地址
    private int port = 5672;//服务端口
    private String username;//账户
    private String password;//密码
    private String virtualHost;//虚拟主机
    private String addresses;//该方法配置多个host，在当前连接host down掉的时候会自动去重连后面的host
    private Integer requestedHeartbeat;//连接心跳时间

    private boolean publisherConfirms;//是否支持发布确认
    private boolean publisherReturns;//是否支持发布返回
    private Integer connectionTimeout;//连接超时时间

    private String exchange = "";//交换器名称
    private String topic;//topic列表

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
    private String contentType = "text/plain";//文件类型
    private Map args = new HashMap();//扩展参数
}
