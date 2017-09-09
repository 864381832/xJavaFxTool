package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolDataViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolDataViewView;
import com.xwintop.xcore.util.RedisUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisToolDataViewController extends RedisToolDataViewView {
	private RedisToolDataViewService redisToolDataViewService = new RedisToolDataViewService(this);
	private RedisUtil redisUtil;
	private String redisKey;
	private ObservableList<Map<String, String>> valueMapTableData = FXCollections.observableArrayList();
	private ObservableList<Map<String, String>> valueListTableData = FXCollections.observableArrayList();
	
	public static FXMLLoader getFXMLLoader() {
		FXMLLoader fXMLLoader = new FXMLLoader(
				IndexController.class.getResource("/fxml/debugTools/redisTool/RedisToolDataView.fxml"));
		return fXMLLoader;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		JavaFxViewUtil.setTableColumnMapValueFactory(valueMapKeyTableColumn, "key");
		JavaFxViewUtil.setTableColumnMapValueFactory(valueMapValueTableColumn, "value");
		valueMapTableView.setItems(valueMapTableData);
		JavaFxViewUtil.setTableColumnMapValueFactory(valueListValueTableColumn, "value");
		valueListTableView.setItems(valueListTableData);
	}

	private void initEvent() {
	}

	private void initService() {
	}

	public void setData(RedisUtil redisUtil,String key) {
		this.redisUtil = redisUtil;
		this.redisKey = key;
		redisToolDataViewService.setData(redisUtil,key);
	}
	
	@FXML
	private void overdueEnterAction(ActionEvent event) {
	}

	@FXML
	private void overdueReloadAction(ActionEvent event) {
	}

	@FXML
	private void valueMapAddAction(ActionEvent event) {
	}

	@FXML
	private void valueMapDeleteAction(ActionEvent event) {
	}

	@FXML
	private void valueMapReloadAction(ActionEvent event) {
	}

	@FXML
	private void valueMapShowAction(ActionEvent event) {
	}

	@FXML
	private void valueStringEnterAction(ActionEvent event) {
	}

	@FXML
	private void valueStringCoanelAction(ActionEvent event) {
	}

	@FXML
	private void valueStringReloadAction(ActionEvent event) {
	}

	@FXML
	private void valueStringShowAction(ActionEvent event) {
	}

	@FXML
	private void valueListInsertHeadAction(ActionEvent event) {
	}

	@FXML
	private void valueListAppendTailAction(ActionEvent event) {
	}

	@FXML
	private void valueListDeleteHeadAction(ActionEvent event) {
	}

	@FXML
	private void valueListDeleteTailAction(ActionEvent event) {
	}

	@FXML
	private void valueListEnterAction(ActionEvent event) {
	}

	@FXML
	private void valueListCancelAction(ActionEvent event) {
	}

	@FXML
	private void valueListReloadAction(ActionEvent event) {
	}

	@FXML
	private void valueListShowAction(ActionEvent event) {
	}
}