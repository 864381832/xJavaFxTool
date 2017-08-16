package com.xwintop.xJavaFxTool.controller.webTools;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.xwintop.xJavaFxTool.services.webTools.ShortURLService;
import com.xwintop.xJavaFxTool.view.webTools.ShortURLView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class ShortURLController extends ShortURLView {

	private String[] shortURLService = new String[] { "百度", "新浪" };
	private RadioButton[] shortSiteRadioButtons = new RadioButton[60];
	private Map<String, String> shortSiteMap = new HashMap<String, String>();
	private ToggleGroup shortSiteToggleGroup = new ToggleGroup();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		longURLTextField.setText("http://www.baidu.com");
		shortURLServiceChoiceBox.getItems().addAll(shortURLService);
		shortURLServiceChoiceBox.setValue(shortURLServiceChoiceBox.getItems().get(0));
		resultTextArea.setText("说明：" + "\n1，百度短网址API：http://www.baidu.com/search/dwz.html"
				+ "\n2，新浪短网址API：新浪短网址http://t.cn/需要授权才可使用，此处转换中不列出，详细API说明如下："
				+ "\n                                http://open.weibo.com/wiki/2/short_url/shorten"
				+ "\n                                http://open.weibo.com/wiki/2/short_url/expand");
	}

	private void initEvent() {
		shortURLServiceChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(shortURLService[0].equals(newValue)) {
					for(int i=0;i<60;i++) {
						
					}
					
				}else if(shortURLService[1].equals(newValue)) {
					shortSiteMap.clear();
					shortSiteMap.put("sina.lt", "sinalt");
					shortSiteMap.put("t.cn", "sina");
					shortSiteMap.put("dwz.cn", "dwz");
					shortSiteMap.put("qq.cn.hn", "qq.cn.hn");
					shortSiteMap.put("tb.cn.hn", "tb.cn.hn");
					shortSiteMap.put("jd.cn.hn", "jd.cn.hn");
					shortSiteMap.put("tinyurl.com", "tinyurl");
					shortSiteMap.put("goo.gl", "googl");
					shortSiteMap.put("j.mp", "jmp");
					shortSiteMap.put("bit.ly", "bitly");
					int i = 0;
					for(String key:shortSiteMap.keySet()) {
						shortSiteRadioButtons[i] = new RadioButton(key);
						shortSiteRadioButtons[i].setLayoutX(13+80*i);
						shortSiteRadioButtons[i].setLayoutY(114);
						shortSiteRadioButtons[i].setToggleGroup(shortSiteToggleGroup);
						if(i == 0) {
							shortSiteRadioButtons[i].setSelected(true);
						}
						mainAnchorPane.getChildren().add(shortSiteRadioButtons[i]);
						i++;
					}
				}
			}
		});
	}

	@FXML
	private void longURLCopyAction(ActionEvent event) {
		StringSelection selection = new StringSelection(longURLTextField.getText());// 获取系统剪切板，复制长网址
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	@FXML
	private void convertAction(ActionEvent event) {
		String shortURLServiceType = shortURLServiceChoiceBox.getValue();
		String longUrlText = longURLTextField.getText().trim();
		if(StringUtils.isEmpty(longUrlText)) {
			return;
		}
		shortURLTextField.setText(null);
		if(shortURLService[0].equals(shortURLServiceType)) {
			String shortURL = null;
			if(aliasUrlCheckBox.isSelected()) {
				shortURL = ShortURLService.baiduToShort(longUrlText,aliasUrlTextField.getText());
			}else {
				shortURL = ShortURLService.baiduToShort(longUrlText,null);
			}
			shortURLTextField.setText(shortURL);
		}else if(shortURLService[1].equals(shortURLServiceType)) {
			String shortURL = null;
			String site = shortSiteMap.get(((RadioButton) shortSiteToggleGroup.getSelectedToggle()).getText());
			shortURL = ShortURLService.sinaToShort(longUrlText,site);
			shortURLTextField.setText(shortURL);
		}
	}

	@FXML
	private void shortURLCopyAction(ActionEvent event) {
		StringSelection selection = new StringSelection(shortURLTextField.getText());// 获取系统剪切板，复制短网址
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	@FXML
	private void revertAction(ActionEvent event) {
		String shortURLServiceType = shortURLServiceChoiceBox.getValue();
		String shortUrlText = shortURLTextField.getText().trim();
		if(StringUtils.isEmpty(shortUrlText)) {
			return;
		}
		longURLTextField.setText(null);
		if(shortURLService[0].equals(shortURLServiceType)) {
			String shortURL = ShortURLService.baiduToLongURL(shortUrlText);
			longURLTextField.setText(shortURL);
		}
	}

}