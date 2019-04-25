package com.xwintop.xJavaFxTool.controller.javaFxTools;

import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.services.javaFxTools.JavaFxXmlToObjectCodeService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * @ClassName: JavaFxXmlToObjectCodeController
 * @Description: javaFx Xml转对象工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:24
 */

public class JavaFxXmlToObjectCodeController implements Initializable {
	@FXML
	private TextArea textArea1;
	@FXML
	private TextArea textArea2;
	@FXML
	private TextArea textArea3;
	@FXML
	private TextArea textArea4;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	@FXML
	private void xmlToCodeOnAction(ActionEvent event) throws Exception{
		String[] string = JavaFxXmlToObjectCodeService.xmlToCode(textArea1.getText());
		textArea2.setText(string[0]);
		textArea3.setText(string[1]);
		textArea4.setText(string[2]);
	}

}
