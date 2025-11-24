package com.xwintop.xJavaFxTool.view.debugTools;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import lombok.Getter;

@Getter
public abstract class HttpToolView implements Initializable {
	@FXML
	protected TextField urlTextField;
	@FXML
	protected ChoiceBox<String> methodChoiceBox;
	@FXML
	protected Button sendButton;
	@FXML
	protected Button toBrowerButton;
	@FXML
	protected CheckBox paramsDataCheckBox;
    @FXML
    private Button addParamsDataButton;
    @FXML
	protected CheckBox paramsDataIsStringCheckBox;
    @FXML
	protected TextArea paramsDataTextArea;
	@FXML
	protected TableView<Map<String, String>> paramsDataTableView;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsDataNameTableColumn;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsDataValueTableColumn;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsDataRemarkTableColumn;
	@FXML
	protected CheckBox paramsHeaderCheckBox;
	@FXML
    private Button addParamsHeaderButton;
	@FXML
	protected TableView<Map<String, String>> paramsHeaderTableView;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsHeaderNameTableColumn;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsHeaderValueTableColumn;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsHeaderRemarkTableColumn;
	@FXML
	protected CheckBox paramsCookieCheckBox;
    @FXML
    private Button addParamsCookieButton;
	@FXML
	protected TableView<Map<String, String>> paramsCookieTableView;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsCookieNameTableColumn;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsCookieValueTableColumn;
	@FXML
	protected TableColumn<Map<String, String>, String> paramsCookieRemarkTableColumn;
	@FXML
	protected TextArea ResponseBodyTextArea;
	@FXML
	protected TextArea ResponseHeaderTextArea;
	@FXML
	protected WebView ResponseHtmlWebView;
	@FXML
	protected ImageView ResponseImgImageView;

}