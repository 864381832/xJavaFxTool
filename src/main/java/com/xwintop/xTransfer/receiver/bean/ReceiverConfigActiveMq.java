package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

/**
 * @ClassName: ReceiverConfigActiveMq
 * @Description: ActiveMq接收器配置
 * @author: xufeng
 * @date: 2019/5/8 9:30
 */


@Data
public class ReceiverConfigActiveMq implements ReceiverConfig {
    private String serviceName = "receiverActiveMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String host = "localhost";//MQ服务器地址
    private int port = 61616;//服务端口
    private String username;//账户
    private String password;//密码
    private Integer concurrentConsumers;//初始化消费者数量
    private String queueName;//队列名称

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
}
