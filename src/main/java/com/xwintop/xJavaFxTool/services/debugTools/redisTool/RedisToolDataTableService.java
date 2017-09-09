package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import java.io.IOException;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataTableController;
import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class RedisToolDataTableService {
	private RedisToolDataTableController redisToolDataTableController;

	public RedisToolDataTableService(RedisToolDataTableController redisToolDataTableController) {
		this.redisToolDataTableController = redisToolDataTableController;
	}
	
	public void addRedisToolDataViewTab(String key) {
		Tab tab = new Tab(key);
		FXMLLoader fXMLLoader = RedisToolDataViewController.getFXMLLoader();
		try {
			tab.setContent(fXMLLoader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		RedisToolDataViewController redisToolDataViewController = fXMLLoader.getController();
		redisToolDataViewController.setData(redisToolDataTableController.getRedisUtil(), key);
		redisToolDataTableController.getRedisToolController().getDataViewTabPane().getTabs().add(tab);
		redisToolDataTableController.getRedisToolController().getDataViewTabPane().getSelectionModel().select(tab);
	}

}
