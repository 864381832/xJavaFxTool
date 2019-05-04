package com.xwintop.xTransfer.datasource.dao;

import com.xwintop.xTransfer.datasource.bean.DataSourceConfig;
import com.xwintop.xTransfer.datasource.service.DataSourceConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: DataSourceConfigDao
 * @Description: 任务配置管理类
 * @author: xufeng
 * @date: 2018/6/13 16:17
 */

@Slf4j
@Data
public class DataSourceConfigDao {
    private final static String CONFIG_PATH = "./configuration/";
    private final static File CONFIG_DIR = new File(CONFIG_PATH);

    public final static String SUFFIX_SERVICE = "datasource.yml";

    private Map<String, DataSourceConfig> dataSourceConfigMap = new ConcurrentHashMap<>();//任务配置Map
    private Map<String, Long> dataSourceConfigFileAlterMap = new ConcurrentHashMap<>();//文件更新时间map
    private Map<String, Map<String, DataSourceConfig>> dataSourceConfigFileMap = new ConcurrentHashMap<>();//任务对应配置文件map

    public DataSourceConfig saveAndFlush(DataSourceConfig dataSourceConfig) {
        return dataSourceConfigMap.put(dataSourceConfig.getId(), dataSourceConfig);
    }

    public void saveAll(Iterable<DataSourceConfig> entities) {
        for (DataSourceConfig entity : entities) {
            saveAndFlush(entity);
        }
    }

    public void update(DataSourceConfig dataSourceConfig) {
        dataSourceConfigMap.replace(dataSourceConfig.getId(), dataSourceConfig);
    }

    public void delete(Class<DataSourceConfig> dataSourceConfigClass, List<String> idList) {
        for (String key : idList) {
            dataSourceConfigMap.remove(key);
        }
    }

    public void delete(String key) {
        dataSourceConfigMap.remove(key);
    }

    public List<DataSourceConfig> findAll() {
        return new ArrayList<>(dataSourceConfigMap.values());
    }

    public List<DataSourceConfig> findAllById(List<String> idList) {
        List<DataSourceConfig> dataSourceConfigList = new ArrayList<>();
        for (String key : idList) {
            dataSourceConfigList.add(dataSourceConfigMap.get(key));
        }
        return dataSourceConfigList;
    }

    public DataSourceConfig getOne(String id) {
        return dataSourceConfigMap.get(id);
    }

