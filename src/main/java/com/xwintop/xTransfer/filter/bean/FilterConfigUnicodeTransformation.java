package com.xwintop.xTransfer.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigUnicodeTransformation
 * @Description: 编码转换工具
 * @author: xufeng
 * @date: 2019/8/21 18:05
 */

@Data
public class FilterConfigUnicodeTransformation implements FilterConfig {
    private String serviceName = "filterUnicodeTransformation";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式(?!为剩余未过滤的,需放同组最后)
    private String fileNameFilterRegexGroup;//文件名过滤正则表达式分组

    private String oldEncoding = null;//原文件编码（null为保持输入端编码，AUTO为自动识别文件编码）
    private String newEncoding = null;//转换后编码（null为不转换）
    private Map args = new HashMap();//自定义参数
}
