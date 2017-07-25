package com.xwintop.xJavaFxTool.controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.xwintop.xcore.util.javafx.AlertUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
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
	
	@FXML
	private TabPane tabPaneMain;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
//		myTextField.setText(bundle.getString("Title"));
		init();
	}
	
	private void init(){
		myButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showDateTime(arg0);
//				JOptionPane.showMessageDialog(null, "test");
//				AlertUtil.showWarnAlert("showConfirmAlert");
			}
		});
	}

	// When user click on myButton
	// this method will be called.
	public void showDateTime(ActionEvent event) {
//		System.out.println("Button Clicked!");
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		String dateTimeString = df.format(now);
		// Show in VIEW
		myTextField.setText(dateTimeString);
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public Button getMyButton() {
		return myButton;
	}

	public void setMyButton(Button myButton) {
		this.myButton = myButton;
	}

	public TextField getMyTextField() {
		return myTextField;
	}

	public void setMyTextField(TextField myTextField) {
		this.myTextField = myTextField;
	}

	public TabPane getTabPaneMain() {
		return tabPaneMain;
	}

	public void setTabPaneMain(TabPane tabPaneMain) {
		this.tabPaneMain = tabPaneMain;
	}

}
