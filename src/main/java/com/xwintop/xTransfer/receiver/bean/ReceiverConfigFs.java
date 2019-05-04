package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ReceiverConfigFs
 * @Description: Fs接收器配置
 * @author: xufeng
 * @date: 2018/4/9 11:25
 */
@Data
public class ReceiverConfigFs implements ReceiverConfig {
    private String serviceName = "receiverFs";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    /** 接收目录 */
    private String pathIn;
    /** 接收缓冲目录 */
    private String pathTmp;
    /** 一次最大处理数 */
    private int max = 100;
    /** 文件最大大小 */
    private long maxSize = 4194304;
    /** 编码 */
    private String encoding = "utf-8";
    /** 是否有缓冲目录 */
    private boolean hasTmpPath = true;
    /** 是否删除原文件 */
    private boolean delReceiveFile = true;
    /** 文件名后缀表达式 */
    private String fileNameRegex;
    /** 是否扫描子目录 */
    private boolean includeSubdirectory = false;
    /** 延时过滤时间，单位为毫秒 */
    private long delayTime = 0;
    /** 过滤最小文件 */
    private long minSize;
    /** 大文件保存目录 */
    private String bigFilePath;
    private Integer lastModifiedSort;//是否按时间排序接收(null、0不排序，-1升序，1，降序)

    private Map args = new HashMap();//自定义参数
}