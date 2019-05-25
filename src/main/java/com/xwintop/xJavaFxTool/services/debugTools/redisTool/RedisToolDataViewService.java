package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataViewController;
import com.xwintop.xcore.util.RedisUtil;

import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Tuple;

@Getter
@Setter
@Slf4j
public class RedisToolDataViewService {
	private RedisToolDataViewController redisToolDataViewController;
	private String type;

	public RedisToolDataViewService(RedisToolDataViewController redisToolDataViewController) {
		this.redisToolDataViewController = redisToolDataViewController;
	}

	/**
	 * @Title: setData
	 * @Description: 初始化数据
	 */
	public void setData(RedisUtil redisUtil, String redisKey) {
		log.info("setData_redisKey:"+redisKey);
		redisToolDataViewController.getServerLabel().setText(redisUtil.getName());
		redisToolDataViewController.getDatabaseLabel().setText("" + redisUtil.getId());
		redisToolDataViewController.getRedisKeyTextField().setText(redisKey);
		Long timeLong = redisUtil.getDeadline(redisKey);
		String time = timeLong == -1 ? "永久" : timeLong.toString();
		redisToolDataViewController.getOverdueTimeTextField().setText(time);
		type = redisUtil.getValueType(redisKey);
		if ("hash".equals(type) || "zset".equals(type)) {
			redisToolDataViewController.getValueMapHBox().setVisible(true);
			if("zset".equals(type)){
				redisToolDataViewController.getValueMapKeyTableColumn().setText("Sorce");
				redisToolDataViewController.getValueMapValueTableColumn().setText("Member");
			}
			reloadMapData();
		} else if ("string".equals(type)) {
			redisToolDataViewController.getValueStringHBox().setVisible(true);
			reloadStringData();
		} else if ("list".equals(type) || "set".equals(type)) {
			redisToolDataViewController.getValueListHBox().setVisible(true);
			if("set".equals(type)){
				VBox parent = (VBox) redisToolDataViewController.getValueListInsertHeadButton().getParent();
				parent.getChildren().remove(redisToolDataViewController.getValueListInsertHeadButton());
				parent.getChildren().remove(redisToolDataViewController.getValueListDeleteHeadButton());
				parent.getChildren().remove(redisToolDataViewController.getValueListDeleteTailButton());
				redisToolDataViewController.getValueListAppendTailButton().setText("添加");
			}
			reloadListData();
		}
	}

	public void reloadStringData() {
		String value = redisToolDataViewController.getRedisUtil().getString(redisToolDataViewController.getRedisKey());
		redisToolDataViewController.getValueStringTextArea().setText(value);
	}

	/**
	 * @Title: reloadListData
	 * @Description: 刷新List数据
	 */
	public void reloadListData() {
		if ("list".equals(type)) {
			reloadList();
		} else if ("set".equals(type)) {
			reloadSet();
		}
	}

	/**
	 * @Title: reloadMapData
	 * @Description: 刷新Map数据
	 */
	public void reloadMapData() {
		if ("hash".equals(type)) {
			reloadHash();
		} else if ("zset".equals(type)) {
			reloadZSet();
		}
	}

	public void setListData() {
		if ("list".equals(type)) {
			setList();
		} else if ("set".equals(type)) {
			setSet();
		}
	}
	
	public void setMapData() {
		if ("hash".equals(type)) {
			setHash();
		} else if ("zset".equals(type)) {
			setZSet();
		}
	}

	/**
	 * @Title: reloadHash
	 * @Description: 重新加载Hash
	 */
	public void reloadHash() {
		redisToolDataViewController.getValueMapTableData().clear();
		Map<String, String> map = redisToolDataViewController.getRedisUtil()
				.getHash(redisToolDataViewController.getRedisKey());
		map.forEach((String key, String value) -> {
			Map<String, String> mapData = new HashMap<String, String>();
			mapData.put("key", key);
			mapData.put("value", value);
			redisToolDataViewController.getValueMapTableData().add(mapData);
		});
	}

	/**
	 * @Title: setHash
	 * @Description: 设置Hash数据
	 */
	public void setHash() {
		ObservableList<Map<String, String>> valueMapTableData = redisToolDataViewController.getValueMapTableData();
		Map<String, String> valueMap = new HashMap<String, String>();
		for (Map<String, String> value : valueMapTableData) {
			valueMap.put(value.get("key"), value.get("value"));
		}
		redisToolDataViewController.getRedisUtil().updateHash(redisToolDataViewController.getRedisKey(), valueMap);
	}

	/**
	 * @Title: reloadList
	 * @Description: 重新加载List
	 */
	public void reloadList() {
		redisToolDataViewController.getValueListTableData().clear();
		List<String> listValue = redisToolDataViewController.getRedisUtil()
				.getList(redisToolDataViewController.getRedisKey());
		for (String value : listValue) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("value", value);
			redisToolDataViewController.getValueListTableData().add(map);
		}
	}

	/**
	 * @Title: setList
	 * @Description: 更新List
	 */
	public void setList() {
		ObservableList<Map<String, String>> valueListTableData = redisToolDataViewController.getValueListTableData();
		List<String> listValueList = new ArrayList<String>();
		for (Map<String, String> valueMap : valueListTableData) {
			listValueList.add(valueMap.get("value"));
		}
		redisToolDataViewController.getRedisUtil().updateList(redisToolDataViewController.getRedisKey(), listValueList);
	}
	
	public void reloadSet() {
		redisToolDataViewController.getValueListTableData().clear();
		Set<String> listValue = redisToolDataViewController.getRedisUtil()
				.getSet(redisToolDataViewController.getRedisKey());
		for (String value : listValue) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("value", value);
			redisToolDataViewController.getValueListTableData().add(map);
		}
	}
	
	public void setSet() {
		ObservableList<Map<String, String>> valueListTableData = redisToolDataViewController.getValueListTableData();
		Set<String> listValueList = new HashSet<String>();
		for (Map<String, String> valueMap : valueListTableData) {
			listValueList.add(valueMap.get("value"));
		}
		redisToolDataViewController.getRedisUtil().updateSet(redisToolDataViewController.getRedisKey(),listValueList);
	}
	
	/** 
	 * @Title: reloadZset 
	 * @Description: 重新加载Sorted Set
	 * @return: void
	 */
	public void reloadZSet() {
		redisToolDataViewController.getValueMapTableData().clear();
		Set<Tuple> map = redisToolDataViewController.getRedisUtil()
				.getZSet(redisToolDataViewController.getRedisKey());
		map.forEach((Tuple tuple) -> {
			Map<String, String> mapData = new HashMap<String, String>();
			mapData.put("key", String.valueOf(tuple.getScore()));
			mapData.put("value", tuple.getElement());
			redisToolDataViewController.getValueMapTableData().add(mapData);
		});
	}
	
	public void setZSet() {
		ObservableList<Map<String, String>> valueMapTableData = redisToolDataViewController.getValueMapTableData();
		Map<String, Double> valueMap = new HashMap<String, Double>();
		for (Map<String, String> value : valueMapTableData) {
			valueMap.put(value.get("value"),Double.parseDouble(value.get("key")));
		}
		redisToolDataViewController.getRedisUtil().updateZSet(redisToolDataViewController.getRedisKey(), valueMap);
	}

}
