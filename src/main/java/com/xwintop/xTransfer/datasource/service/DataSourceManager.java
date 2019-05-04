package com.xwintop.xTransfer.datasource.service;

import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;

import javax.sql.DataSource;

public interface DataSourceManager {
    DataSourceBean getDataSourceBean(DataSourceConfig dataSourceConfig) throws Exception;

    DataSource getDataSource(String name) throws Exception;

    void closeDatasource(DataSourceConfig dataSourceConfig) throws Exception;
}
