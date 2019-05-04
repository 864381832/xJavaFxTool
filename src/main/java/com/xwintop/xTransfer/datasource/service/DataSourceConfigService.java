package com.xwintop.xTransfer.datasource.service;

import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;

import javax.sql.DataSource;

public interface DataSourceConfigService {
    boolean addDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception;

    boolean updateDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception;

    boolean deleteDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception;
    
    void initDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception;

    DataSource getDataSource(String name) throws Exception;
}
