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

	public SimpleStringProperty getName() {
		return name;
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public SimpleStringProperty getType() {
		return type;
	}

	public void setType(SimpleStringProperty type) {
		this.type = type;
	}

	public SimpleStringProperty getSize() {
		return size;
	}

	public void setSize(SimpleStringProperty size) {
		this.size = size;
	}

	public SimpleStringProperty getTime() {
		return time;
	}

	public void setTime(SimpleStringProperty time) {
		this.time = time;
	}

}
