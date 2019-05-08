package com.xwintop.xTransfer.receiver.bean;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ReceiverConfigHttp
 * @Description: Http接收器配置
 * @author: xufeng
 * @date: 2019/2/25 9:57
 */

@Data
public class ReceiverConfigHttp implements ReceiverConfig {
    private String serviceName = "receiverHttp";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

//    private int max = 100;     //接收数量。一次从http服务器上接收的最大数量
    private String url;       //http请求地址
    private String httpMethod = HttpMethod.GET.name(); //接收方式。http请求方式
    private String contentType = "application/problem+json;charset=UTF-8"; //contentType
    private boolean isController = false;
    private String fileNameField = "X_TRANSFER_FILE_NAME";//文件名获取字段
    private Map args = new HashMap();//自定义参数
}
