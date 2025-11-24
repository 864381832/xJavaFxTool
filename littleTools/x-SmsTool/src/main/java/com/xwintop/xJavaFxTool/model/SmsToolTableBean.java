package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.*;

/**
 * @ClassName: PhoneToolTableBean
 * @Description: 邮件发送工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:14
 */
public class SmsToolTableBean {
	private SimpleIntegerProperty order;// 序号
	private SimpleBooleanProperty isEnabled;
	private SimpleStringProperty toPhone;
	private SimpleStringProperty toPhoneName;
	private SimpleStringProperty sendStatus;

	public SmsToolTableBean(Integer order, Boolean isEnabled, String toPhone, String toPhoneName, String sendStatus){
		this.order = new SimpleIntegerProperty(order);
		this.isEnabled = new SimpleBooleanProperty(isEnabled);
		this.toPhone = new SimpleStringProperty(toPhone);
		this.toPhoneName = new SimpleStringProperty(toPhoneName);
		this.sendStatus = new SimpleStringProperty(sendStatus);
	}

	public SmsToolTableBean(String propertys) {
		String[] strings = propertys.split("__",5);
		this.order = new SimpleIntegerProperty(Integer.valueOf(strings[0]));
		this.isEnabled = new SimpleBooleanProperty(Boolean.valueOf(strings[1]));
		this.toPhone = new SimpleStringProperty(strings[2]);
		this.toPhoneName = new SimpleStringProperty(strings[3]);
		this.sendStatus = new SimpleStringProperty(strings[4]);
	}

	public String getPropertys() {
		return order.get() + "__" + isEnabled.get() + "__" + toPhone.get() + "__" + toPhoneName.get() + "__" + sendStatus.get();
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

	public String getToPhone(){
		return toPhone.get();
	}

	public void setToPhone(String toPhone){
		this.toPhone.set(toPhone);
	}

	public String getToPhoneName(){
		return toPhoneName.get();
	}

	public void setToPhoneName(String toPhoneName){
		this.toPhoneName.set(toPhoneName);
	}

	public String getSendStatus(){
		return sendStatus.get();
	}

	public void setSendStatus(String sendStatus){
		this.sendStatus.set(sendStatus);
	}
}
