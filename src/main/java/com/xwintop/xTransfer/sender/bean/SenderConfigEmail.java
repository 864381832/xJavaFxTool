package com.xwintop.xTransfer.sender.bean;

import com.xwintop.xTransfer.sender.enums.EmailMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName: SenderConfigEmail
 * @Description: Email发送配置
 * @author: xufeng
 * @date: 2018/4/11 11:05
 */

@Getter
@Setter
@ToString
public class SenderConfigEmail implements SenderConfig {
    private String serviceName = "senderEmail";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String host; //服务器。邮件服务器。支持变量替换。
    private int port = 25;//端口。发送邮件使用的端口，默认值为：25。
    private String user;//用户名。邮件帐号。
    private String password;//密码，帐号密码。

    private String from;//发件人。发送邮件方地址,支持变量替换。
    private String to;//收件人。接收方邮件地址,支持变量替换。
    private String subject;//主题,邮件主题,支持变量替换。
    private String protocol;//协议。发送邮件协议。目前只有smtp可以选择。
    private String method = EmailMethod.attachment.name();//发送类型
    private String fileName;//文件名,发送方式为附件时的文件名。支持变量替换。
    private String encoding = "utf-8";//编码。详细说明请参考[wiki:EpmsBook/SenderFs 文件发送器]的配置中的Encoding说明。
}
