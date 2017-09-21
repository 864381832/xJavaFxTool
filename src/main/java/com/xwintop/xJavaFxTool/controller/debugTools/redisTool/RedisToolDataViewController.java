package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolDataViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolDataViewView;
import com.xwintop.xcore.util.RedisUtil;

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
				}else {
					valueStringEnterButton.setDisable(false);
					valueStringCancelButton.setDisable(false);
				}
			}
		});
		
		valueListTableData.addListener(new ListChangeListener<Map<String,String>>(){
			@Override
			public void onChanged(Change<? extends Map<String, String>> c) {
				valueListEnterButton.setDisable(false);
				valueListCancelButton.setDisable(false);
			}
		});
	}

	private void initService() {
	}

	public void setData(RedisUtil redisUtil, String key) {
		this.redisUtil = redisUtil;
		this.redisKey = key;
		redisToolDataViewService.setData(redisUtil,key);
	}

	@FXML
	private void overdueEnterAction(ActionEvent event) {
		redisUtil.setDeadLine(redisKey, Integer.parseInt(overdueTimeTextField.getText()));
		overdueEnterButton.setDisable(true);
		overdueCheckBox.setSelected(false);
	}

	@FXML
	private void overdueReloadAction(ActionEvent event) {
		overdueTimeTextField.setText(redisUtil.getDeadline(redisKey).toString());
		overdueCheckBox.setSelected(false);
	}

	@FXML
	private void valueMapAddAction(ActionEvent event) {
	}

	@FXML
	private void valueMapDeleteAction(ActionEvent event) {
	}
	@FXML
	private void valueMapEnterAction(ActionEvent event) {
	}
	
	@FXML
	private void valueMapCancelAction(ActionEvent event) {
	}


	@FXML
	private void valueMapReloadAction(ActionEvent event) {
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
		valueStringTextArea.setText(redisUtil.getString(redisKey));
	}

	@FXML
	private void valueStringReloadAction(ActionEvent event) {
		valueStringTextArea.setText(redisUtil.getString(redisKey));
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
		redisToolDataViewService.setList();
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void valueListCancelAction(ActionEvent event) {
		redisToolDataViewService.reloadList();
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void valueListReloadAction(ActionEvent event) {
		redisToolDataViewService.reloadList();
		valueListEnterButton.setDisable(true);
		valueListCancelButton.setDisable(true);
	}

	@FXML
	private void valueListShowAction(ActionEvent event) {
	}
}