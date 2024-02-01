package com.xwintop.xJavaFxTool.controller.games.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;

/**
 * 基础对象类
 * 
 * @author flyfox
 * @date 2014年11月4日
 */
public abstract class FFObject implements FFEvent {
	protected DoubleProperty widthProperty = new SimpleDoubleProperty(0);
	protected DoubleProperty heightProperty = new SimpleDoubleProperty(0);
	protected DoubleProperty xProperty = new SimpleDoubleProperty(0);
	protected DoubleProperty yProperty = new SimpleDoubleProperty(0);
	protected BooleanProperty updateProperty = new SimpleBooleanProperty(true);
	protected BooleanProperty visibleProperty = new SimpleBooleanProperty(true);

	public FFObject(double x, double y, double width, double height) {
		this.xProperty = new SimpleDoubleProperty(x);
		this.yProperty = new SimpleDoubleProperty(y);
		this.widthProperty = new SimpleDoubleProperty(width);
		this.heightProperty = new SimpleDoubleProperty(height);
	}

	public FFObject() {
		this.xProperty = new SimpleDoubleProperty(0);
		this.yProperty = new SimpleDoubleProperty(0);
		this.widthProperty = new SimpleDoubleProperty(0);
		this.heightProperty = new SimpleDoubleProperty(0);
	}

	@Override
	public void init() {
		setVisible(true);
	}

	public abstract void draw(GraphicsContext gc);

	public abstract void update();

	public DoubleProperty widthProperty() {
		return widthProperty;
	}

	public double getWidth() {
		return widthProperty.get();
	}

	public void setWidth(double width) {
		this.widthProperty.set(width);
	}

	public DoubleProperty heightProperty() {
		return heightProperty;
	}

	public double getHeight() {
		return heightProperty.get();
	}

	public void setHeight(double height) {
		this.heightProperty.set(height);
	}

	public DoubleProperty xProperty() {
		return xProperty;
	}

	public double getX() {
		return xProperty.get();
	}

	public void setX(double x) {
		this.xProperty.set(x);
	}

	public DoubleProperty yProperty() {
		return yProperty;
	}

	public double getY() {
		return yProperty.get();
	}

	public void setY(double y) {
		this.yProperty.set(y);
	}

	public BooleanProperty updateProperty() {
		return updateProperty;
	}

	public void setUpdate(boolean isUpdate) {
		this.updateProperty.set(isUpdate);
	}

	public boolean isUpdate() {
		return updateProperty.get();
	}

	public void setVisible(boolean isVisible) {
		this.visibleProperty.set(isVisible);
	}

	public boolean isVisible() {
		return visibleProperty.get();
	}

	public void moveX(double x) {
		this.xProperty.set(getX() + x);
	}

	public void moveY(double y) {
		this.yProperty.set(getY() + y);
	}

	public boolean isCollisionWith(FFObject baseObject) {
		if (getX() + getWidth() > baseObject.getX() && getX() < baseObject.getX() + baseObject.getWidth()
				&& getY() + getHeight() > baseObject.getY() && getY() < baseObject.getY() + baseObject.getHeight()) {
			return true;
		}
		return false;
	}
}