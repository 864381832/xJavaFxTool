package com.xwintop.xTransfer.sender.bean;

import lombok.Data;

/**
 * @ClassName: SenderConfigHdfs
 * @Description: Hdfs发送配置
 * @author: xufeng
 * @date: 2019/7/5 17:23
 */
@Data
public class SenderConfigHdfs implements SenderConfig {
    private String serviceName = "senderHdfs";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String hdfsUrl = "hdfs://localhost:9000";//hdfs服务地址
    /** 发送目录 */
    private String path;
    private String fileName;//文件名，支持变量替换。
    /** 是否创建目录 */
    private boolean createPathFlag = false;
    /** 是否覆盖重名文件 */
    private boolean overload = false;
}
