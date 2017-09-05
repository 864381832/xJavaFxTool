package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;

@Getter
@Setter
@Log4j
public class RedisToolService {
	private RedisToolController redisToolController;
	private Map<String,Jedis> jedisMap = new HashMap<String, Jedis>();

	public RedisToolService(RedisToolController redisToolController) {
		this.redisToolController = redisToolController;
	}
	
	public void addServiceAddress(String name,String host,int port,String password){
//		Jedis jedis = new Jedis("localhost", 6379);
		Jedis jedis = new Jedis(host, port);
		if(StringUtils.isNotEmpty(password)){
			jedis.auth(password);
		}
		jedis.getDB();
		jedisMap.put(name, jedis);
	}

}
