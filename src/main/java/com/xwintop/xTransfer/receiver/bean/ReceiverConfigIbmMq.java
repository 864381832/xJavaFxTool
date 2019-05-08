package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

/**
 * @ClassName: ReceiverConfigIbmMq
 * @Description: IbmMq接收器配置
 * @author: xufeng
 * @date: 2018/12/12 09:12
 */

@Data
public class ReceiverConfigIbmMq implements ReceiverConfig {
    private String serviceName = "receiverIbmMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String hostName;//hostName(可留空为本地localhost)
    private String channel;//channel(hostName非空时填写)
    private Integer port;//Mq连接端口(hostName非空时填写)
    private String queueManagerName;//MQ队列管理器的名称
    private String queueName;//Mq队列的名称
    private String username;//用户名
    private String password;//密码
    private Integer CCSID;//队列管理器字符集
    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
    private Integer concurrentConsumers;//初始化消费者数量
}
