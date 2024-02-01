package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolDataViewService;
import com.xwintop.xJavaFxTool.utils.AlertUtil;
import com.xwintop.xJavaFxTool.utils.RedisUtil;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolDataViewView;

import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
				RedisToolDataViewController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/debugTools/redisTool/RedisToolDataView.fxml"));
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
		overdueCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					overdueTimeTextField.setEditable(true);
				} else {
					overdueTimeTextField.setEditable(false);
				}
			}
		});
		overdueTimeTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (overdueCheckBox.isSelected()) {
					overdueEnterButton.setDisable(false);
				} else {
					overdueEnterButton.setDisable(true);
				}
			}
		});

		valueStringTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.equals(redisUtil.getString(redisKey))) {
					valueStringEnterButton.setDisable(true);
					valueStringCancelButton.setDisable(true);
				} else {
					valueStringEnterButton.setDisable(false);
					valueStringCancelButton.setDisable(false);
				}
			}
		});

		valueListTableData.addListener(new ListChangeListener<Map<String, String>>() {
			@Override
			public void onChanged(Change<? extends Map<String, String>> c) {
				valueListEnterButton.setDisable(false);
				valueListCancelButton.setDisable(false);
			}
		});

		valueMapTableData.addListener(new ListChangeListener<Map<String, String>>() {
			@Override
			public void onChanged(Change<? extends Map<String, String>> c) {
				valueMapEnterButton.setDisable(false);
				valueMapCancelButton.setDisable(false);
			}
		});
	}

	private void initService() {
	}

	public void setData(RedisUtil redisUtil, String key) {
		this.redisUtil = redisUtil;
		this.redisKey = key;
		redisToolDataViewService.setData(redisUtil, key);
		valueMapEnterButton.setDisable(true);
		valueMapCancelButton.setDisable(true);
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void overdueEnterAction(ActionEvent event) {
		redisUtil.setDeadLine(redisKey, Integer.parseInt(overdueTimeTextField.getText()));
		overdueEnterButton.setDisable(true);
		overdueCheckBox.setSelected(false);
	}

	@FXML
	private void overdueReloadAction(ActionEvent event) {
		Long overdueTimeText = redisUtil.getDeadline(redisKey);
		overdueTimeTextField.setText(overdueTimeText == -1 ? "永久" : overdueTimeText.toString());
		overdueCheckBox.setSelected(false);
	}

	@FXML
	private void valueMapAddAction(ActionEvent event) {
		String[] value = null;
		if("hash".equals(redisToolDataViewService.getType())){
			value = AlertUtil.showInputAlert("请输入值","key","value");
		}else if("zset".equals(redisToolDataViewService.getType())){
			value = AlertUtil.showInputAlert("请输入值","Sorce","Member");
		}
		if(value!=null){
			Map<String, String> mapData = new HashMap<String, String>();
			mapData.put("key", value[0]);
			mapData.put("value", value[1]);
			valueMapTableData.add(mapData);
		}
	}

	@FXML
	private void valueMapDeleteAction(ActionEvent event) {
		int selectIndex = valueMapTableView.getSelectionModel().getSelectedIndex();
		if (selectIndex >= 0) {
			valueMapTableData.remove(selectIndex);
		}
	}

	@FXML
	private void valueMapEnterAction(ActionEvent event) {
		redisToolDataViewService.setMapData();
		valueMapEnterButton.setDisable(true);
		valueMapCancelButton.setDisable(true);
	}

	@FXML
	private void valueMapCancelAction(ActionEvent event) {
		redisToolDataViewService.reloadMapData();
	}

	@FXML
	private void valueMapReloadAction(ActionEvent event) {
		redisToolDataViewService.reloadMapData();
	}

	@FXML
	private void valueMapShowAction(ActionEvent event) {
	}

	@FXML
	private void valueStringEnterAction(ActionEvent event) {
		redisUtil.setString(redisKey, valueStringTextArea.getText());
		valueStringEnterButton.setDisable(true);
		valueStringCancelButton.setDisable(true);
	}

	@FXML
	private void valueStringCancelAction(ActionEvent event) {
		redisToolDataViewService.reloadStringData();
	}

	@FXML
	private void valueStringReloadAction(ActionEvent event) {
		redisToolDataViewService.reloadStringData();
	}

	@FXML
	private void valueStringShowAction(ActionEvent event) {
	}

	@FXML
	private void valueListInsertHeadAction(ActionEvent event) {
		String value = AlertUtil.showInputAlert("请输入值");
		if (StringUtils.isNotEmpty(value)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("value", value);
			valueListTableData.add(0, map);
		}
	}

	@FXML
	private void valueListAppendTailAction(ActionEvent event) {
		String value = AlertUtil.showInputAlert("请输入值");
		if (StringUtils.isNotEmpty(value)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("value", value);
			valueListTableData.add(map);
		}
	}

	@FXML
	private void valueListDeleteHeadAction(ActionEvent event) {
		valueListTableData.remove(0);
	}

	@FXML
	private void valueListDeleteTailAction(ActionEvent event) {
		valueListTableData.remove(valueListTableData.size() - 1);
	}

	@FXML
	private void valueListDeleteAction(ActionEvent event) {
		int selectIndex = valueListTableView.getSelectionModel().getSelectedIndex();
		if (selectIndex >= 0) {
			valueListTableData.remove(selectIndex);
		}
	}

	@FXML
	private void valueListEnterAction(ActionEvent event) {
		redisToolDataViewService.setListData();
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void valueListCancelAction(ActionEvent event) {
		redisToolDataViewService.reloadListData();
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void valueListReloadAction(ActionEvent event) {
		redisToolDataViewService.reloadListData();
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void valueListShowAction(ActionEvent event) {
	}
}
