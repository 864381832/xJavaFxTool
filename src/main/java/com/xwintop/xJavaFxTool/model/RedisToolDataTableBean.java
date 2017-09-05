package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.SimpleStringProperty;

public class RedisToolDataTableBean {
	private SimpleStringProperty name;
	private SimpleStringProperty type;
	private SimpleStringProperty size;
	private SimpleStringProperty time;

	public RedisToolDataTableBean(SimpleStringProperty name, SimpleStringProperty type, SimpleStringProperty size,
			SimpleStringProperty time) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
		this.time = time;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getSize() {
		return size.get();
	}

	public void setSize(String size) {
		this.size.set(size);
	}

	public String getTime() {
		return time.get();
	}

	public void setTime(String time) {
		this.time.set(time);
	}

}
