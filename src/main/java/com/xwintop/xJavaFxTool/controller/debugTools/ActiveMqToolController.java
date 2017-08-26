package com.xwintop.xJavaFxTool.controller.debugTools;

import java.net.URL;
import java.util.ResourceBundle;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQSession;

import com.xwintop.xJavaFxTool.model.ActiveMqToolReceiverTableBean;
import com.xwintop.xJavaFxTool.model.ActiveMqToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.ActiveMqToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.ActiveMqToolView;
import com.xwintop.xcore.base.XProperty;

import javafx.application.Platform;
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
import lombok.Getter;

@Getter
public class ActiveMqToolController extends ActiveMqToolView {
	private ActiveMqToolService activeMqToolService = new ActiveMqToolService(this);
	private ObservableList<ActiveMqToolTableBean> tableData = FXCollections.observableArrayList();
	private ObservableList<ActiveMqToolReceiverTableBean> receiverTableData = FXCollections.observableArrayList();
	private String[] messageTypeStrings = new String[] { "TextMessage", "ObjectMessage", "BytesMessage", "MapMessage",
			"StreamMessage" };
	private String[] quartzChoiceBoxStrings = new String[] { "简单表达式", "Cron表达式" };
	@SuppressWarnings("unchecked")
	private XProperty<Integer>[] receiverAcknowledgeModeChoiceBoxValues = new XProperty[] {
			new XProperty<Integer>("SESSION_TRANSACTED", Session.SESSION_TRANSACTED),
			new XProperty<Integer>("AUTO_ACKNOWLEDGE", Session.AUTO_ACKNOWLEDGE),
			new XProperty<Integer>("CLIENT_ACKNOWLEDGE", Session.CLIENT_ACKNOWLEDGE),
			new XProperty<Integer>("DUPS_OK_ACKNOWLEDGE", Session.DUPS_OK_ACKNOWLEDGE),
			new XProperty<Integer>("INDIVIDUAL_ACKNOWLEDGE", ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE) };

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
		messageTypeTableColumn
				.setCellFactory(ChoiceBoxTableCell.<ActiveMqToolTableBean, String>forTableColumn(messageTypeStrings));
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

		initReceiverView();
	}

	private void initReceiverView() {
		receiverAcknowledgeModeChoiceBox.getItems().addAll(receiverAcknowledgeModeChoiceBoxValues);
		receiverAcknowledgeModeChoiceBox.setValue(receiverAcknowledgeModeChoiceBoxValues[4]);
		receiverMessageIDTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolReceiverTableBean, String>("messageID"));
		receiverQueueTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolReceiverTableBean, String>("queue"));
		receiverMessageTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolReceiverTableBean, String>("message"));
		receiverMessageTypeTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolReceiverTableBean, String>("messageType"));
		receiverTimestampTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolReceiverTableBean, String>("timestamp"));
		receiverIsAcknowledgeTableColumn
				.setCellValueFactory(new PropertyValueFactory<ActiveMqToolReceiverTableBean, Boolean>("isAcknowledge"));
		receiverIsAcknowledgeTableColumn
				.setCellFactory(CheckBoxTableCell.forTableColumn(receiverIsAcknowledgeTableColumn));
		receiverTableView.setItems(receiverTableData);
	}

	private void initEvent() {
		tableData.addListener((Change<? extends ActiveMqToolTableBean> tableBean) -> {
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
					ActiveMqToolTableBean tableBean2 = new ActiveMqToolTableBean(tableBean.getPropertys());
					tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(), tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					deleteSelectRowAction(null);
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				tableViewMain.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
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
		initReceiverEvent();
	}

	private void initReceiverEvent() {
		receiverTableView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_acknowledge1 = new MenuItem("消费该消息");
				menu_acknowledge1.setOnAction(event1 -> {
					ActiveMqToolReceiverTableBean tableBean = receiverTableView.getSelectionModel().getSelectedItem();
					tableBean.setIsAcknowledge(true);
					try {
						activeMqToolService.getReceiverMessageMap().get(tableBean.getMessageID()).acknowledge();
						activeMqToolService.getReceiverMessageMap().remove(tableBean.getMessageID());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				});
				MenuItem menu_acknowledge = new MenuItem("消费全部消息");
				menu_acknowledge.setOnAction(event1 -> {
					for (ActiveMqToolReceiverTableBean tableBean : receiverTableData) {
						tableBean.setIsAcknowledge(true);
						try {
							activeMqToolService.getReceiverMessageMap().get(tableBean.getMessageID()).acknowledge();
							activeMqToolService.getReceiverMessageMap().remove(tableBean.getMessageID());
						} catch (JMSException e) {
							e.printStackTrace();
						}
					}
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					deleteSelectRowAction(null);
					receiverTableData.remove(receiverTableView.getSelectionModel().getSelectedIndex());
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					receiverTableData.clear();
				});
				receiverTableView.setContextMenu(
						new ContextMenu(menu_acknowledge1, menu_acknowledge, menu_Remove, menu_RemoveAll));
			}
		});
	}

	private void initService() {
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
		if ("定时发送".equals(runQuartzButton.getText())) {
			boolean isTrue = activeMqToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(),
					intervalSpinner.getValue(), repeatCountSpinner.getValue());
			if (isTrue) {
				runQuartzButton.setText("停止发送");
			}
		} else {
			boolean isTrue = activeMqToolService.stopQuartzAction();
			if (isTrue) {
				runQuartzButton.setText("定时发送");
			}
		}
	}

	/**
	 * @Title: sendAction
	 * @Description: 客户端发送消息
	 */
	@FXML
	private void sendAction(ActionEvent event) {
		activeMqToolService.sendAction();
	}

	/**
	 * @Title: receiverMessageListenerAction
	 * @Description: receiver端监听消息
	 */
	@FXML
	private void receiverMessageListenerAction(ActionEvent event) {
		// receiverTableData.add(new ActiveMqToolReceiverTableBean("1", "2",
		// "1", "1", "2", true));
		if ("监听消息".equals(receiverMessageListenerButton.getText())) {
			activeMqToolService.receiverMessageListenerAction();
			receiverMessageListenerButton.setText("停止监听");
		} else {
			activeMqToolService.receiverMessageStopListenerAction();
			receiverMessageListenerButton.setText("监听消息");
		}
	}

	/**
	 * @Title: receiverPullMessageAction
	 * @Description: receiver端拉取消息
	 */
	@FXML
	private void receiverPullMessageAction(ActionEvent event) {
		Platform.runLater(() -> {
			activeMqToolService.receiverPullMessageAction();
		});
	}
}