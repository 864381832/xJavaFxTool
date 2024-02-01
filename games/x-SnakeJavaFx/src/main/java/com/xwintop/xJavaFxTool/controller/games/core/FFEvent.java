package com.xwintop.xJavaFxTool.controller.games.core;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * 基础事件类，利用JDK8默认时间特性
 * 
 * @author flyfox
 * @date 2014年11月4日
 */
public interface FFEvent {

	/**
	 * 初始化事件
	 * 
	 */
	default public void init() {
	}

	/**
	 * 默认按键按下事件
	 * 
	 * @param event
	 */
	default public void onKeyPressed(KeyEvent event) {
	}

	/**
	 * 默认按键释放事件
	 * 
	 * @param event
	 */
	default public void onKeyReleased(KeyEvent event) {
	}

	/**
	 * 默认鼠标移动事件
	 * 
	 * @param event
	 */
	default public void onMouseMoved(MouseEvent event) {
	}
}
