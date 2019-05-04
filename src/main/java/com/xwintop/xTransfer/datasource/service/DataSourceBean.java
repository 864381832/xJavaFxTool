package com.xwintop.xTransfer.datasource.service;

import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;

import javax.sql.DataSource;

public interface DataSourceBean {
    void initDataSource() throws Exception;

    void setDataSourceConfig(DataSourceConfig dataSourceConfig);

    DataSource getDataSource();

    void destroy();//销毁对象
}
