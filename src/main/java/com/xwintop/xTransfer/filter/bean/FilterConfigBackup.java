package com.xwintop.xTransfer.filter.bean;

import com.xwintop.xTransfer.filter.enums.StrategyEnum;
import lombok.Data;

/**
 * @ClassName: FilterConfigBackup
 * @Description: 消息备份操作配置
 * @author: xufeng
 * @date: 2018/5/28 16:34
 */
@Data
public class FilterConfigBackup implements FilterConfig {
    private String serviceName = "filterBackup";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    private boolean exceptionExit = true;//是否发生异常时退出任务
    private String fileNameFilterRegex;//文件名过滤正则表达式
    private String fileNameFilterRegexGroup;//文件名过滤正则表达式分组

    private String path;//备份目录
    private String tmpPath;//备份缓冲路径(留空为不使用缓冲目录)
    private boolean createPathFlag = false;//是否自动创建目录
    private String fileName;//文件名,支持变量替换。
    private String strategy = StrategyEnum.direct.name();//文件目录策略（direct、day、hour、day_hour）
    private boolean overload = false;//是否覆盖重名文件
}