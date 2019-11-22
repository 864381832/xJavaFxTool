package com.xwintop.xTransfer.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigChangeStr
 * @Description: 改变内容操作
 * @author: xufeng
 * @date: 2019/11/22 16:00
 */

@Data
public class FilterConfigChangeStr implements FilterConfig {
    private String serviceName = "filterChangeStr";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式(?!为剩余未过滤的,需放同组最后)
    private String fileNameFilterRegexGroup;//文件名过滤正则表达式分组

    private String encoding = null;//原文件编码（null为保持输入端编码）
    private boolean halfChinese = false;//移除乱码字符
    private String orgStr = null;//待替换字符串
    private String newStr = null;//替换后字符串
    private boolean useRegex = false;//使用正则表达式
    private Map args = new HashMap();//自定义参数
}
