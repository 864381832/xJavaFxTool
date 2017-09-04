package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

public class RedisToolController extends RedisToolView {
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		TreeItem<String> treeItem = new TreeItem<String>("Redis服务器");
		redisServiceTreeView.setRoot(treeItem);
	}

	private void initEvent() {
	}

	private void initService() {
	}

	@FXML
	private void treeLeftAction(ActionEvent event) {
	}

	@FXML
	private void treeRightAction(ActionEvent event) {
	}

	@FXML
	private void treeUpAction(ActionEvent event) {
	}

	@FXML
	private void treeRefurbishAction(ActionEvent event) {
	}
}
