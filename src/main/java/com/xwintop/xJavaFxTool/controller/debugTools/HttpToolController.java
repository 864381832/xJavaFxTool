package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.view.debugTools.HttpToolView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HttpToolController extends HttpToolView {
	private String[] methodStrings = new String[] { "GET", "POST", "HEAD", "PUT", "PATCH", "DELETE" };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		methodChoiceBox.getItems().addAll(methodStrings);
		methodChoiceBox.setValue(methodStrings[0]);
	}

	private void initEvent() {
	}

	private void initService() {
	}

	@FXML
	private void sendAction(ActionEvent event) {
	}

	@FXML
	private void toBrowerAction(ActionEvent event) {
	}
}