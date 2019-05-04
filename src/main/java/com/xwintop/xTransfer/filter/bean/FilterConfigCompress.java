package com.xwintop.xTransfer.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigCompress
 * @Description: 消息压缩操作配置
 * @author: xufeng
 * @date: 2018/6/26 14:28
 */

@Data
public class FilterConfigCompress implements FilterConfig {
    private String serviceName = "filterCompress";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = false;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private boolean isAddPostfixName = true;//是否添加压缩后缀名
    private String method = "zip";//压缩类型
    private Map args = new HashMap();
}
