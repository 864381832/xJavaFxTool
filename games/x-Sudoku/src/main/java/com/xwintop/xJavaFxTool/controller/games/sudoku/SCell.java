package com.xwintop.xJavaFxTool.controller.games.sudoku;

import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

public class SCell extends Button {

	public CStatus status;
	public Boolean isHighLight;

	public SCell() {
		isHighLight = false;
		setInitStatus();
	}

	public void setInitStatus() {
		setEffect(null);
		setStyle("-fx-text-fill: rgb(132, 117, 225)");
		status = CStatus.INIT;
		isHighLight = false;
	}

	public void setErrorStatus() {
		setStyle("-fx-text-fill: red");
		setText("X");
		status = CStatus.ERROR;
	}

	public void setHighLightStatus() {
		InnerShadow innerShadow = new InnerShadow();
		innerShadow.setRadius(50);
		innerShadow.setWidth(30);
 		innerShadow.setColor(Color.DEEPSKYBLUE);
		setEffect(innerShadow);
		isHighLight = true;
	}

	/**
	 * set note mode button and candidates button Status
	 */
	public void setNoteStatus(int fontSize) {
		setStyle(String.format("-fx-text-fill: black; -fx-font-weight: normal; -fx-font-size: %d", fontSize));
		status = CStatus.NOTE;
	}

	public void setNoteText(String input) {
		if (input.isEmpty() || !"123456789".contains(input)) return;
		int fontSize = 20;
		String cellText = getText();
		if (status.equals(CStatus.ERROR)) {
			cellText = "";
		}
		if (!status.equals(CStatus.INIT) || cellText.isEmpty()) {
			if (cellText.contains(input)) cellText = String.join("", cellText.split(input));
			else cellText += input;
			if (cellText.length() <= 2) fontSize = 24 - cellText.length() * 4;
			else fontSize = 12;
			setNoteStatus(fontSize);
			wrapTextProperty().setValue(true);
			setText(cellText);
		}
	}
}