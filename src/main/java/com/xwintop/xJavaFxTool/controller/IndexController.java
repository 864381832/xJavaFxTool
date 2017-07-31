package com.xwintop.xJavaFxTool.controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.xml.soap.Node;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import javafx.util.Duration;

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
//				TooltipUtil.showToast(myButton,"test");
				TooltipUtil.showToast("test");
//				JOptionPane.showMessageDialog(null, "test");
//				AlertUtil.showWarnAlert("showConfirmAlert");
				
//				Notifications notificationBuilder = Notifications.create()
//		                .title("Title Text")
//		                .text("showText")
//		                .graphic(null)
//		                .hideAfter(Duration.seconds(1))
//		                .position(Pos.BOTTOM_CENTER)
//		                .onAction(new EventHandler<ActionEvent>() {
//		                    @Override public void handle(ActionEvent arg0) {
//		                        System.out.println("Notification clicked on!");
//		                    }
//		                });
//				notificationBuilder.owner(myButton);
//	            notificationBuilder.hideCloseButton();
//	            notificationBuilder.darkStyle();
//				notificationBuilder.show();
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
