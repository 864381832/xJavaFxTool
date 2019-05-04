package com.xwintop.xTransfer.datasource.service.impl;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import com.xwintop.xTransfer.datasource.service.DataSourceBean;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
public class DataSourceBeanDruidImpl implements DataSourceBean {
    private DataSourceConfigDruid dataSourceConfigDruid;

    private DruidDataSource dataSource;

    @Override
    public void initDataSource() throws Exception {
        if (dataSource == null) {
            dataSource = new DruidDataSource();
            dataSource.setUrl(dataSourceConfigDruid.getUrl());
            dataSource.setUsername(dataSourceConfigDruid.getUsername());
            if (dataSourceConfigDruid.isDecrypt()) {
                dataSource.setPassword(ConfigTools.decrypt(dataSourceConfigDruid.getPublicKey(), dataSourceConfigDruid.getPassword()));
            } else {
                dataSource.setPassword(dataSourceConfigDruid.getPassword());
            }
            dataSource.setDriverClassName(dataSourceConfigDruid.getDriverClassName());
            //configuration
            dataSource.setInitialSize(dataSourceConfigDruid.getInitialSize());
            dataSource.setMinIdle(dataSourceConfigDruid.getMinIdle());
            dataSource.setMaxActive(dataSourceConfigDruid.getMaxActive());
            dataSource.setMaxWait(dataSourceConfigDruid.getMaxWait());
            dataSource.setTimeBetweenEvictionRunsMillis(dataSourceConfigDruid.getTimeBetweenEvictionRunsMillis());
            dataSource.setMinEvictableIdleTimeMillis(dataSourceConfigDruid.getMinEvictableIdleTimeMillis());
            dataSource.setValidationQuery(dataSourceConfigDruid.getValidationQuery());
            dataSource.setTestWhileIdle(dataSourceConfigDruid.isTestWhileIdle());
            dataSource.setTestOnBorrow(dataSourceConfigDruid.isTestOnBorrow());
            dataSource.setTestOnReturn(dataSourceConfigDruid.isTestOnReturn());
            dataSource.setPoolPreparedStatements(dataSourceConfigDruid.isPoolPreparedStatements());
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(dataSourceConfigDruid.getMaxPoolPreparedStatementPerConnectionSize());
            try {
                dataSource.setFilters(dataSourceConfigDruid.getFilters());
            } catch (SQLException e) {
                System.err.println("druid configuration initialization filter: " + e);
            }
            dataSource.setConnectionProperties(dataSourceConfigDruid.getConnectionProperties());
//            new JdbcTemplate(dataSource).execute(dataSourceConfigDruid.getValidationQuery());
        }
    }

    @Override
    public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfigDruid = (DataSourceConfigDruid) dataSourceConfig;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void destroy() {
        if (dataSource != null) {
            try {
                dataSource.getConnection().close();
                dataSource = null;
            } catch (SQLException e) {
                log.error("关闭连接异常：", e);
            }
        }
    }
}
