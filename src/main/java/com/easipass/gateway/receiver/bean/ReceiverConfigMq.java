package com.easipass.gateway.receiver.bean;

import com.easipass.gateway.receiver.entity.ReceiverConfig;
import lombok.Data;

/**
 * @ClassName: ReceiverConfigMq
 * @Description: Mq接收器配置
 * @author: xufeng
 * @date: 2018/6/13 16:12
 */

@Data
@Deprecated   //推荐使用IbmMq
public class ReceiverConfigMq implements ReceiverConfig {
    private String serviceName = "receiverMq";//对应服务名称
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
    private Integer CCSID;//队列管理器字符集
    /** 一次最大处理数,小于等于0表示当前目录下有多少文件就取多少文件,默认为0 */
    private int max = 0;
    private String fileNameField = "EP_GATEWAY_FILE_NAME";//文件名获取字段
}