    public void loadConfigsFromFs() throws Exception {
        if (!CONFIG_DIR.exists()) {
            if(!CONFIG_DIR.mkdir()){
                throw new IOException("Config path does not exist.");
            }
        }
//        if (!CONFIG_DIR.isDirectory()) {
//            throw new IOException("CONFIG_PATH:[" + CONFIG_PATH + "] is not a Directory.");
//        }
        log.debug("Start loading all config files in " + CONFIG_DIR);
        File[] all = CONFIG_DIR.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(SUFFIX_SERVICE)) {
                    return true;
                }
                return false;
            }
        });
        for (File file : all) {
            try {
                load(file);
            } catch (Exception e) {
                log.error("load config [" + file.getPath() + "] error.", e);
            }
        }
        log.debug("All config files in " + CONFIG_DIR + " are loaded.");
    }

    public void load(File file) throws Exception {
        Yaml yaml = new Yaml();
        FileInputStream fileInputStream = new FileInputStream(file);
        Object config = yaml.load(fileInputStream);
        fileInputStream.close();
        Map<String, DataSourceConfig> dataSourceConfigMap = new ConcurrentHashMap<>();
        if (config instanceof List) {
            log.info("加载数据源配置是List" + config.toString());
            Iterable<DataSourceConfig> dataSourceConfigs = (Iterable<DataSourceConfig>) config;
            this.saveAll(dataSourceConfigs);
            for (DataSourceConfig dataSourceConfig : dataSourceConfigs) {
                dataSourceConfigMap.put(dataSourceConfig.getId(), dataSourceConfig);
            }
        } else {
            DataSourceConfig dataSourceConfig = (DataSourceConfig) config;
            this.saveAndFlush(dataSourceConfig);
            dataSourceConfigMap.put(dataSourceConfig.getId(), dataSourceConfig);
            log.info("加载数据源配置是config" + config.toString());
        }
        dataSourceConfigFileMap.put(file.getAbsolutePath(), dataSourceConfigMap);
        dataSourceConfigFileAlterMap.put(file.getAbsolutePath(), file.lastModified());
    }

    public void reloadDataSourceConfigFromFs(DataSourceConfigService dataSourceConfigService) throws Exception {
        File[] all = CONFIG_DIR.listFiles((dir, name) -> {
            if (name.endsWith(SUFFIX_SERVICE)) {
                return true;
            }
            return false;
        });
        Set<String> deleteFileNameList = new HashSet<>(dataSourceConfigFileAlterMap.keySet());
        for (File file : all) {
            deleteFileNameList.remove(file.getAbsolutePath());
        }
        for (String key : deleteFileNameList) {
            for (DataSourceConfig dataSourceConfig : dataSourceConfigFileMap.get(key).values()) {
                dataSourceConfigService.deleteDataSourceConfig(dataSourceConfig);
                log.info("删除DataSourceConfig：" + dataSourceConfig.getId());
            }
            dataSourceConfigFileMap.remove(key);
            dataSourceConfigFileAlterMap.remove(key);
        }
        for (File file : all) {
            try {
                String fileName = file.getAbsolutePath();
                if (dataSourceConfigFileAlterMap.containsKey(fileName)) {
                    if (dataSourceConfigFileAlterMap.get(fileName) == file.lastModified()) {
                        continue;
                    }
                }
                Map<String, DataSourceConfig> dataSourceConfigMap = new ConcurrentHashMap<>();
                Yaml yaml = new Yaml();
                FileInputStream fileInputStream = new FileInputStream(file);
                Object config = yaml.load(fileInputStream);
                fileInputStream.close();
                if (config instanceof List) {
                    log.info("更新加载数据源配置是List" + config.toString());
                    Iterable<DataSourceConfig> configs = (Iterable<DataSourceConfig>) config;
                    for (DataSourceConfig dataSourceConfig : configs) {
                        dataSourceConfigMap.put(dataSourceConfig.getId(), dataSourceConfig);
                    }
                } else {
                    log.info("更新加载数据源配置是config" + config.toString());
                    dataSourceConfigMap.put(((DataSourceConfig) config).getId(), (DataSourceConfig) config);
                }
                for (DataSourceConfig dataSourceConfig : dataSourceConfigMap.values()) {
                    if (dataSourceConfigFileMap.containsKey(fileName) && dataSourceConfigFileMap.get(fileName).containsKey(dataSourceConfig.getId())) {
                        dataSourceConfigService.updateDataSourceConfig(dataSourceConfig);
                    } else {
                        dataSourceConfigService.addDataSourceConfig(dataSourceConfig);
                    }
                }
                if (dataSourceConfigFileMap.containsKey(fileName)) {
                    dataSourceConfigFileMap.get(fileName).forEach((key, dataSourceConfig) -> {
                        if (!dataSourceConfigMap.containsKey(key)) {
                            try {
                                dataSourceConfigService.deleteDataSourceConfig(dataSourceConfig);
                                log.info("删除DataSourceConfig：" + dataSourceConfig.getId());
                            } catch (Exception e) {
                                log.error("删除DataSourceConfig失败：", e);
                            }
                        }
                    });
                }
                dataSourceConfigFileMap.put(fileName, dataSourceConfigMap);
                dataSourceConfigFileAlterMap.put(fileName, file.lastModified());
            } catch (Exception e) {
                log.error("reload config [" + file.getPath() + "] error.", e);
            }
        }
        log.debug("更新数据源配置文件检查完成。");
    }
}
