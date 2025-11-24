package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.controller.games.core.FFContants;
import com.xwintop.xJavaFxTool.controller.games.core.FFObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Information extends FFObject {

	private boolean isLive;
	private int score = 0;
	private int hp = 0;

	public Information() {
		init();
	}

	@Override
	public void init() {
		super.init();

		score = 0;
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.setFill(Color.GREEN);
		gc.fillText("生命：" + getHp() + (getHp() == 0 ? "（死亡）" : ""), FFContants.WIDTH - 100, 20);
		gc.fillText("积分：" + getScore(), FFContants.WIDTH - 100, 40);
		gc.fillText("P键暂停", FFContants.WIDTH - 100, 60);
		gc.fillText("S键重新开始", FFContants.WIDTH - 100, 80);
	}

	@Override
	public void update() {

	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public int getScore() {
		return score * 10;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

}
