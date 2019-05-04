package com.xwintop.xTransfer.sender.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SenderConfigHttp
 * @Description: Http发送配置
 * @author: xufeng
 * @date: 2019/2/25 13:45
 */

@Getter
@Setter
@ToString
public class SenderConfigHttp implements SenderConfig {
    private String serviceName = "senderHttp";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式

    private String url;       //http请求地址
    private String httpMethod = HttpMethod.GET.name(); //接收方式。http请求方式
    private String ContentType = "application/problem+json;charset=UTF-8"; //ContentType
    private Map args = new HashMap();//自定义参数
    private boolean updateMsgWithHttpResp = false;  //是否用http返回值更新原有msg数据

}
