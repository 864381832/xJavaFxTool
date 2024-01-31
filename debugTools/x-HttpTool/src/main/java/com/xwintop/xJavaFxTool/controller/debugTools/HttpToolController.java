package com.xwintop.xJavaFxTool.controller.debugTools;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.json.JSONUtil;
import com.xwintop.xJavaFxTool.services.debugTools.HttpToolService;
import com.xwintop.xJavaFxTool.view.debugTools.HttpToolView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: HttpToolController
 * @Description: Http调试工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:23
 */

@Slf4j
@Setter
@Getter
public class HttpToolController extends HttpToolView {
	private HttpToolService httpToolService = new HttpToolService(this);
	private String[] methodStrings = new String[] { "GET", "POST", "HEAD", "PUT", "PATCH", "DELETE" };
	private ObservableList<Map<String, String>> paramsDatatableData = FXCollections.observableArrayList();
	private ObservableList<Map<String, String>> paramsHeadertableData = FXCollections.observableArrayList();
	private ObservableList<Map<String, String>> paramsCookietableData = FXCollections.observableArrayList();
	private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		methodChoiceBox.getItems().addAll(methodStrings);
		methodChoiceBox.setValue(methodStrings[0]);
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsDataNameTableColumn, "name");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsDataValueTableColumn, "value");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsDataRemarkTableColumn, "remark");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsHeaderNameTableColumn, "name");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsHeaderValueTableColumn, "value");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsHeaderRemarkTableColumn, "remark");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsCookieNameTableColumn, "name");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsCookieValueTableColumn, "value");
		JavaFxViewUtil.setTableColumnMapValueFactory(paramsCookieRemarkTableColumn, "remark");
//		Map<String, String> hMap = new HashMap<String, String>();
//		hMap.put("name", "key");
//		hMap.put("value", "lo5ujaa0pv5jtrkv");
//		hMap.put("remark", "API密钥");
//		Map<String, String> hMap2 = new HashMap<String, String>();
//		hMap2.put("name", "location");
//		hMap2.put("value", "北京");
//		hMap2.put("remark", "城市中文名");
//		paramsDatatableData.addAll(hMap, hMap2);
		paramsDataTableView.setItems(paramsDatatableData);
		paramsHeaderTableView.setItems(paramsHeadertableData);
		paramsCookieTableView.setItems(paramsCookietableData);
	}

	private void initEvent() {
		paramsDataIsStringCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue){
					paramsDataTextArea.setVisible(true);
					paramsDataTableView.setVisible(false);
				}else{
					paramsDataTextArea.setVisible(false);
					paramsDataTableView.setVisible(true);
				}
			}
		});
		setTableViewOnMouseClicked(paramsDataTableView, paramsDatatableData);
		setTableViewOnMouseClicked(paramsHeaderTableView, paramsHeadertableData);
		setTableViewOnMouseClicked(paramsCookieTableView, paramsCookietableData);
		MenuItem compressJsonMenuItem = new MenuItem("压缩JSON");
		compressJsonMenuItem.setOnAction(event -> {
			Matcher m = p.matcher(ResponseBodyTextArea.getText());
			ResponseBodyTextArea.setText(m.replaceAll(""));
		});
		MenuItem formatJsonMenuItem = new MenuItem("格式化JSON");
		formatJsonMenuItem.setOnAction(event -> {
			try {
				String prettyJsonString = JSONUtil.formatJsonStr(ResponseBodyTextArea.getText());
				ResponseBodyTextArea.setText("null".equals(prettyJsonString) ? "" : prettyJsonString);
			} catch (Exception e) {
				log.debug("格式化错误:" + e.getMessage());
				TooltipUtil.showToast("格式化错误:" + e.getMessage());
			}
		});
		ResponseBodyTextArea.setContextMenu(new ContextMenu(compressJsonMenuItem, formatJsonMenuItem));
	}

	private void initService() {
	}

	@FXML
	private void sendAction(ActionEvent event) {
		try {
			httpToolService.sendAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void addParamsDataAction(ActionEvent event) {
		paramsDatatableData.add(new HashMap<String, String>());
	}

	@FXML
	void addParamsHeaderAction(ActionEvent event) {
		paramsHeadertableData.add(new HashMap<String, String>());
	}

	@FXML
	void addParamsCookieAction(ActionEvent event) {
		paramsCookietableData.add(new HashMap<String, String>());
	}

	@FXML
	private void toBrowerAction(ActionEvent event) {
		httpToolService.toBrowerAction();
	}

	/**
	 * @Title: setTableViewOnMouseClicked
	 * @Description: 设置表格右击事件
	 */
	private void setTableViewOnMouseClicked(TableView<Map<String, String>> paramsDataTableView,
			ObservableList<Map<String, String>> paramsDatatableData) {
		paramsDataTableView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY && !paramsDatatableData.isEmpty()) {
				MenuItem menu_Copy = new MenuItem("复制选中行");
				menu_Copy.setOnAction(event1 -> {
					Map<String, String> tableBean = paramsDataTableView.getSelectionModel().getSelectedItem();
					Map<String, String> tableBean2 = new HashMap<String, String>(tableBean);
					paramsDatatableData.add(paramsDataTableView.getSelectionModel().getSelectedIndex(), tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					paramsDatatableData.remove(paramsDataTableView.getSelectionModel().getSelectedIndex());
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					paramsDatatableData.clear();
				});
				paramsDataTableView.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
			}
		});
	}
}
