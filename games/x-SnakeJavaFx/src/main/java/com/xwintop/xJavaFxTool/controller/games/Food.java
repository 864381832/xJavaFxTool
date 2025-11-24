package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.controller.games.core.FFContants;
import com.xwintop.xJavaFxTool.controller.games.core.FFObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.security.SecureRandom;

public class Food extends FFObject {

	private SecureRandom random = new SecureRandom();

	public Food() {
		init();
	}

	public void init() {
		super.init();
		
		createRandomFood();
		setWidth(FFContants.MIN_X);
		setHeight(FFContants.MIN_Y);
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.setFill(Color.RED);
		gc.fillOval(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void update() {
		if (!isVisible()) {
			createRandomFood();
			setVisible(true);
		}
	}

	private void createRandomFood() {
		// 保证是在最小单元上，不会错位
		int x = random.nextInt(FFContants.WIDTH / FFContants.MIN_X) * FFContants.MIN_X;
		int y = random.nextInt(FFContants.HEIGHT / FFContants.MIN_Y) * FFContants.MIN_Y;
		setX(x);
		setY(y);
	}

}
