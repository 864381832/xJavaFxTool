package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.filter.service.FilterManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: FilterManagerImpl
 * @Description: 获取Filter管理类
 * @author: xufeng
 * @date: 2018/6/13 16:16
 */

@Slf4j
public class FilterManagerImpl implements FilterManager {
    @Override
    public Filter getFilter(FilterConfig filterConfig) {
        Filter filter = null;
        try {
            filter = (Filter) Class.forName(this.getClass().getPackage() + "." + filterConfig.getServiceName().replaceFirst("filter", "Filter") + "Impl").newInstance();
        } catch (Exception e) {
            log.warn("获取Filter失败：", e);
        }
        if (filter == null) {
            try {
                filter = (Filter) Class.forName(filterConfig.getServiceName()).newInstance();
            } catch (Exception e) {
                log.warn("获取FilterByClassName失败：", e);
            }
        }
        return filter;
    }
}
