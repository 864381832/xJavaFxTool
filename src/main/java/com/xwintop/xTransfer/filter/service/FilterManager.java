package com.xwintop.xTransfer.filter.service;

import com.xwintop.xTransfer.filter.bean.FilterConfig;

/**
 * @ClassName: FilterManager
 * @Description: Filter管理类
 * @author: xufeng
 * @date: 2018/6/13 16:15
 */

public interface FilterManager {
    Filter getFilter(FilterConfig filterConfig);
}
