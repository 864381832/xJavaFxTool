package com.xwintop.xJavaFxTool.controller.codeTools;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.xwintop.xcore.util.javafx.AlertUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * @ClassName: RegexTesterController
 * @Description: 正则表达式生成工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:02
 */
public class RegexTesterController implements Initializable {
	@FXML
	private TextField regexTextField;
	@FXML
	private Button regulatButton;
	@FXML
	private TableView<Map<String, String>> examplesTableView;
	@FXML
	private TableColumn<Map<String, String>,String> examplesTableColumn0;
	@FXML
	private TableColumn<Map<String, String>,String> examplesTableColumn1;
	@FXML
	private TextArea sourceTextArea;
	@FXML
	private TextArea matchTextArea;
	@FXML
	private TableView<Map<String, String>> matchTableView;
	@FXML
	private TableColumn<Map<String, String>,String> matchTableColumn0;
	@FXML
	private TableColumn<Map<String, String>,String> matchTableColumn1;
	@FXML
	private TableColumn<Map<String, String>,String> matchTableColumn2;
	@FXML
	private TableColumn<Map<String, String>,String> matchTableColumn3;
	@FXML
	private TableColumn<Map<String, String>,String> matchTableColumn4;
	@FXML
	private Button resetButton;
	@FXML
	private Button aboutRegularButton;
	@FXML
	private CheckBox ignoreCaseCheckBox;
	@FXML
	private TextField replaceTextField;
	@FXML
	private CheckBox isReplaceCheckBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			initView();
			initEvent();
		} catch (Exception e) {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() throws Exception {
		examplesTableColumn0.setCellValueFactory(new MapValueFactory("column0"));
		examplesTableColumn1.setCellValueFactory(new MapValueFactory("column1"));
		PropertiesConfiguration pcfg = new PropertiesConfiguration("data/regexData.properties");
		pcfg.getKeys().forEachRemaining((String key) -> {
			Map<String, String> map = new HashMap<String, String>();
			map.put("column0", key);
			map.put("column1", pcfg.getString(key));
			examplesTableView.getItems().add(map);
		});
		matchTableColumn0.setCellValueFactory(new MapValueFactory("column0"));
		matchTableColumn1.setCellValueFactory(new MapValueFactory("column1"));
		matchTableColumn2.setCellValueFactory(new MapValueFactory("column2"));
		matchTableColumn3.setCellValueFactory(new MapValueFactory("column3"));
		matchTableColumn4.setCellValueFactory(new MapValueFactory("column4"));
	}

	private void initEvent() {
		examplesTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					regexTextField.setText(examplesTableView.getSelectionModel().getSelectedItem().get("column1"));
                }
			}
		});
	}

	@FXML
	private void regulatAction(ActionEvent event) {
		String regexText = regexTextField.getText().trim();
		String sourceText = sourceTextArea.getText().trim();
		String replaceText = replaceTextField.getText();
		matchTableView.getItems().clear();
		Pattern p = null;
		if (ignoreCaseCheckBox.isSelected()) {
			p = Pattern.compile(regexText, Pattern.CASE_INSENSITIVE); // 不区分大小写
		} else {
			p = Pattern.compile(regexText);
		}
		// 用Pattern类的matcher()方法生成一个Matcher对象
		Matcher m = p.matcher(sourceText);
		StringBuffer sb = new StringBuffer();
		StringBuffer rsb = new StringBuffer(); // 替换匹配
		// 使用find()方法查找第一个匹配的对象
		boolean result = m.find();
		// 使用循环找出模式匹配的内容替换之,再将内容加到sb里
		int cnt = 0; // 匹配总数
		int start = 0;
		int end = 0;
		while (result) {
			m.appendReplacement(rsb, replaceText); // 替换匹配
			cnt++;
			sb.append("\n");
			start = m.start();
			end = m.end();
			String matchText = sourceText.substring(start, end);
			sb.append("Match[").append(cnt).append("]: ");
			sb.append(matchText);
			sb.append(" [start: ").append(start).append(", end: ").append(end).append("]");
			String str0 = m.group();			
			String str1 = "";
			try {
				str1 = m.group(1);								//捕获的子序列
			}catch(Exception e) {
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("column0", Integer.toString(cnt));
			map.put("column1", str0);
			map.put("column2", str1);
			map.put("column3", Integer.toString(start));
			map.put("column4", Integer.toString(end));
			matchTableView.getItems().add(map);
			result = m.find();
		}
		sb.insert(0, "\t匹配总数: " + cnt);
		if (isReplaceCheckBox.isSelected()&&replaceText.length() != 0) {
			m.appendTail(rsb);
			sb.append("\n\n替换匹配: ").append(rsb);
		}
		matchTextArea.setText(sb.length() > 0 ? sb.substring(1) : "");
	}

	@FXML
	private void resetAction(ActionEvent event) {
		regexTextField.setText(null);
		sourceTextArea.setText(null);
		matchTextArea.setText(null);
		matchTableView.getItems().clear();
	}

	@FXML
	private void aboutRegularAction(ActionEvent event) {
		AlertUtil.showInfoAlert("到底什么是正则表达式？","在编写处理字符串的程序或网页时，经常有查找符合某些复杂规则的字符串的需要。正则表达式就是用于描述这些规则的工具。换句话说，正则表达式就是记录文本规则的代码。");
	}
}
