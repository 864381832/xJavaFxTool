package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.*;

/**
 * @ClassName: EmailToolTableBean
 * @Description: 邮件发送工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:14
 */
public class EmailToolTableBean {
	private SimpleIntegerProperty order;// 序号
	private SimpleBooleanProperty isEnabled;
	private SimpleStringProperty toEmail;
	private SimpleStringProperty toEmailName;
	private SimpleStringProperty sendStatus;

	public EmailToolTableBean(Integer order,Boolean isEnabled,String toEmail,String toEmailName,String sendStatus){
		this.order = new SimpleIntegerProperty(order);
		this.isEnabled = new SimpleBooleanProperty(isEnabled);
		this.toEmail = new SimpleStringProperty(toEmail);
		this.toEmailName = new SimpleStringProperty(toEmailName);
		this.sendStatus = new SimpleStringProperty(sendStatus);
	}

	public EmailToolTableBean(String propertys) {
		String[] strings = propertys.split("__",5);
		this.order = new SimpleIntegerProperty(Integer.valueOf(strings[0]));
		this.isEnabled = new SimpleBooleanProperty(Boolean.valueOf(strings[1]));
		this.toEmail = new SimpleStringProperty(strings[2]);
		this.toEmailName = new SimpleStringProperty(strings[3]);
		this.sendStatus = new SimpleStringProperty(strings[4]);
	}

	public String getPropertys() {
		return order.get() + "__" + isEnabled.get() + "__" + toEmail.get() + "__" + toEmailName.get() + "__" + sendStatus.get();
	}

	public IntegerProperty orderProperty(){
		return order;
	}

	public Integer getOrder(){
		return order.get();
	}

	public void setOrder(Integer order){
		this.order.set(order);
	}

	public BooleanProperty isEnabledProperty(){
		return isEnabled;
	}

	public Boolean getIsEnabled(){
		return isEnabled.get();
	}

	public void setIsEnabled(Boolean isEnabled){
		this.isEnabled.set(isEnabled);
	}

	public String getToEmail(){
		return toEmail.get();
	}

	public void setToEmail(String toEmail){
		this.toEmail.set(toEmail);
	}

	public String getToEmailName(){
		return toEmailName.get();
	}

	public void setToEmailName(String toEmailName){
		this.toEmailName.set(toEmailName);
	}

	public String getSendStatus(){
		return sendStatus.get();
	}

	public void setSendStatus(String sendStatus){
		this.sendStatus.set(sendStatus);
	}
}
