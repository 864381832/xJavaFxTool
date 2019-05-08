package com.xwintop.xTransfer.sender.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SenderConfigActiveMq
 * @Description: ActiveMq发送配置
 * @author: xufeng
 * @date: 2019/5/8 13:19
 */

@Data
public class SenderConfigActiveMq implements SenderConfig {
    private String serviceName = "senderActiveMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String hostName;//hostName(留空为本地localhost)
    private Integer port;//连接端口（hostName非空时填写）
    private String queueName;//Mq队列的名称
    private String username;//用户名
    private String password;//密码

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
    private Map args = new HashMap();//扩展参数
}
