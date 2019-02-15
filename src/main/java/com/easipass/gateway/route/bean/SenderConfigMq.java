package com.easipass.gateway.route.bean;

import com.easipass.gateway.route.entity.SenderConfig;
import lombok.Data;

/**
 * @ClassName: SenderConfigMq
 * @Description: Mq发送配置
 * @author: xufeng
 * @date: 2018/6/11 15:01
 */

@Deprecated   //推荐使用IbmMq
@Data
public class SenderConfigMq implements SenderConfig {
    private String serviceName = "senderMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;

    private String hostName;//hostName(留空为本地localhost)
    private String channel;//channel（hostName非空时填写）
    private Integer port;//连接端口（hostName非空时填写）
    private String queueManagerName;//MQ队列管理器的名称
    private String queueName;//Mq队列的名称
    private Integer CCSID;//队列管理器字符集
    private boolean targetClient = false;//是否关闭jms标签
    private String fileNameField = "EP_GATEWAY_FILE_NAME";//文件名获取字段
}
