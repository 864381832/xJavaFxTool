package com.xwintop.xcore.base;

@Deprecated
public class XProperty<T> {
	private String name;
	private T bean;

	public XProperty() {
		this.name = "";
	}

	public XProperty(String name) {
		this.name = name;
	}

	public XProperty(String name, T bean) {
		this.bean = bean;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getBean() {
		return bean;
	}

	public void setBean(T bean) {
		this.bean = bean;
	}

}
