package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ReceiverConfigFs
 * @Description: Hdfs接收器配置
 * @author: xufeng
 * @date: 2019/7/5 16:21
 */

@Data
public class ReceiverConfigHdfs implements ReceiverConfig {
    private String serviceName = "receiverHdfs";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String hdfsUrl = "hdfs://localhost:9000";//hdfs服务地址
    /** 接收目录 */
    private String pathIn;
    /** 一次最大处理数 */
    private int max = 100;
    /** 文件最大大小 */
    private long maxSize = 4194304;
    /** 编码 */
    private String encoding = "utf-8";
    /** 是否删除原文件 */
    private boolean delReceiveFile = true;
    /** 文件名过滤正则表达式 */
    private String fileNameRegex;
    /** 是否扫描子目录 */
    private boolean includeSubdirectory = false;
    /** 延时过滤时间，单位为毫秒 */
    private long delayTime = 0;
    /** 过滤最小文件 */
    private long minSize;

    private Map args = new HashMap();//自定义参数
}