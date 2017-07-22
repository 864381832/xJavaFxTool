package com.xwintop.xJavaFxTool.controller.epmsTools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.services.epmsTools.TextToHibernateXmlService;
import com.xwintop.xJavaFxTool.services.epmsTools.XmlToBeanService;
import com.xwintop.xJavaFxTool.services.epmsTools.XmlToCodeService;
import com.xwintop.xJavaFxTool.services.epmsTools.XmlToSqlService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class GeneratingCodeController extends AnchorPane implements Initializable {
	private ResourceBundle bundle;
	@FXML
	private TextField textField1;
	@FXML
	private TextField textField2;
	@FXML
	private TextArea textArea1;
	@FXML
	private TextArea textArea2;
	@FXML
	private TextArea textArea3;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
	}
	
	@FXML
	public void xmlToSql(ActionEvent event){
		String string = XmlToSqlService.xmlToSql(textArea1.getText(),textField1.getText(),textField2.getText());
		textArea3.setText(string);
	}
	public void xmlToBean(ActionEvent event){
		String string = XmlToBeanService.xmlTobean(textArea1.getText());
		textArea3.setText(string);
	}
	
	public void xmlTemplateToCode(ActionEvent event) throws Exception{
		String string = XmlToCodeService.xmlTemplateToCode(textArea1.getText(),textArea2.getText(),textField1.getText(),textField2.getText());
		textArea3.setText(string);
	}
	
	public void textToHibernateXml(ActionEvent event) throws Exception{
		String string = TextToHibernateXmlService.textToHibernateXml(textArea1.getText(),textField1.getText(),textField2.getText());
		textArea3.setText(string);
	}
	
	/** 
	 * @Title: copyTextValueToClipboard 
	 * @Description: 拷贝内容到剪贴板
	 */
	public void copyTextValueToClipboard(ActionEvent event) throws Exception{
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // 获得系统剪贴板
		clipboard.setContents(new StringSelection(textArea3.getText()), null);
	}

}
