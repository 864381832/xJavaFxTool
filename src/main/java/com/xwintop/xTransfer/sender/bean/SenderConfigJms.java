package com.xwintop.xTransfer.sender.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SenderConfigJms
 * @Description: Jms发送配置
 * @author: xufeng
 * @date: 2019/2/13 14:31
 */

@Data
public class SenderConfigJms implements SenderConfig {
    private String serviceName = "senderJms";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String jmsJndiFactory;
    private String jmsFactory;
    private String jmsQueue;
    private String jmsUrl ;
    private boolean targetClient = false;//是否关闭jms标签

    private int sessionCacheSize = 0;

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
    private Map args = new HashMap();//扩展参数
}
