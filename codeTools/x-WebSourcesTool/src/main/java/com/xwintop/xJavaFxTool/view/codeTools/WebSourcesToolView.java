package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class WebSourcesToolView implements Initializable {
	@FXML
	protected TextField urlTextField;
	@FXML
	protected Button jumpButton;
	@FXML
	protected Button browserOpenButton;
	@FXML
	protected Button downloadButton;
	@FXML
	protected WebView showHrmlWebView;

}