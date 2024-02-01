package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.controller.games.core.FFObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class SnakeBody extends FFObject {
	com.xwintop.xJavaFxTool.controller.games.Snake snake;
	int length; // 方向
	LinkedList<Point> list = new LinkedList<Point>();

	public SnakeBody(com.xwintop.xJavaFxTool.controller.games.Snake snake) {
		this.snake = snake;
		// 属性绑定
		this.xProperty.bindBidirectional(snake.xProperty());
		this.yProperty.bindBidirectional(snake.yProperty());
		this.widthProperty.bindBidirectional(snake.widthProperty());
		this.heightProperty.bindBidirectional(snake.heightProperty());
		// 初始化位置列表
		init();
	}

	public void init() {
		super.init();
		
		this.length = snake.length;
		
		list.clear();
		for (int i = 0; i < snake.getLength(); i++) {
			Point point = new Point();
			point.setX(getX() - getWidth() * i);
			point.setY(getY());
			list.add(point);
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		if (snake.getLength() <= 1) {
			return;
		}

		// 原理：移动一次，那么后一个的位置就等于前一个的位置，也就是加入新的first，删除旧的last
		Point firstPoi = list.getFirst();
		// 位移已经达到一个身位再进行处理
		if (firstPoi.getX() + getWidth() <= getX() || firstPoi.getX() - getWidth() >= getX() //
				|| firstPoi.getY() + getHeight() <= getY() || firstPoi.getY() - getHeight() >= getY()) {
			Point poi = new Point();
			poi.setX(getX());
			poi.setY(getY());
			// 添加第一个头
			list.addFirst(poi);
			// 如果吃到了就不移除了，没吃到就删除最后一个
			if (this.length < snake.length) {
				this.length = this.length + 1;
			} else {
				// 移除最后一个
				list.removeLast();
			}
		}

		gc.setFill(snake.getSnakeColor());
		// 调试用这个颜色
		gc.setFill(Color.BLUE);
		for (Point point : list) {
			// 为了连贯性~第一个点也画出来了
			gc.fillRect(point.getX(), point.getY(), getWidth(), getHeight());
		}
	}

	@Override
	public void update() {
		// 如果不显示了，重新展示
		if (!isVisible()) {
			init();
			setVisible(true);
			return;
		}
	}

	@Override
	public boolean isCollisionWith(FFObject baseObject) {
		for (Point point : list) {
			if (list.getFirst().equals(point))
				continue;

			if (isCollisionWith(point.getX(), point.getY(), baseObject))
				return true;
		}
		return false;
	}

	private boolean isCollisionWith(double x, double y, FFObject baseObject) {
		if (x + getWidth() > baseObject.getX() && x < baseObject.getX() + baseObject.getWidth()
				&& y + getHeight() > baseObject.getY() && y < baseObject.getY() + baseObject.getHeight()) {
			return true;
		}
		return false;
	}

}
