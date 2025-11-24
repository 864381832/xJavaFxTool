package com.xwintop.xJavaFxTool.controller.games.core;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础展示类
 * 
 * @author flyfox
 * @date 2014年11月4日
 */
public abstract class FFScreen extends Canvas implements FFEvent {
	protected enum GameState {
		GAME_MENU, GAME_START, GAME_CONTINUE, GAME_HELP, GAME_SET, GAME_EXIT, GAME_PAUSE
	};

	private List<FFObject> mObjects = new ArrayList<FFObject>();
	private Timeline timeline;
	private KeyFrame keyFrame;
	private int duration = 10;
	protected GameState mGameState = GameState.GAME_MENU;

	public FFScreen(double width, double height) {
		super(width, height);
		initTimeLine();
	}

	public void initEvents() {
		getParent().getScene().setOnKeyPressed(event -> {
			onKeyPressed(event);
		});

		getParent().getScene().setOnKeyReleased(event -> {
			onKeyReleased(event);
		});

		getParent().getScene().setOnMouseMoved(event -> {
			onMouseMoved(event);
		});
	}

	public void onKeyPressed(KeyEvent event) {
		for (FFObject wObject : mObjects) {
			wObject.onKeyPressed(event);
		}
	}

	public void onKeyReleased(KeyEvent event) {
		for (FFObject wObject : mObjects) {
			wObject.onKeyReleased(event);
		}
	}

	public void onMouseMoved(MouseEvent event) {
		for (FFObject wObject : mObjects) {
			wObject.onMouseMoved(event);
		}
	}

	/**
	 * add the object
	 * 
	 * @param baseObject
	 *            the object to be add
	 */
	public void addObject(FFObject baseObject) {
		this.mObjects.add(baseObject);
	}

	/**
	 * remove the object
	 * 
	 * @param baseObject
	 *            the object to be remove
	 */
	public void removeObject(FFObject baseObject) {
		this.mObjects.remove(baseObject);
	}

	/**
	 * remove the object with the index
	 * 
	 * @param index
	 *            the index of the object
	 */
	public void removeObjectAtIndex(int index) {
		this.mObjects.remove(index);
	}

	/**
	 * draw the objects
	 * 
	 * @param gc
	 */
	public void draw(GraphicsContext gc) {
		gc.setEffect(null);
		gc.clearRect(0, 0, getWidth(), getHeight());
		for (int i = 0; i < mObjects.size(); i++) {
			FFObject wObject = mObjects.get(i);
			if (wObject.isVisible()) {
				wObject.draw(gc);
			}
		}
	}

	/**
	 * update all the objects
	 */
	public void update() {
		for (int i = 0; i < mObjects.size(); i++) {
			FFObject wObject = mObjects.get(i);
			if (wObject.isUpdate()) {
				mObjects.get(i).update();
			}
		}
	}

	/**
	 * init the timeline
	 */
	private void initTimeLine() {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		keyFrame = new KeyFrame(Duration.millis(duration), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				draw(getGraphicsContext2D());
				update();
			}
		});
		timeline.getKeyFrames().add(keyFrame);
	}

	/**
	 * start the update timeline
	 */
	public void start() {
		timeline.play();
	}

	/**
	 * pause the update timeline
	 */
	public void pause() {
		timeline.pause();
	}

	/**
	 * stop the update timeline
	 */
	public void stop() {
		timeline.stop();
	}

}