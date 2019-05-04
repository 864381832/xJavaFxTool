package com.xwintop.xTransfer.filter.bean;

import com.xwintop.xTransfer.filter.enums.CryptosEnum;
import lombok.Data;

/**
 * @ClassName: FilterConfigEncryptDecrypt
 * @Description: 加密解密操作类
 * @author: xufeng
 * @date: 2018/5/30 10:44
 */
@Data
public class FilterConfigEncryptDecrypt implements FilterConfig {
    private String serviceName = "filterEncryptDecrypt";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = false;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String encoding = "utf-8";//编码
    private String cryptos = CryptosEnum.Null.name();//加解密算法
    private String actionType = "encrypt";//操作类型（encrypt、decrypt）
}
