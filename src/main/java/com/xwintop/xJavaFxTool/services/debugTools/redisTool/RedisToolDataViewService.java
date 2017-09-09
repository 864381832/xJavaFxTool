package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataViewController;
import com.xwintop.xcore.util.RedisUtil;

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
	
	public void setData(RedisUtil redisUtil,String redisKey) {
		redisToolDataViewController.getServerLabel().setText(redisUtil.getName());
		redisToolDataViewController.getDatabaseLabel().setText(""+redisUtil.getId());
		Long timeLong = redisUtil.getDeadline(redisKey);
		String time = timeLong == -1 ? "永久" : timeLong.toString();
		redisToolDataViewController.getOverdueTimeTextField().setText(time);
		String type = redisUtil.getValueType(redisKey);
		if("hash".equals(type)) {
			redisToolDataViewController.getValueMapHBox().setVisible(true);
		}else if("string".equals(type)) {
			redisToolDataViewController.getValueStringHBox().setVisible(true);
			String value = redisUtil.getString(redisKey);
			redisToolDataViewController.getValueStringTextArea().setText(value);
		}else if("list".equals(type)) {
			redisToolDataViewController.getValueListHBox().setVisible(true);
		}
	}

}
