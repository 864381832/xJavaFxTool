package com.xwintop.xTransfer.datasource.bean;

import java.io.Serializable;
/**
 * @ClassName: DataSourceConfig
 * @Description: 数据源配置
 * @author: xufeng
 * @date: 2018/12/6 17:15
 */

public interface DataSourceConfig extends Serializable {
    String getId();
    void setId(String id);
    boolean isEnable();//是否开启
}
