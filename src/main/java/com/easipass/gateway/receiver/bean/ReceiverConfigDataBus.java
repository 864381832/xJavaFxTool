package com.easipass.gateway.receiver.bean;

import com.easipass.gateway.receiver.entity.ReceiverConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReceiverConfigDataBus
 * @Description: 数据总线消息接收配置
 * @author: xufeng
 * @date: 2018/6/6 10:11
 */

@Data
public class ReceiverConfigDataBus implements ReceiverConfig {
    private String serviceName = "receiverDataBus";//对应服务名称
    private String id;//如果留空则系统自动分配
    /** 是否开启 */
    private boolean enable = true;
    /** 是否异步执行 */
    private boolean async = false;
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = false;

    private String clientId;//groupId
    private List<String> topics = new ArrayList<>();//topic（可多个）
    private boolean saveSolr = false;   //是否使用solr
    private boolean saveMongodb = false;//是否使用Mongodb
}
