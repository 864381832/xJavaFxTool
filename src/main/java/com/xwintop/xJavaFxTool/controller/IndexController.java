package com.xwintop.xJavaFxTool.controller;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
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
		initView();
		initEvent();
	}
	
	private void initView(){
//		myTextField.setText(bundle.getString("Title"));
		Map<String,String> map = new HashMap<String,String>();
		map.put("epms转换", "/fxml/epmsTools/GeneratingCode.fxml");
		map.put("epms调试工具", "/fxml/epmsTools/DebugEpms.fxml");
		map.put("javaFx转换", "/fxml/javaFxTools/JavaFxXmlToObjectCode.fxml");
		map.put("路径转换", "/fxml/littleTools/LinuxPathToWindowsPath.fxml");
		map.put("报文查看", "/fxml/epmsTools/MessageViewer.fxml");
		map.put("Time转换", "/fxml/littleTools/TimeTool.fxml");
		map.put("编码转换", "/fxml/littleTools/CharacterConverter.fxml");
		map.put("加密解密", "/fxml/littleTools/EncryptAndDecrypt.fxml");
		map.forEach(new BiConsumer<String,String>() {
			@Override
			public void accept(String t, String u) {
				Tab tab = new Tab(t);
				ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.Menu", Locale.CHINA);
				FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(u), resourceBundle);
				try {
					tab.setContent(generatingCodeFXMLLoader.load());
				} catch (IOException e) {
					e.printStackTrace();
				}
				tabPaneMain.getTabs().add(tab);
			}
		});
	}
	private void initEvent(){
		myButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showDateTime(arg0);
//				TooltipUtil.showToast(myButton,"test");
				TooltipUtil.showToast("test");
				TooltipUtil.showToast("test",Pos.BOTTOM_RIGHT);
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
}
