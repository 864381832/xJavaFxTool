package com.xwintop.xJavaFxTool.controller.codeTools;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegexTesterController implements Initializable {
	@FXML
	private TextField regexTextField;
	@FXML
	private Button regulatButton;
	@FXML
	private TableView<Map<String, String>> examplesTableView;
	@FXML
	private TextArea sourceTextArea;
	@FXML
	private TableView<Map<String, String>> matchTableView;
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

	private void initView() throws Exception {
		PropertiesConfiguration pcfg = new PropertiesConfiguration("data/regexData.properties");
	}

	private void initEvent() {
	}

	@FXML
	private void regulatAction(ActionEvent event) {
	}

	@FXML
	private void resetAction(ActionEvent event) {
	}

	@FXML
	private void aboutRegularAction(ActionEvent event) {
	}
}
