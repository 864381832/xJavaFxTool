package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KafkaToolTableBean {
	private SimpleStringProperty queue;
	private SimpleStringProperty message;
	private SimpleStringProperty sendNumber;
	private SimpleStringProperty messageType;
	private SimpleBooleanProperty isSend;
	private SimpleBooleanProperty isCreate;
	private SimpleBooleanProperty isRequiredReply;

	public KafkaToolTableBean(String queue, String message, String sendNumber, String messageType, Boolean isSend,
                              Boolean isCreate, Boolean isRequiredReply) {
		super();
		this.queue = new SimpleStringProperty(queue);
		this.message = new SimpleStringProperty(message);
		this.sendNumber = new SimpleStringProperty(sendNumber);
		this.messageType = new SimpleStringProperty(messageType);
		this.isSend = new SimpleBooleanProperty(isSend);
		this.isCreate = new SimpleBooleanProperty(isCreate);
		this.isRequiredReply = new SimpleBooleanProperty(isRequiredReply);
	}

	public KafkaToolTableBean(String propertys) {
		String[] strings = propertys.split("__",7);
		this.queue = new SimpleStringProperty(strings[0]);
		this.message = new SimpleStringProperty(strings[1]);
		this.sendNumber = new SimpleStringProperty(strings[2]);
		this.messageType = new SimpleStringProperty(strings[3]);
		this.isSend = new SimpleBooleanProperty(Boolean.valueOf(strings[4]));
		this.isCreate = new SimpleBooleanProperty(Boolean.valueOf(strings[5]));
		this.isRequiredReply = new SimpleBooleanProperty(Boolean.valueOf(strings[6]));
	}

	public String getPropertys() {
		return queue.get() + "__" + message.get() + "__" + sendNumber.get() + "__" + messageType.get() + "__"
				+ isSend.get() + "__" + isCreate.get() + "__" + isRequiredReply.get();
	}

	public String getQueue() {
		return queue.get();
	}

	public void setQueue(String queue) {
		this.queue.set(queue);
	}

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	public String getSendNumber() {
		return sendNumber.get();
	}

	public void setSendNumber(String sendNumber) {
		this.sendNumber.set(sendNumber);
	}

	public String getMessageType() {
		return messageType.get();
	}
	
	public StringProperty messageTypeProperty() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType.set(messageType);
	}

	public Boolean getIsSend() {
		return isSend.getValue();
	}

	public BooleanProperty isSendProperty() {
		return isSend;
	}

	public void setIsSend(Boolean isSend) {
		this.isSend.set(isSend);
	}

	public Boolean getIsCreate() {
		return isCreate.getValue();
	}

	public BooleanProperty isCreateProperty() {
		return isCreate;
	}

	public void setIsCreate(Boolean isCreate) {
		this.isCreate.set(isCreate);
	}

	public Boolean getIsRequiredReply() {
		return isRequiredReply.getValue();
	}

	public BooleanProperty isRequiredReplyProperty() {
		return isRequiredReply;
	}

	public void setIsRequiredReply(Boolean isRequiredReply) {
		this.isRequiredReply.set(isRequiredReply);
	}

}
