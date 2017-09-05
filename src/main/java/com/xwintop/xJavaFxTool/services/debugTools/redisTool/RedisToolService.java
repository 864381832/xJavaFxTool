package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import java.util.HashMap;
import java.util.Map;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolController;
import com.xwintop.xcore.util.RedisUtil;

import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class RedisToolService {
	private RedisToolController redisToolController;
	private Map<String, RedisUtil> jedisMap = new HashMap<String, RedisUtil>();

	public RedisToolService(RedisToolController redisToolController) {
		this.redisToolController = redisToolController;
	}

	public void addServiceAddress(String name, String host, int port, String password) {
		RedisUtil redisUtil = new RedisUtil(name, host, port, password);
		// Jedis jedis = new Jedis("localhost", 6379);
		jedisMap.put(name, redisUtil);
		TreeItem<String> treeItem = new TreeItem<String>(name);
		redisToolController.getRedisServiceTreeView().getRoot().getChildren().add(treeItem);
		int dbAmount = redisUtil.getDbAmount();
		for (int i = 0; i < dbAmount; i++) {
			redisUtil.setId(i);
			TreeItem<String> treeItem2 = new TreeItem<String>("db"+i+"("+redisUtil.getDbSize()+")");
			treeItem.getChildren().add(treeItem2);
		}

	}

}
