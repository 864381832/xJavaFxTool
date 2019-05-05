package com.xwintop.xTransfer.filter.service;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.messaging.IContext;

import java.util.Map;

/**
 * @ClassName: Filter
 * @Description: 过滤操作接口
 * @author: xufeng
 * @date: 2018/5/28 17:32
 */

public interface Filter {
    void doFilter(IContext ctx, Map params) throws Exception;

    void setFilterConfig(FilterConfig filterConfig) throws Exception;

    void destroy();//销毁对象
}