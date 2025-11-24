package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @ClassName: ScriptEngineToolTableBean
 * @Description: 脚本调试工具表bean
 * @author: xufeng
 * @date: 2018/1/27 18:02  
 */

public class ScriptEngineToolTableBean {
	private SimpleStringProperty order;// 序列
	private SimpleBooleanProperty isEnabled;// 是否启用
	private SimpleStringProperty script;// 脚本
	private SimpleStringProperty type;// 执行类型
	private SimpleStringProperty parameter;// 参数
	private SimpleBooleanProperty isRunAfterActivate;// 是否执行触发事件
	private SimpleStringProperty runAfterActivate;// 执行后触发事件
	private SimpleStringProperty remarks;// 备注

	public ScriptEngineToolTableBean(String order, Boolean isEnabled, String script, String type, String parameter,
                                     Boolean isRunAfterActivate, String runAfterActivate, String remarks) {
		this.order = new SimpleStringProperty(order);
		this.isEnabled = new SimpleBooleanProperty(isEnabled);
		this.script = new SimpleStringProperty(script);
		this.type = new SimpleStringProperty(type);
		this.parameter = new SimpleStringProperty(parameter);
		this.isRunAfterActivate = new SimpleBooleanProperty(isRunAfterActivate);
		this.runAfterActivate = new SimpleStringProperty(runAfterActivate);
		this.remarks = new SimpleStringProperty(remarks);
	}

	public ScriptEngineToolTableBean(String propertys) {
		String[] strings = propertys.split("__", 8);
		this.order = new SimpleStringProperty(strings[0]);
		this.isEnabled = new SimpleBooleanProperty(Boolean.valueOf(strings[1]));
		this.script = new SimpleStringProperty(strings[2]);
		this.type = new SimpleStringProperty(strings[3]);
		this.parameter = new SimpleStringProperty(strings[4]);
		this.isRunAfterActivate = new SimpleBooleanProperty(Boolean.valueOf(strings[5]));
		this.runAfterActivate = new SimpleStringProperty(strings[6]);
		this.remarks = new SimpleStringProperty(strings[7]);
	}

	public String getPropertys() {
		return order.get() + "__" + isEnabled.get() + "__" + script.get() + "__" + type.get() + "__" + parameter.get()
				+ "__" + isRunAfterActivate.get() + "__" + runAfterActivate.get() + "__" + remarks.get();
	}

	public String getOrder() {
		return order.get();
	}

	public void setOrder(String order) {
		this.order.set(order);
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

	public String getScript() {
		return script.get();
	}

	public void setScript(String script) {
		this.script.set(script);
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getParameter() {
		return parameter.get();
	}

	public void setParameter(String parameter) {
		this.parameter.set(parameter);
	}

	public BooleanProperty isRunAfterActivateProperty() {
		return isRunAfterActivate;
	}

	public Boolean getIsRunAfterActivate() {
		return isRunAfterActivate.get();
	}

	public void setIsRunAfterActivate(Boolean isRunAfterActivate) {
		this.isRunAfterActivate.set(isRunAfterActivate);
	}

	public String getRunAfterActivate() {
		return runAfterActivate.get();
	}

	public void setRunAfterActivate(String runAfterActivate) {
		this.runAfterActivate.set(runAfterActivate);
	}

	public String getRemarks() {
		return remarks.get();
	}

	public void setRemarks(String remarks) {
		this.remarks.set(remarks);
	}

}
