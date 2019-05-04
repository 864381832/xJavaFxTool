package com.xwintop.xTransfer.task.entity;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.task.quartz.enums.QuartzTypeEnum;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TaskConfig
 * @Description: 任务配置
 * @author: xufeng
 * @date: 2018/6/13 16:17
 */
@Data
public class TaskConfig implements Serializable {
    private String name;//任务名称(唯一标识，不可重复)
    private Boolean isEnable = true;//是否开启
    private String taskType;//任务类型(script/receiver/execute flow)
    private String triggerType = QuartzTypeEnum.SIMPLE.name();//触发器类型(simple/cron)
    private Integer intervalTime = 5;//两次任务调度的间隔时间(simple类型触发器显示该信息)，单位为秒
    private Integer executeTimes = -1;//任务执行次数(simple类型触发器显示该信息，-1表示无限次)
    private String triggerCron;//任务调度的时间(cron类型触发器显示该信息)
    private Boolean isStatefulJob = false;//是否为有状态的job

    private List<ReceiverConfig> receiverConfig = new ArrayList<>();//接收器配置集合

    private List<FilterConfig> filterConfigs = new ArrayList<>();//过滤器配置集合

    private List<SenderConfig> senderConfig = new ArrayList<>();//发送器配置集合

    private Map<String, Object> properties = new HashMap();//附加配置属性

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
}
