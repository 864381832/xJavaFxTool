package com.xwintop.xTransfer.sender.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName: SenderConfigKafka
 * @Description: Kafka发送配置
 * @author: xufeng
 * @date: 2018/4/11 11:05
 */

@Getter
@Setter
@ToString
public class SenderConfigKafka implements SenderConfig {
    private String serviceName = "senderKafka";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String servers;//kafka服务器地址
    private String topic;//topic列表

    private String acks = "all";
    private int retries = 0;
    private int batchSize = 16384;
    private int linger = 1;
    private int bufferMemory = 33554432;
}
