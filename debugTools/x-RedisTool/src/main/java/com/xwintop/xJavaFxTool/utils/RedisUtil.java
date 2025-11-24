package com.xwintop.xJavaFxTool.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class RedisUtil {
    private String name;
    private String password;
    private Jedis jedis;
    private int id;

    /**
     * @Title: getDbAmount
     * @Description: 获取数据库db大小
     */
    public int getDbAmount() {
        int dbAmount = 0;
        try {
            List<String> dbs = jedis.configGet("databases");
            if (dbs.size() > 0) {
                dbAmount = Integer.parseInt(dbs.get(1));
            }
        } catch (JedisException e) {
            dbAmount = 15;
        }
        return dbAmount;
    }

    public void del(String... key) {
        jedis.del(key);
    }

    /**
     * @Title: delAll
     * @Description: 删除本表所有数据
     */
    public void delAll() {
        Set<String> keys = getKeys();
        for (String key : keys) {
            jedis.del(key);
        }
    }

    /**
     * @Title: flushAll
     * @Description: 清空所有数据
     */
    public void flushAll() {
        jedis.flushAll();
    }

    public String getString(String key) {
        return jedis.get(key);
    }

    public byte[] getString(byte[] key) {
        return jedis.get(key);
    }

    public void setString(String key, String value) {
        jedis.set(key, value);
    }

    public void insertList(String key, boolean beforeAfter, String pivot, String value) {
        jedis.linsert(key, beforeAfter ? ListPosition.BEFORE : ListPosition.AFTER, pivot, value);
    }

    public List<String> getList(String key) {
        return jedis.lrange(key, 0, -1);
    }

    public List<String> getList(String key, int start, int end) {
        return jedis.lrange(key, start, end);
    }

    public void setListValue(String key, int index, String value) {
        jedis.lset(key, index, value);
    }

    /**
     * @Title: addList
     * @Description: 添加List
     */
    public void addList(String key, List<String> values, boolean headTail, boolean exist) {
        for (String value : values) {
            if (headTail && exist) {
                jedis.rpush(key, value);
            } else if (headTail && !exist) {
                jedis.rpushx(key, value);
            } else if (!headTail && exist) {
                jedis.lpush(key, value);
            } else {
                jedis.lpushx(key, value);
            }
        }
    }

    /**
     * @Title: updateList
     * @Description: 更新List
     */
    public void updateList(String key, List<String> values) {
        updateList(key, values, true);
    }

    public void updateList(String key, List<String> values, boolean headTail) {
        Long time = jedis.ttl(key);
        jedis.del(key);
        addList(key, values, true, true);
        setDeadLine(key, time.intValue());
    }

    public String removeListValue(String key, boolean headTail) {
        if (headTail) {
            return jedis.lpop(key);
        } else {
            return jedis.rpop(key);
        }
    }

    public Set<String> getSet(String key) {
        return jedis.smembers(key);
    }

    public Set<String> getSet(String key, int start, int end) {
        SortingParams sp = new SortingParams();
        sp.alpha();
        sp.limit(start, end - start);
        return new HashSet<String>(jedis.sort(key, sp));
    }

    public void addSet(String key, Set<String> values) {
        for (String value : values) {
            jedis.sadd(key, value);
        }
    }

    public void removeSet(String key, Set<String> values) {
        for (String value : values) {
            jedis.srem(key, value);
        }
    }

    public void updateSet(String key, Set<String> values) {
        Long time = jedis.ttl(key);
        jedis.del(key);
        addSet(key, values);
        setDeadLine(key, time.intValue());
    }

    public void addHash(String key, Map<String, String> values) {
        jedis.hmset(key, values);
    }

    public Map<String, String> getHash(String key) {
        return jedis.hgetAll(key);
    }

    public Set<String> getHashKeys(String key) {
        return jedis.hkeys(key);
    }

    public void setHashField(String key, String field, String value) {
        jedis.hset(key, field, value);
    }

    public void delHashField(String key, String[] fields) {
        jedis.hdel(key, fields);
    }

    public void updateHash(String key, Map<String, String> values) {
        Long time = jedis.ttl(key);
        jedis.del(key);
        addHash(key, values);
        setDeadLine(key, time.intValue());
    }

    public Set<Tuple> getZSet(String key) {
        return jedis.zrangeWithScores(key, 0, -1);
    }

    public Set<Tuple> getZSet(String key, int start, int end) {
        return jedis.zrangeWithScores(key, start, end);
    }

    public void addZSet(String key, Map<String, Double> values) {
//		for(Map.Entry<String, Double> value: values.entrySet())
//			jedis.zadd(key, value.getValue(), value.getKey());
        jedis.zadd(key, values);
    }

    public void removeZSet(String key, String[] values) {
        jedis.zrem(key, values);
    }

    public void updateZSet(String key, Map<String, Double> values) {
        Long time = jedis.ttl(key);
        jedis.del(key);
        addZSet(key, values);
        setDeadLine(key, time.intValue());
    }

    public Long getDbSize() {
        return jedis.dbSize();
    }

    public Set<String> getKeys() {
        Set<String> nodekeys = jedis.keys("*");
        return nodekeys;
    }

    public String getValueType(String key) {
        return jedis.type(key);
    }

    public Long getSize(String key) {
        Long size;
        String type = jedis.type(key);
        if ("string".equals(type)) {
            size = (long) 1;
        } else if ("hash".equals(type)) {
            size = jedis.hlen(key);
        } else if ("list".equals(type)) {
            size = jedis.llen(key);
        } else if ("set".equals(type)) {
            size = jedis.scard(key);
        } else {
            size = jedis.zcard(key);
        }
        return size;
    }

    /**
     * @Title: getDeadline
     * @Description: 获取过期时间
     */
    public Long getDeadline(String key) {
        return jedis.ttl(key);
    }

    public void setDeadLine(String key, int second) {
        if (second != -1) {
            jedis.expire(key, second);
        } else {
            jedis.persist(key);
        }
    }

    public RedisUtil(Jedis jedis) {
        this.jedis = jedis;
    }

    public RedisUtil(String name, String host, int port) {
        this.name = name;
        this.jedis = new Jedis(host, port);
    }

    public RedisUtil(String name, String host, int port, String password) {
        this.name = name;
        this.jedis = new Jedis(host, port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.auth(password);
            this.password = password;
        }
    }

    @Override
    public RedisUtil clone() {
        Client client = jedis.getClient();
        RedisUtil redisUtil = new RedisUtil(name, client.getHost(), client.getPort(), password);
        return redisUtil;
    }

    public void setId(int id) {
        jedis.select(id);
        this.id = id;
    }

}
