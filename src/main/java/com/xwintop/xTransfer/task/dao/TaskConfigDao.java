package com.xwintop.xTransfer.task.dao;

import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.task.service.TaskConfigService;
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
 * @ClassName: TaskConfigDao
 * @Description: 任务配置管理类
 * @author: xufeng
 * @date: 2018/6/13 16:17
 */

@Service("taskConfigDao")
@Slf4j
@Data
public class TaskConfigDao {
    private final static String CONFIG_PATH = "./configuration/";
    private final static File CONFIG_DIR = new File(CONFIG_PATH);

    public final static String SUFFIX_SERVICE = "service.yml";

    private Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();//任务配置Map
    private Map<String, Long> taskConfigFileAlterMap = new ConcurrentHashMap<>();//文件更新时间map
    private Map<String, Map<String, TaskConfig>> taskConfigFileMap = new ConcurrentHashMap<>();//任务对应配置文件map

    public TaskConfig saveAndFlush(TaskConfig taskConfig) {
        return taskConfigMap.put(taskConfig.getName(), taskConfig);
    }

    public void saveAll(Iterable<TaskConfig> entities) {
        for (TaskConfig entity : entities) {
            saveAndFlush(entity);
        }
    }

    public void update(TaskConfig taskConfig) {
        taskConfigMap.replace(taskConfig.getName(), taskConfig);
    }

    public void delete(Class<TaskConfig> taskConfigClass, List<String> idList) {
        for (String key : idList) {
            taskConfigMap.remove(key);
        }
    }

    public void delete(String key) {
        taskConfigMap.remove(key);
    }

    public List<TaskConfig> findAll() {
        return new ArrayList<>(taskConfigMap.values());
    }

    public List<TaskConfig> findAllById(List<String> idList) {
        List<TaskConfig> taskConfigList = new ArrayList<>();
        for (String key : idList) {
            taskConfigList.add(taskConfigMap.get(key));
        }
        return taskConfigList;
    }

    public TaskConfig getOne(String id) {
        return taskConfigMap.get(id);
    }

    public void loadConfigsFromFs() throws Exception {
        if (!CONFIG_DIR.exists()) {
            if(!CONFIG_DIR.mkdir()){
                throw new IOException("Config path does not exist.");
            }
        }
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
        Object config = yaml.load(new FileInputStream(file));
        Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
        if (config instanceof List) {
            log.info("加载配置是List" + config.toString());
            Iterable<TaskConfig> taskConfigs = (Iterable<TaskConfig>) config;
            this.saveAll(taskConfigs);
            for (TaskConfig taskConfig : taskConfigs) {
                taskConfigMap.put(taskConfig.getName(), taskConfig);
            }
        } else {
            TaskConfig taskConfig = (TaskConfig) config;
            this.saveAndFlush(taskConfig);
            taskConfigMap.put(taskConfig.getName(), taskConfig);
            log.info("加载配置是config" + config.toString());
        }
        taskConfigFileMap.put(file.getAbsolutePath(), taskConfigMap);
        taskConfigFileAlterMap.put(file.getAbsolutePath(), file.lastModified());
    }

    public void reloadTaskConfigFromFs(TaskConfigService taskConfigService) throws Exception {
        File[] all = CONFIG_DIR.listFiles((dir, name) -> {
            if (name.endsWith(SUFFIX_SERVICE)) {
                return true;
            }
            return false;
        });
        Set<String> deleteFileNameList = new HashSet<>(taskConfigFileAlterMap.keySet());
        for (File file : all) {
            deleteFileNameList.remove(file.getAbsolutePath());
        }
        for (String key : deleteFileNameList) {
            for (TaskConfig taskConfig : taskConfigFileMap.get(key).values()) {
                taskConfigService.deleteTaskConfig(taskConfig);
                log.info("删除任务：" + taskConfig.getName());
            }
            taskConfigFileMap.remove(key);
            taskConfigFileAlterMap.remove(key);
        }
        for (File file : all) {
            try {
                String fileName = file.getAbsolutePath();
                if (taskConfigFileAlterMap.containsKey(fileName)) {
                    if (taskConfigFileAlterMap.get(fileName) == file.lastModified()) {
                        continue;
                    }
                }
                Map<String, TaskConfig> taskConfigMap = new ConcurrentHashMap<>();
                Yaml yaml = new Yaml();
                FileInputStream fileInputStream = new FileInputStream(file);
                Object config = yaml.load(fileInputStream);
                fileInputStream.close();
                if (config instanceof List) {
                    log.info("更新加载配置是List" + config.toString());
                    Iterable<TaskConfig> configs = (Iterable<TaskConfig>) config;
                    for (TaskConfig taskConfig : configs) {
                        taskConfigMap.put(taskConfig.getName(), taskConfig);
                    }
                } else {
                    log.info("更新加载配置是config" + config.toString());
                    taskConfigMap.put(((TaskConfig) config).getName(), (TaskConfig) config);
                }
                for (TaskConfig taskConfig : taskConfigMap.values()) {
                    if (taskConfigFileMap.containsKey(fileName) && taskConfigFileMap.get(fileName).containsKey(taskConfig.getName())) {
                        taskConfigService.updateTaskConfig(taskConfig);
                    } else {
                        taskConfigService.addTaskConfig(taskConfig);
                    }
                }
                if (taskConfigFileMap.containsKey(fileName)) {
                    taskConfigFileMap.get(fileName).forEach((key, taskConfig) -> {
                        if (!taskConfigMap.containsKey(key)) {
                            try {
                                taskConfigService.deleteTaskConfig(taskConfig);
                                log.info("删除任务：" + taskConfig.getName());
                            } catch (Exception e) {
                                log.error("删除任务失败：", e.getMessage());
                            }
                        }
                    });
                }
                taskConfigFileMap.put(fileName, taskConfigMap);
                taskConfigFileAlterMap.put(fileName, file.lastModified());
            } catch (Exception e) {
                log.error("reload config [" + file.getPath() + "] error.", e);
            }
        }
        log.debug("更新配置文件检查完成。");
    }
}
