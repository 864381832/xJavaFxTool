package com.xwintop.xTransfer.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigDecompress
 * @Description: 消息解压操作配置
 * @author: xufeng
 * @date: 2018/5/29 16:38
 */
@Data
public class FilterConfigDecompress implements FilterConfig {
    private String serviceName = "filterDecompress";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = false;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String method = "zip";//解压类型
    private String encoding;      //文件编码
    private Map args = new HashMap();
}
