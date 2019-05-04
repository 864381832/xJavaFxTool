package com.xwintop.xTransfer.receiver.bean;

import com.xwintop.xTransfer.sender.enums.EmailMethod;
import lombok.Data;

/**
 * @ClassName: ReceiverConfigEmail
 * @Description: Email接收器配置
 * @author: xufeng
 * @date: 2018/4/9 11:25
 */
@Data
public class ReceiverConfigEmail implements ReceiverConfig {
    private String serviceName = "receiverEmail";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private int max = 100;     //接收数量。一次从邮件服务器上接收邮件的最大数量
    private String host;       //邮件服务器(IP或邮件服务器的域名)
    private int port = 110;    //端口。接收邮件使用的端口，默认值为：110
    private String user;       //用户名。邮件帐号
    private String password;   //密码，帐号密码
    private String excPath;    //异常路径。存放接收邮件时发生异常时的文件目录
    private String procotol = "pop3";   //协议。接收邮件协议
    private String receiveType = EmailMethod.attachment.name(); //接收方式。接收邮件方式
}
