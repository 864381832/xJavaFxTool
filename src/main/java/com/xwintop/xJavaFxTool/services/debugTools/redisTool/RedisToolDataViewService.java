package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataViewController;
import com.xwintop.xcore.util.RedisUtil;

import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class RedisToolDataViewService {
	private RedisToolDataViewController redisToolDataViewController;

	public RedisToolDataViewService(RedisToolDataViewController redisToolDataViewController) {
		this.redisToolDataViewController = redisToolDataViewController;
	}
	
	/** 
	 * @Title: setData 
	 * @Description: 初始化数据
	 */
	public void setData(RedisUtil redisUtil,String redisKey) {
		redisToolDataViewController.getServerLabel().setText(redisUtil.getName());
		redisToolDataViewController.getDatabaseLabel().setText(""+redisUtil.getId());
		redisToolDataViewController.getRedisKeyTextField().setText(redisKey);
		Long timeLong = redisUtil.getDeadline(redisKey);
		String time = timeLong == -1 ? "永久" : timeLong.toString();
		redisToolDataViewController.getOverdueTimeTextField().setText(time);
		String type = redisUtil.getValueType(redisKey);
		if("hash".equals(type)) {
			redisToolDataViewController.getValueMapHBox().setVisible(true);
			reloadHash();
		}else if("string".equals(type)) {
			redisToolDataViewController.getValueStringHBox().setVisible(true);
			String value = redisUtil.getString(redisKey);
			redisToolDataViewController.getValueStringTextArea().setText(value);
		}else if("list".equals(type)) {
			redisToolDataViewController.getValueListHBox().setVisible(true);
			reloadList();
		}
	}
	
	/** 
	 * @Title: reloadHash 
	 * @Description: 重新加载Hash
	 */
	public void reloadHash() {
		redisToolDataViewController.getValueMapTableData().clear();
		Map<String, String> map = redisToolDataViewController.getRedisUtil().getHash(redisToolDataViewController.getRedisKey());
		map.forEach((String key, String value)->{
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
		for(Map<String, String> value : valueMapTableData) {
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
		List<String> listValue = redisToolDataViewController.getRedisUtil().getList(redisToolDataViewController.getRedisKey());
		for(String value : listValue) {
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
		for(Map<String, String> valueMap : valueListTableData) {
			listValueList.add(valueMap.get("value"));
		}
		redisToolDataViewController.getRedisUtil().updateList(redisToolDataViewController.getRedisKey(), listValueList);
	}

}
