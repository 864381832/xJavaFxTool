package com.xwintop.xTransfer.datasource.service.impl;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;
import com.xwintop.xTransfer.datasource.dao.DataSourceConfigDao;
import com.xwintop.xTransfer.datasource.service.DataSourceBean;
import com.xwintop.xTransfer.datasource.service.DataSourceConfigService;
import com.xwintop.xTransfer.datasource.service.DataSourceManager;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DataSourceConfigServiceImpl implements DataSourceConfigService {
    private DataSourceManager dataSourceManager = Singleton.get(DataSourceManagerImpl.class);

    private DataSourceConfigDao dataSourceConfigDao = Singleton.get(DataSourceConfigDao.class);

    @Override
    public boolean addDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception {
        if (dataSourceConfig.isEnable()) {
            this.initDataSourceConfig(dataSourceConfig);
        }
        return dataSourceConfigDao.saveAndFlush(dataSourceConfig) != null;
    }

    @Override
    public boolean updateDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception {
        dataSourceManager.closeDatasource(dataSourceConfig);
        if (dataSourceConfig.isEnable()) {
            this.initDataSourceConfig(dataSourceConfig);
        }
        dataSourceConfigDao.update(dataSourceConfig);
        return true;
    }

    @Override
    public boolean deleteDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception {
        dataSourceManager.closeDatasource(dataSourceConfig);
        dataSourceConfigDao.delete(dataSourceConfig.getId());
        return true;
    }

    @Override
    public void initDataSourceConfig(DataSourceConfig dataSourceConfig) throws Exception {
        if (!dataSourceConfig.isEnable()) {
            return;
        }
        dataSourceManager.getDataSourceBean(dataSourceConfig);
    }

    @Override
    public DataSource getDataSource(String name) throws Exception {
        return dataSourceManager.getDataSource(name);
    }
}
