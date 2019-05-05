package com.xwintop.xTransfer.datasource.service.impl;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;
import com.xwintop.xTransfer.datasource.dao.DataSourceConfigDao;
import com.xwintop.xTransfer.datasource.service.DataSourceConfigService;
import com.xwintop.xTransfer.datasource.service.DataSourceManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    /**
     * 初始化DataSourceConfig数据
     */
    @PostConstruct
    public void initDataSourceConfig() throws Exception {
        log.debug("初始化DataSourceConfig开始");
        dataSourceConfigDao.loadConfigsFromFs();
        List<DataSourceConfig> dataSourceConfigList = dataSourceConfigDao.findAll();
        for (DataSourceConfig dataSourceConfig : dataSourceConfigList) {
            if (dataSourceConfig.isEnable()) {
                this.initDataSourceConfig(dataSourceConfig);
            }
        }
        log.debug("初始化DataSourceConfig结束");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    dataSourceConfigDao.reloadDataSourceConfigFromFs(DataSourceConfigServiceImpl.this);
                } catch (Exception e) {
                    log.error("加载DataSourceConfig文件失败：", e);
                }
            }
        }, 5000, 5000);
    }
}
