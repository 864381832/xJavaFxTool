package com.xwintop.xTransfer.filter.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FilterConfigOracleSqlldr
 * @Description: OracleSqlldr入库配置
 * @author: xufeng
 * @date: 2019/2/14 15:39
 */

@Data
public class FilterConfigOracleSqlldr implements FilterConfig {
    private String serviceName = "filterOracleSqlldr";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式
    private Map args = new HashMap();//自定义参数

    private String tmpFilePath = "./tmpOracleSqlldrDir/sqlPath/";
    private String username;
    private String password;
    private String dbServiceName;
    private String logPath = "./tmpOracleSqlldrDir/logPath/";
    private String badPath = "./tmpOracleSqlldrDir/badPath/";
}
