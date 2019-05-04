package com.xwintop.xTransfer.datasource.service.impl;

import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import com.xwintop.xTransfer.datasource.service.DataSourceBean;
import com.xwintop.xTransfer.datasource.service.DataSourceManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceManagerImpl implements DataSourceManager {
    private Map<String, DataSourceBean> cache = new ConcurrentHashMap<>();

    @Override
    public DataSourceBean getDataSourceBean(DataSourceConfig dataSourceConfig) throws Exception {
        DataSourceBean dataSource = cache.get(dataSourceConfig.getId());
        if (dataSource == null) {
            dataSource = this.getNewDataSourceBean(dataSourceConfig);
            if (dataSource == null) {
                throw new Exception("未找到对应的dataSource：" + dataSourceConfig.toString());
            }
            dataSource.setDataSourceConfig(dataSourceConfig);
            dataSource.initDataSource();
            cache.put(dataSourceConfig.getId(), dataSource);
        }
        return dataSource;
    }

    @Override
    public DataSource getDataSource(String name) throws Exception {
        DataSourceBean dataSource = cache.get(name);
        if (dataSource != null) {
            return dataSource.getDataSource();
        }
        return null;
    }

    public DataSourceBean getNewDataSourceBean(DataSourceConfig dataSourceConfig) {
        DataSourceBean dataSource = null;
        if (dataSourceConfig instanceof DataSourceConfigDruid) {
            dataSource = new DataSourceBeanDruidImpl();
        }
        return dataSource;
    }

    @Override
    public void closeDatasource(DataSourceConfig dataSourceConfig) throws Exception {
        DataSourceBean dataSource = cache.get(dataSourceConfig.getId());
        if (dataSource != null) {
            dataSource.destroy();
            cache.remove(dataSourceConfig.getId());
        }
    }
}
