package com.xwintop.xTransfer.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigGroovyScript
 * @Description: 执行Groovy脚本配置
 * @author: xufeng
 * @date: 2019/5/13 15:31
 */

@Data
public class FilterConfigGroovyScript implements FilterConfig {
    private String serviceName = "filterGroovyScript";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式
    private Map args = new HashMap();//自定义参数

    private String groovyScript = null;//脚本
    private String groovyScriptFilePath = null;//脚本路径
}
