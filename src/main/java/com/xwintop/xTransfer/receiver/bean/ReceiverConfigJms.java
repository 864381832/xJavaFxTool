package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

/**
 * @ClassName: ReceiverConfigJms
 * @Description: Jms接收器配置
 * @author: xufeng
 * @date: 2019/1/17 16:23
 */


@Data
public class ReceiverConfigJms implements ReceiverConfig {
    private String serviceName = "receiverJms";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String jmsJndiFactory;
    private String jmsFactory;
    private String jmsQueue;
    private String jmsUrl ;
    private String jmsMessage = "JMS_MESSAGE";

    private boolean useJms102 = false;
    private int sessionSize = 0;// no cache, or only one

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
}
