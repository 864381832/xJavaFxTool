package com.xwintop.xJavaFxTool.controller.debugTools;

import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.model.ActiveMqToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.ActiveMqToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.ActiveMqToolView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;

public class ActiveMqToolController extends ActiveMqToolView {
	private ActiveMqToolService activeMqToolService = new ActiveMqToolService();
	private ObservableList<ActiveMqToolTableBean> tableData = FXCollections.observableArrayList();
	private String[] messageTypeStrings = new String[] { "TextMessage", "ObjectMessage", "BytesMessage", "MapMessage",
			"StreamMessage" };
	private String[] quartzChoiceBoxStrings = new String[] { "简单表达式", "Cron表达式" };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initService();
		initView();
		initEvent();
	}

	private void initView() {
		JavaFxViewUtil.setSpinnerValueFactory(sendNumberSpinner, 1, Integer.MAX_VALUE);
		messageTypeChoiceBox.getItems().addAll(messageTypeStrings);
		messageTypeChoiceBox.setValue(messageTypeStrings[0]);
		queueTableColumn.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, String>("queue"));
		queueTableColumn.setCellFactory(TextFieldTableCell.<ActiveMqToolTableBean>forTableColumn());
		queueTableColumn.setOnEditCommit((CellEditEvent<ActiveMqToolTableBean, String> t) -> {
			t.getRowValue().setQueue(t.getNewValue());
		});
		messageTableColumn.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, String>("message"));
		messageTableColumn.setCellFactory(TextFieldTableCell.<ActiveMqToolTableBean>forTableColumn());
		messageTableColumn.setOnEditCommit((CellEditEvent<ActiveMqToolTableBean, String> t) -> {
			t.getRowValue().setMessage(t.getNewValue());
		});
		sendNumberTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, String>("sendNumber"));
		sendNumberTableColumn.setCellFactory(TextFieldTableCell.<ActiveMqToolTableBean>forTableColumn());
		sendNumberTableColumn.setOnEditCommit((CellEditEvent<ActiveMqToolTableBean, String> t) -> {
			t.getRowValue().setSendNumber(t.getNewValue());
		});
		messageTypeTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, String>("messageType"));
		messageTypeTableColumn.setCellFactory(ChoiceBoxTableCell.<ActiveMqToolTableBean,String>forTableColumn(messageTypeStrings));
		isSendTableColumn.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, Boolean>("isSend"));
		isSendTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isSendTableColumn));
		isCreateTableColumn.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, Boolean>("isCreate"));
		isCreateTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isCreateTableColumn));
		isRequiredReplyTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolTableBean, Boolean>("isRequiredReply"));
		isRequiredReplyTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isRequiredReplyTableColumn));
		tableViewMain.setItems(tableData);
		quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
		quartzChoiceBox.setValue(quartzChoiceBoxStrings[0]);
		JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 1, Integer.MAX_VALUE);
		JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);
	}

	private void initEvent() {
		tableData.addListener((Change<? extends ActiveMqToolTableBean> tableBean)->{
			try {
				saveConfigureAction(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		tableViewMain.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_Copy = new MenuItem("复制选中行");
				menu_Copy.setOnAction(event1 -> {
					ActiveMqToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
					ActiveMqToolTableBean tableBean2= new ActiveMqToolTableBean(tableBean.getPropertys());
					tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(),tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					deleteSelectRowAction(null);
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				tableViewMain.setContextMenu(new ContextMenu(menu_Copy,menu_Remove, menu_RemoveAll));
			}
		});
		quartzChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (quartzChoiceBoxStrings[0].equals(newValue)) {
					cronTextField.setVisible(false);
					simpleScheduleAnchorPane.setVisible(true);
				} else if (quartzChoiceBoxStrings[1].equals(newValue)) {
					cronTextField.setVisible(true);
					simpleScheduleAnchorPane.setVisible(false);
				}
			}
		});
	}

	private void initService() {
		activeMqToolService.setTableData(tableData);
		activeMqToolService.setMessageTypeStrings(messageTypeStrings);
		activeMqToolService.setUrlTextField(urlTextField);
		activeMqToolService.setUserNameTextField(userNameTextField);
		activeMqToolService.setPasswordTextField(passwordTextField);
		activeMqToolService.loadingConfigure();
	}

	@FXML
	private void addItemAction(ActionEvent event) {
		tableData.add(new ActiveMqToolTableBean(queueTextField.getText(), messageTextField.getText(),
				sendNumberSpinner.getValue().toString(), messageTypeChoiceBox.getValue(), isSendCheckBox.isSelected(),
				isCreateCheckBox.isSelected(), isRequiredReplyCheckBox.isSelected()));
	}

	@FXML
	private void saveConfigureAction(ActionEvent event) throws Exception {
		activeMqToolService.saveConfigure();
	}

	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		activeMqToolService.otherSaveConfigureAction();
	}

	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		activeMqToolService.loadingConfigureAction();
	}

	@FXML
	private void deleteSelectRowAction(ActionEvent event) {
		tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void runQuartzAction(ActionEvent event) throws Exception {
		if("定时发送".equals(runQuartzButton.getText())){
			boolean isTrue = activeMqToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(), intervalSpinner.getValue(),
					repeatCountSpinner.getValue());
			if(isTrue){
				runQuartzButton.setText("停止发送");
			}
		}else{
			boolean isTrue = activeMqToolService.stopQuartzAction();
			if(isTrue){
				runQuartzButton.setText("定时发送");
			}
		}
	}

	@FXML
	private void sendAction(ActionEvent event) {
		activeMqToolService.sendAction();
	}
}