package com.xwintop.xTransfer.sender.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SenderConfigRocketMq
 * @Description: RocketMq发送配置
 * @author: xufeng
 * @date: 2018/12/29 11:05
 */

@Getter
@Setter
@ToString
public class SenderConfigRocketMq implements SenderConfig {
    private String serviceName = "senderRocketMq";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String namesrvAddr = "localhost:9876";
    private String groupName = "EPMS-GATEWAY";
    private String topic;//topic列表
    private String tags = "";//tags标签

    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
    private Map args = new HashMap();//扩展参数
}
