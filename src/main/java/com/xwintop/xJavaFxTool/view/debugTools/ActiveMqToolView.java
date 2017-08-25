package com.xwintop.xJavaFxTool.view.debugTools;

import com.xwintop.xJavaFxTool.model.ActiveMqToolReceiverTableBean;
import com.xwintop.xJavaFxTool.model.ActiveMqToolTableBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;

@Getter
public abstract class ActiveMqToolView implements Initializable {
	@FXML
	protected TextField urlTextField;
	@FXML
	protected TextField userNameTextField;
	@FXML
	protected TextField passwordTextField;
	@FXML
	protected TextField queueTextField;
	@FXML
	protected TextArea messageTextField;
	@FXML
	protected Spinner<Integer> sendNumberSpinner;
	@FXML
	protected ChoiceBox<String> messageTypeChoiceBox;
	@FXML
	protected CheckBox isSendCheckBox;
	@FXML
	protected CheckBox isCreateCheckBox;
	@FXML
	protected CheckBox isRequiredReplyCheckBox;
	@FXML
	protected Button addItemButton;
	@FXML
	protected TableView<ActiveMqToolTableBean> tableViewMain;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, String> queueTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, String> messageTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, String> sendNumberTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, String> messageTypeTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, Boolean> isSendTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, Boolean> isCreateTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolTableBean, Boolean> isRequiredReplyTableColumn;
	@FXML
	protected Button saveConfigureButton;
	@FXML
	protected Button otherSaveConfigureButton;
	@FXML
	protected Button loadingConfigureButton;
	@FXML
	protected Button deleteSelectRowButton;
	@FXML
	protected ChoiceBox<String> quartzChoiceBox;
	@FXML
	protected AnchorPane simpleScheduleAnchorPane;
	@FXML
	protected TextField cronTextField;
	@FXML
	protected Spinner<Integer> intervalSpinner;
	@FXML
	protected Spinner<Integer> repeatCountSpinner;
	@FXML
	protected Button runQuartzButton;
	@FXML
	protected Button sendButton;

	@FXML
	protected TextField receiverQueueTextField;
	@FXML
	protected Button receiverPullMessageButton;
	@FXML
	protected Button receiverMessageListenerButton;
	@FXML
	protected ChoiceBox<String> receiverAcknowledgeModeChoiceBox;
	@FXML
	protected TableView<ActiveMqToolReceiverTableBean> receiverTableView;
	@FXML
	protected TableColumn<ActiveMqToolReceiverTableBean, String> receiverMessageIDTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolReceiverTableBean, String> receiverQueueTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolReceiverTableBean, String> receiverMessageTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolReceiverTableBean, String> receiverMessageTypeTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolReceiverTableBean, String> receiverTimestampTableColumn;
	@FXML
	protected TableColumn<ActiveMqToolReceiverTableBean, Boolean> receiverIsAcknowledgeTableColumn;
}