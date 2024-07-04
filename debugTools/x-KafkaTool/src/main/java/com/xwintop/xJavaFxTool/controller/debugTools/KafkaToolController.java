package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.model.KafkaToolReceiverTableBean;
import com.xwintop.xJavaFxTool.model.KafkaToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.KafkaToolService;
import com.xwintop.xJavaFxTool.view.debugTools.KafkaToolView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: KafkaToolController
 * @Description: kafka调试工具
 * @author: xufeng
 * @date: 2018/2/5 17:05
 */
@Getter
public class KafkaToolController extends KafkaToolView {
	private KafkaToolService kafkaToolService = new KafkaToolService(this);
	private ObservableList<KafkaToolTableBean> tableData = FXCollections.observableArrayList();
	private ObservableList<KafkaToolReceiverTableBean> receiverTableData = FXCollections.observableArrayList();
	private String[] messageTypeStrings = new String[] { "TextMessage" };
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
		queueTableColumn.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, String>("queue"));
		queueTableColumn.setCellFactory(TextFieldTableCell.<KafkaToolTableBean>forTableColumn());
		queueTableColumn.setOnEditCommit((CellEditEvent<KafkaToolTableBean, String> t) -> {
			t.getRowValue().setQueue(t.getNewValue());
		});
		messageTableColumn.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, String>("message"));
		messageTableColumn.setCellFactory(TextFieldTableCell.<KafkaToolTableBean>forTableColumn());
		messageTableColumn.setOnEditCommit((CellEditEvent<KafkaToolTableBean, String> t) -> {
			t.getRowValue().setMessage(t.getNewValue());
		});
		sendNumberTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, String>("sendNumber"));
		sendNumberTableColumn.setCellFactory(TextFieldTableCell.<KafkaToolTableBean>forTableColumn());
		sendNumberTableColumn.setOnEditCommit((CellEditEvent<KafkaToolTableBean, String> t) -> {
			t.getRowValue().setSendNumber(t.getNewValue());
		});
		messageTypeTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, String>("messageType"));
		messageTypeTableColumn
				.setCellFactory(ChoiceBoxTableCell.<KafkaToolTableBean, String>forTableColumn(messageTypeStrings));
		isSendTableColumn.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, Boolean>("isSend"));
		isSendTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isSendTableColumn));
		isCreateTableColumn.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, Boolean>("isCreate"));
		isCreateTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isCreateTableColumn));
		isRequiredReplyTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolTableBean, Boolean>("isRequiredReply"));
		isRequiredReplyTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isRequiredReplyTableColumn));
		tableViewMain.setItems(tableData);
		quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
		quartzChoiceBox.setValue(quartzChoiceBoxStrings[0]);
		JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 1, Integer.MAX_VALUE);
		JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);

		initReceiverView();
	}

	private void initReceiverView() {
		receiverMessageIDTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolReceiverTableBean, String>("messageID"));
		receiverQueueTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolReceiverTableBean, String>("queue"));
		receiverMessageTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolReceiverTableBean, String>("message"));
		receiverMessageTypeTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolReceiverTableBean, String>("messageType"));
		receiverTimestampTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolReceiverTableBean, String>("timestamp"));
		receiverIsAcknowledgeTableColumn
				.setCellValueFactory(new PropertyValueFactory<KafkaToolReceiverTableBean, Boolean>("isAcknowledge"));
		receiverIsAcknowledgeTableColumn
				.setCellFactory(CheckBoxTableCell.forTableColumn(receiverIsAcknowledgeTableColumn));
		receiverTableView.setItems(receiverTableData);
	}

	private void initEvent() {
		tableData.addListener((Change<? extends KafkaToolTableBean> tableBean) -> {
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
					KafkaToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
					KafkaToolTableBean tableBean2 = new KafkaToolTableBean(tableBean.getPropertys());
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
					KafkaToolReceiverTableBean tableBean = receiverTableView.getSelectionModel().getSelectedItem();
					tableBean.setIsAcknowledge(true);
//					try {
//						kafkaToolService.getReceiverMessageMap().get(tableBean.getMessageID()).acknowledge();
//						kafkaToolService.getReceiverMessageMap().remove(tableBean.getMessageID());
//					} catch (JMSException e) {
//						e.printStackTrace();
//					}
				});
				MenuItem menu_acknowledge = new MenuItem("消费全部消息");
				menu_acknowledge.setOnAction(event1 -> {
					for (KafkaToolReceiverTableBean tableBean : receiverTableData) {
						tableBean.setIsAcknowledge(true);
//						try {
//							kafkaToolService.getReceiverMessageMap().get(tableBean.getMessageID()).acknowledge();
//							kafkaToolService.getReceiverMessageMap().remove(tableBean.getMessageID());
//						} catch (JMSException e) {
//							e.printStackTrace();
//						}
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
		kafkaToolService.loadingConfigure();
	}

	@FXML
	private void addItemAction(ActionEvent event) {
		tableData.add(new KafkaToolTableBean(queueTextField.getText(), messageTextField.getText(),
				sendNumberSpinner.getValue().toString(), messageTypeChoiceBox.getValue(), isSendCheckBox.isSelected(),
				isCreateCheckBox.isSelected(), isRequiredReplyCheckBox.isSelected()));
	}

	@FXML
	private void saveConfigureAction(ActionEvent event) throws Exception {
		kafkaToolService.saveConfigure();
	}

	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		kafkaToolService.otherSaveConfigureAction();
	}

	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		kafkaToolService.loadingConfigureAction();
	}

	@FXML
	private void deleteSelectRowAction(ActionEvent event) {
		tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void runQuartzAction(ActionEvent event) throws Exception {
		if ("定时发送".equals(runQuartzButton.getText())) {
			boolean isTrue = kafkaToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(),
					intervalSpinner.getValue(), repeatCountSpinner.getValue());
			if (isTrue) {
				runQuartzButton.setText("停止发送");
			}
		} else {
			boolean isTrue = kafkaToolService.stopQuartzAction();
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
		kafkaToolService.sendAction();
	}

	/**
	 * @Title: receiverMessageListenerAction
	 * @Description: receiver端监听消息
	 */
	@FXML
	private void receiverMessageListenerAction(ActionEvent event) {
		// receiverTableData.add(new KafkaToolReceiverTableBean("1", "2",
		// "1", "1", "2", true));
		if ("监听消息".equals(receiverMessageListenerButton.getText())) {
			kafkaToolService.receiverMessageListenerAction();
			receiverMessageListenerButton.setText("停止监听");
		} else {
			kafkaToolService.receiverMessageStopListenerAction();
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
			kafkaToolService.receiverPullMessageAction();
		});
	}

	/**
	 * 父控件被移除前调用
	 */
	public void onCloseRequest(Event event) throws Exception {
		kafkaToolService.stopQuartzAction();
		kafkaToolService.receiverMessageStopListenerAction();
	}
}