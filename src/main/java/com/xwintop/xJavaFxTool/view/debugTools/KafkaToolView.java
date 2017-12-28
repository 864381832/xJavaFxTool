package com.xwintop.xJavaFxTool.view.debugTools;

import com.xwintop.xJavaFxTool.model.KafkaToolReceiverTableBean;
import com.xwintop.xJavaFxTool.model.KafkaToolTableBean;
import com.xwintop.xcore.base.XProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;

@Getter
public abstract class KafkaToolView implements Initializable {
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
	protected TableView<KafkaToolTableBean> tableViewMain;
	@FXML
	protected TableColumn<KafkaToolTableBean, String> queueTableColumn;
	@FXML
	protected TableColumn<KafkaToolTableBean, String> messageTableColumn;
	@FXML
	protected TableColumn<KafkaToolTableBean, String> sendNumberTableColumn;
	@FXML
	protected TableColumn<KafkaToolTableBean, String> messageTypeTableColumn;
	@FXML
	protected TableColumn<KafkaToolTableBean, Boolean> isSendTableColumn;
	@FXML
	protected TableColumn<KafkaToolTableBean, Boolean> isCreateTableColumn;
	@FXML
	protected TableColumn<KafkaToolTableBean, Boolean> isRequiredReplyTableColumn;
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
	protected ChoiceBox<XProperty<Integer>> receiverAcknowledgeModeChoiceBox;
	@FXML
	protected TableView<KafkaToolReceiverTableBean> receiverTableView;
	@FXML
	protected TableColumn<KafkaToolReceiverTableBean, String> receiverMessageIDTableColumn;
	@FXML
	protected TableColumn<KafkaToolReceiverTableBean, String> receiverQueueTableColumn;
	@FXML
	protected TableColumn<KafkaToolReceiverTableBean, String> receiverMessageTableColumn;
	@FXML
	protected TableColumn<KafkaToolReceiverTableBean, String> receiverMessageTypeTableColumn;
	@FXML
	protected TableColumn<KafkaToolReceiverTableBean, String> receiverTimestampTableColumn;
	@FXML
	protected TableColumn<KafkaToolReceiverTableBean, Boolean> receiverIsAcknowledgeTableColumn;
}