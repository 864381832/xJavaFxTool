package com.xwintop.xTransfer.filter.service.impl;

import cn.hutool.core.lang.Singleton;
import com.xwintop.xTransfer.common.ExceptionMsgBackup;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.filter.service.FilterConfigService;
import com.xwintop.xTransfer.filter.service.FilterManager;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @ClassName: FilterConfigServiceImpl
 * @Description: Filter执行操作服务实现类
 * @author: xufeng
 * @date: 2018/6/13 16:15
 */

@Service("filterConfigService")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FilterConfigServiceImpl implements FilterConfigService {
    private FilterManager filterManager = Singleton.get(FilterManagerImpl.class);

    private Map<String, Filter> cache = new ConcurrentHashMap<>();

    @Override
    public void executeFilter(TaskConfig taskConfig, IContext iContext) throws Exception {
        List<FilterConfig> filterConfigList = taskConfig.getFilterConfigs();
        if (filterConfigList == null || filterConfigList.isEmpty()) {
            return;
        }
        ThreadPoolTaskExecutor executor = null;
        int filterIndex = 0;
        Map<String, Object> params = new HashMap<>();
        params.put(TaskQuartzJob.JOBID, taskConfig.getName());
        params.put(TaskQuartzJob.JOBSEQ, taskConfig.getProperty(TaskQuartzJob.JOBSEQ));
        for (FilterConfig filterConfig : filterConfigList) {
            if (StringUtils.isBlank(filterConfig.getId())) {
                filterConfig.setId(taskConfig.getName() + "_" + filterIndex);
            } else {
                if (!filterConfig.getId().startsWith(taskConfig.getName())) {
                    filterConfig.setId(taskConfig.getName() + "_" + filterConfig.getId());
                }
            }
            filterIndex++;
            if (!filterConfig.isEnable()) {
                continue;
            }
            try {
                if (filterConfig.isAsync()) {//异步线程执行
                    if (executor == null) {
                        executor = new ThreadPoolTaskExecutor();
                        executor.initialize();
                    }
                    Future future = executor.submit((Callable<Object>) () -> {
                        this.doFilter(filterConfig, iContext, params);
                        return true;
                    });
                    future.get();
                } else {
                    this.doFilter(filterConfig, iContext, params);
                }
            } catch (Exception e) {
                log.error("executeFilter异常:", e);
                this.destroyFilter(filterConfig.getId());
                ExceptionMsgBackup.msgBackup(filterConfig.getId(), iContext);
                if (filterConfig.isExceptionExit()) {//发生异常时退出任务
                    throw e;
                }
            }

        }
    }

    private void doFilter(FilterConfig filterConfig, IContext ctx, Map params) throws Exception {
        Filter filter = cache.get(filterConfig.getId());
        if (filter == null) {
            filter = filterManager.getFilter(filterConfig);
            if (filter == null) {
                log.error("未找到对应的Filter" + filterConfig.toString());
                throw new Exception("未找到对应的Filter");
            }
            filter.setFilterConfig(filterConfig);
            cache.put(filterConfig.getId(), filter);
        }
        filter.doFilter(ctx, params);
    }

    @Override
    public void stopFilter(TaskConfig taskConfig) throws Exception {
        int filterIndex = 0;
        for (FilterConfig filterConfig : taskConfig.getFilterConfigs()) {
            if (StringUtils.isBlank(filterConfig.getId())) {
                filterConfig.setId(taskConfig.getName() + "_" + filterIndex);
            } else {
                if (!filterConfig.getId().startsWith(taskConfig.getName())) {
                    filterConfig.setId(taskConfig.getName() + "_" + filterConfig.getId());
                }
            }
            filterIndex++;
            this.destroyFilter(filterConfig.getId());
            cache.remove(filterConfig.getId());
        }
    }

    private void destroyFilter(String filterId) {
        Filter filter = cache.get(filterId);
        if (filter != null) {
            filter.destroy();
        }
    }
}
