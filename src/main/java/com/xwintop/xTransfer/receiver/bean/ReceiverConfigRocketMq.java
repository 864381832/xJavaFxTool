package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

/**
 * @ClassName: ReceiverConfigRocketMq
 * @Description: RocketMq接收器配置
 * @author: xufeng
 * @date: 2018/12/28 10:48
 */

@Data
public class ReceiverConfigRocketMq implements ReceiverConfig {
    private String serviceName = "receiverRocketMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String namesrvAddr = "localhost:9876";
    private String groupName = "EPMS-GATEWAY";
    private String topic;//topic列表
    private String tags = "";//tags标签

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
}
