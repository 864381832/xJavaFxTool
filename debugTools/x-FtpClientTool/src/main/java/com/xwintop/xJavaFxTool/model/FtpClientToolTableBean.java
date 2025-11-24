package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class FtpClientToolTableBean {
	private SimpleBooleanProperty isEnabled;
	private SimpleStringProperty localFile;
	private SimpleStringProperty serverFile;
	private SimpleStringProperty type;
	private SimpleStringProperty remarks;

	public FtpClientToolTableBean(Boolean isEnabled, String localFile, String serverFile, String type, String remarks) {
		this.isEnabled = new SimpleBooleanProperty(isEnabled);
		this.localFile = new SimpleStringProperty(localFile);
		this.serverFile = new SimpleStringProperty(serverFile);
		this.type = new SimpleStringProperty(type);
		this.remarks = new SimpleStringProperty(remarks);
	}

	public FtpClientToolTableBean(String propertys) {
		String[] strings = propertys.split("__",5);
		this.isEnabled = new SimpleBooleanProperty(Boolean.valueOf(strings[0]));
		this.localFile = new SimpleStringProperty(strings[1]);
		this.serverFile = new SimpleStringProperty(strings[2]);
		this.type = new SimpleStringProperty(strings[3]);
		this.remarks = new SimpleStringProperty(strings[4]);
	}

	public String getPropertys() {
		return isEnabled.get() + "__" + localFile.get() + "__" + serverFile.get() + "__" + type.get() + "__"
				+ remarks.get();
	}

	public BooleanProperty isEnabledProperty() {
		return isEnabled;
	}

	public Boolean getIsEnabled() {
		return isEnabled.get();
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled.set(isEnabled);
	}

	public String getLocalFile() {
		return localFile.get();
	}

	public void setLocalFile(String localFile) {
		this.localFile.set(localFile);
	}

	public String getServerFile() {
		return serverFile.get();
	}

	public void setServerFile(String serverFile) {
		this.serverFile.set(serverFile);
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getRemarks() {
		return remarks.get();
	}

	public void setRemarks(String remarks) {
		this.remarks.set(remarks);
	}

}
