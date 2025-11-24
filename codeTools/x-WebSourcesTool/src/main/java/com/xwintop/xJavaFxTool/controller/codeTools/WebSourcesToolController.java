package com.xwintop.xJavaFxTool.controller.codeTools;

import com.xwintop.xJavaFxTool.services.codeTools.WebSourcesToolService;
import com.xwintop.xJavaFxTool.view.codeTools.WebSourcesToolView;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: WebSourcesToolController
 * @Description: 网页源码下载工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:03
 */

@Getter
@Setter
@Slf4j
public class WebSourcesToolController extends WebSourcesToolView {
	private WebSourcesToolService webSourcesToolService = new WebSourcesToolService(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		urlTextField.setText("http://www.xwintop.com");
		showHrmlWebView.getEngine().load("http://www.xwintop.com");
	}

	private void initEvent() {
		WebEngine webEngine = showHrmlWebView.getEngine();
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == State.SUCCEEDED) {
				System.out.println("finished loading");
				org.w3c.dom.Document xmlDom = webEngine.getDocument();
				System.out.println(xmlDom);
			}
		}); // addListener()
		urlTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					jumpAction(null);
				}
			}
		});
	}

	private void initService() {
	}

	@FXML
	private void jumpAction(ActionEvent event) {
		if (StringUtils.isEmpty(urlTextField.getText())) {
			TooltipUtil.showToast("请输入网站！！！");
			return;
		}
		showHrmlWebView.getEngine().load(urlTextField.getText());
	}

	@FXML
	private void browserOpenAction(ActionEvent event) {
		if (StringUtils.isEmpty(urlTextField.getText())) {
			TooltipUtil.showToast("请输入网站！！！");
			return;
		}
		try {
            JavaFxSystemUtil.openBrowseURLThrowsException(urlTextField.getText());
		} catch (Exception e1) {
			TooltipUtil.showToast("输入Url有误！" + e1.getMessage());
			log.error(e1.getMessage());
		}
	}

	@FXML
	private void downloadAction(ActionEvent event) throws Exception {
		webSourcesToolService.downloadHtmlSources();
		WebEngine webEngine = showHrmlWebView.getEngine();
//		WebPage page = (WebPage) FieldUtils.readDeclaredField(webEngine, "page", true);
		System.out.println(webEngine.getTitle());
	}
}