package com.xwintop.xTransfer.task.quartz.enums;

/**
 * @ClassName: QuartzTypeEnum
 * @Description: quartz表达式类型
 * @author: xufeng
 * @date: 2018/3/26 14:40
 */
public enum QuartzTypeEnum {
    SIMPLE //simple 简单表达式
    , CRON; //cron Cron表达式

    public static QuartzTypeEnum getEnum(String name) {
        return QuartzTypeEnum.valueOf(name.toUpperCase());
    }
}
