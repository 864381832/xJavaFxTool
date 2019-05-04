package com.xwintop.xTransfer.filter.enums;

/**
 * @ClassName: StrategyEnum
 * @Description: 备份目录策略
 * @author: xufeng
 * @date: 2018/5/28 17:28
 */

public enum StrategyEnum {
    direct//默认目录
    , day//按天分目录
    , hour//按小时分目录
    , day_hour//按天/小时分目录
    , minutes;//按分钟分目录
}
