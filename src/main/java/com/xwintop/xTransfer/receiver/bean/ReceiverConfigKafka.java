package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReceiverConfigKafka
 * @Description: Kafka接收器配置
 * @author: xufeng
 * @date: 2018/6/6 15:03
 */

@Data
public class ReceiverConfigKafka implements ReceiverConfig {
    private String serviceName = "receiverKafka";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String servers;//kafka服务器地址
    private List<String> topics = new ArrayList<>();//topic列表
    private String clientId;//消费者分区id

//    假设每条消息最多处理一分钟（如果超出一分钟，消息队列会抛异常）
    private int sessionTimeout = 180000;
    private int maxRequestSize = 5242880;
    private int requestTimeout = 305000;
    private int pollTimeout = 5000;//获取消息时连接时间(毫秒)，尽量设置的大一些否则取不到消息
    private int maxPollRecords = 3;//一次请求返回最大消息记录数，与session.timeout.ms有关，不能够乱设
}
