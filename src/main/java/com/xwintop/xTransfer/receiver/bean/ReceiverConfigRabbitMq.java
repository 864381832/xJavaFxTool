package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

/**
 * @ClassName: ReceiverConfigRabbitMq
 * @Description: RabbitMq接收器配置
 * @author: xufeng
 * @date: 2018/12/3 10:48
 */

@Data
public class ReceiverConfigRabbitMq implements ReceiverConfig {
    private String serviceName = "receiverRabbitMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

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
    private int prefetchCount = 3;//设置每个消费者获取的最大的消息数量
    private Integer concurrentConsumers;//初始化消费者数量
    private Integer maxConcurrentConsumers;//最大消费者数量

    private String topic;//topic列表

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
}
