package com.xwintop.xcore.commons;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @ClassName: CacheUtil
 * @Description: 缓存工具类
 * @author: xufeng
 * @date: 2018/2/7 11:07
 */
@Deprecated
@Getter
@Setter
public class CacheUtil {
    private static CacheUtil cacheUtil = new CacheUtil();
    //    private Cache cache = Caffeine.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();
    private Map cache = new HashMap();

    public static CacheUtil getInstance() {
        return cacheUtil;
    }

    public void set(Object key, Object value) {
        cache.put(key, value);
    }

    public Object get(Object key) {
//        return cache.getIfPresent(key);
        return cache.get(key);
    }

    public <T> T get(Object key, Class<T> type) {
//        return (T) cache.getIfPresent(key);
        return (T) cache.get(key);
    }

    public <T> T get(Object key, Class<T> type, Function mappingFunction) {
        if (get(key, type) == null) {
            set(key, mappingFunction.apply(null));
        }
        return (T) cache.get(key);
    }

    public void remove(Object key) {
//        cache.invalidate(key);
        cache.remove(key);
    }
}
