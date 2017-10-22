package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class FtpServerTableBean {
	private SimpleBooleanProperty isEnabled;//是否启用
	private SimpleStringProperty userName;
	private SimpleStringProperty password;
	private SimpleStringProperty homeDirectory;
	private SimpleBooleanProperty downFIle;
	private SimpleBooleanProperty upFile;
	private SimpleBooleanProperty deleteFile;//读写权限

	public FtpServerTableBean(Boolean isEnabled, String userName, String password, String homeDirectory, Boolean downFIle, Boolean upFile,
			Boolean deleteFile) {
		super();
		this.isEnabled = new SimpleBooleanProperty(isEnabled);
		this.userName = new SimpleStringProperty(userName);
		this.password = new SimpleStringProperty(password);
		this.homeDirectory = new SimpleStringProperty(homeDirectory);
		this.downFIle = new SimpleBooleanProperty(downFIle);
		this.upFile = new SimpleBooleanProperty(upFile);
		this.deleteFile = new SimpleBooleanProperty(deleteFile);
	}

	public FtpServerTableBean(String propertys) {
		String[] strings = propertys.split("__");
		this.userName = new SimpleStringProperty(strings[0]);
		this.password = new SimpleStringProperty(strings[1]);
		this.homeDirectory = new SimpleStringProperty(strings[2]);
		this.downFIle = new SimpleBooleanProperty(Boolean.valueOf(strings[3]));
		this.upFile = new SimpleBooleanProperty(Boolean.valueOf(strings[4]));
		this.deleteFile = new SimpleBooleanProperty(Boolean.valueOf(strings[5]));
		this.isEnabled = new SimpleBooleanProperty(Boolean.valueOf(strings[6]));
	}

	public String getPropertys() {
		return userName.get() + "__" + password.get() + "__" + homeDirectory.get() + "__" + downFIle.get() + "__"
				+ upFile.get() + "__" + deleteFile.get() + "__" + isEnabled.get();
	}

	public String getUserName() {
		return userName.get();
	}

	public void setUserName(String userName) {
		this.userName.set(userName);
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public String getHomeDirectory() {
		return homeDirectory.get();
	}

	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory.set(homeDirectory);
	}

	public SimpleBooleanProperty downFIleProperty() {
		return downFIle;
	}

	public Boolean getDownFIle() {
		return downFIle.getValue();
	}

	public void setDownFIle(Boolean downFIle) {
		this.downFIle.set(downFIle);
	}

	public SimpleBooleanProperty upFileProperty() {
		return upFile;
	}

	public Boolean getUpFile() {
		return upFile.getValue();
	}

	public void setUpFile(Boolean upFile) {
		this.upFile.set(upFile);
	}

	public SimpleBooleanProperty deleteFileProperty() {
		return deleteFile;
	}

	public Boolean getDeleteFile() {
		return deleteFile.getValue();
	}

	public void setDeleteFile(Boolean deleteFile) {
		this.deleteFile.set(deleteFile);
	}

	public SimpleBooleanProperty isEnabledProperty() {
		return isEnabled;
	}

	public Boolean getIsEnabled() {
		return isEnabled.getValue();
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled.set(isEnabled);
	}

}
