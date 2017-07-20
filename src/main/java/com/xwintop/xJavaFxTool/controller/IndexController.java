package com.xwintop.xJavaFxTool.controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * @ClassName: IndexController
 * @Description: TODO
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
public class IndexController implements Initializable {
	private ResourceBundle bundle;
	@FXML
	private Button myButton;

	@FXML
	private TextField myTextField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
//		System.out.println(myTextField.getText());
		// myTextField.setText(bundle.getString("Title"));
	}

	// When user click on myButton
	// this method will be called.
	public void showDateTime(ActionEvent event) {
		System.out.println("Button Clicked!");
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		String dateTimeString = df.format(now);
		// Show in VIEW
		myTextField.setText(dateTimeString);
	}

	public void setText() {

	}

}
