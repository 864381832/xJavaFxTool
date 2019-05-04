package com.xwintop.xTransfer.receiver.bean;

import com.xwintop.xTransfer.filter.bean.FilterConfigBackup;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReceiverConfigFsSplit
 * @Description: Fs接收器大文件拆分操作配置接收器配置
 * @author: xufeng
 * @date: 2018/9/4 17:19
 */

@Data
public class ReceiverConfigFsSplit implements ReceiverConfig {
    private String serviceName = "receiverFsSplit";//对应服务名称
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
    /** 拆分结关时一批发送多少行 */
    private int defaultRow = 100;
    /** 备份大报文配置 */
    private List<FilterConfigBackup> filterConfigBackupList = new ArrayList<>();
}