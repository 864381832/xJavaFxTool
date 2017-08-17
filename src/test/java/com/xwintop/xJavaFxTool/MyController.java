package com.xwintop.xJavaFxTool;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class MyController implements Initializable {

	@FXML
	private Button myButton;

	@FXML
	private TextField myTextField;
	@FXML
	private Slider testJFXSlider;
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// TODO (don't really need to do anything here).
		System.out.println(testJFXSlider.getValue());
	}

	// When user click on myButton
	// this method will be called.
	public void showDateTime(ActionEvent event) {
		System.out.println("Button Clicked!");
		System.out.println(event);

		Date now = new Date();

		DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		String dateTimeString = df.format(now);
		// Show in VIEW
		myTextField.setText(dateTimeString);
	}

}
