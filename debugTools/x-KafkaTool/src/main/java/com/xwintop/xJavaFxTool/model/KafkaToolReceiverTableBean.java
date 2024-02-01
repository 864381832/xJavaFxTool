package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class KafkaToolReceiverTableBean {
	private SimpleStringProperty messageID;
	private SimpleStringProperty queue;
	private SimpleStringProperty message;
	private SimpleStringProperty messageType;
	private SimpleStringProperty timestamp;
	private SimpleBooleanProperty isAcknowledge;

	public KafkaToolReceiverTableBean(String messageID, String queue, String message, String messageType,
                                      String timestamp, Boolean isAcknowledge) {
		super();
		this.messageID = new SimpleStringProperty(messageID);
		this.queue = new SimpleStringProperty(queue);
		this.message = new SimpleStringProperty(message);
		this.messageType = new SimpleStringProperty(messageType);
		this.timestamp = new SimpleStringProperty(timestamp);
		this.isAcknowledge = new SimpleBooleanProperty(isAcknowledge);
	}

	public KafkaToolReceiverTableBean(String propertys) {
		String[] strings = propertys.split("__",6);
		this.messageID = new SimpleStringProperty(strings[0]);
		this.queue = new SimpleStringProperty(strings[1]);
		this.message = new SimpleStringProperty(strings[2]);
		this.messageType = new SimpleStringProperty(strings[3]);
		this.timestamp = new SimpleStringProperty(strings[4]);
		this.isAcknowledge = new SimpleBooleanProperty(Boolean.valueOf(strings[5]));
	}

	public String getPropertys() {
		return messageID.get() + "__" + queue.get() + "__" + message.get() + "__" + messageType.get() + "__"
				+ timestamp.get() + "__" + isAcknowledge.get();
	}

	public String getMessageID() {
		return messageID.get();
	}

	public void setMessageID(String messageID) {
		this.messageID.set(messageID);
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

	public String getMessageType() {
		return messageType.get();
	}

	public void setMessageType(String messageType) {
		this.messageType.set(messageType);
	}

	public String getTimestamp() {
		return timestamp.get();
	}

	public void setTimestamp(String timestamp) {
		this.timestamp.set(timestamp);
	}

	public Boolean getIsAcknowledge() {
		return isAcknowledge.getValue();
	}

	public BooleanProperty isAcknowledgeProperty() {
		return isAcknowledge;
	}

	public void setIsAcknowledge(Boolean isAcknowledge) {
		this.isAcknowledge.set(isAcknowledge);
	}

}
