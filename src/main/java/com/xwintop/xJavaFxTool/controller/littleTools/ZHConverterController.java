package com.xwintop.xJavaFxTool.controller.littleTools;

import java.net.URL;
import java.util.ResourceBundle;

import com.hankcs.hanlp.HanLP;
import com.xwintop.xJavaFxTool.view.littleTools.ZHConverterView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ZHConverterController extends ZHConverterView {
	private String[] codeTypes = new String[] { "拼音", "简-繁", "简体-臺灣正體", "简体-香港繁體", "繁體-臺灣正體", "繁體-香港繁體", "香港繁體-臺灣正體", "" };
	private String[] pinyinTypes = new String[] { "正常", "", "", "", "", "", "", "" };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		codeTypesChoiceBox.getItems().addAll(codeTypes);
		codeTypesChoiceBox.setValue(codeTypes[0]);
		pinyinTypeChoiceBox.getItems().addAll(pinyinTypes);
		pinyinTypeChoiceBox.setValue(pinyinTypes[0]);
	}

	private void initEvent() {
	}

	@FXML
	private void changeAction(ActionEvent event) {
		String codeTypeString = codeTypesChoiceBox.getValue();
		String simplifiedString = simplifiedTextArea.getText();
		String traditionalString = null;
		if (codeTypes[0].equals(codeTypeString)) {
			traditionalString = HanLP.convertToPinyinString(simplifiedString, "", false);
		}else if(codeTypes[1].equals(codeTypeString)) {
			traditionalString = HanLP.s2t(simplifiedString);
		}else if(codeTypes[2].equals(codeTypeString)) {
			traditionalString = HanLP.s2tw(simplifiedString);
		}else if(codeTypes[3].equals(codeTypeString)) {
			traditionalString = HanLP.s2hk(simplifiedString);
		}else if(codeTypes[4].equals(codeTypeString)) {
			traditionalString = HanLP.t2tw(simplifiedString);
		}else if(codeTypes[5].equals(codeTypeString)) {
			traditionalString = HanLP.t2hk(simplifiedString);
		}else if(codeTypes[6].equals(codeTypeString)) {
			traditionalString = HanLP.hk2tw(simplifiedString);
		}
		traditionalTextArea.setText(traditionalString);
	}

	@FXML
	private void restoreAction(ActionEvent event) {
		String codeTypeString = codeTypesChoiceBox.getValue();
		String simplifiedString = null;
		String traditionalString = traditionalTextArea.getText();
		if (codeTypes[0].equals(codeTypeString)) {
//			simplifiedString = HanLP.convertToPinyinString(simplifiedString, "", false);
		}else if(codeTypes[1].equals(codeTypeString)) {
			simplifiedString = HanLP.t2s(traditionalString);
		}else if(codeTypes[2].equals(codeTypeString)) {
			simplifiedString = HanLP.tw2s(traditionalString);
		}else if(codeTypes[3].equals(codeTypeString)) {
			simplifiedString = HanLP.hk2s(traditionalString);
		}else if(codeTypes[4].equals(codeTypeString)) {
			simplifiedString = HanLP.tw2t(traditionalString);
		}else if(codeTypes[5].equals(codeTypeString)) {
			simplifiedString = HanLP.hk2t(traditionalString);
		}else if(codeTypes[6].equals(codeTypeString)) {
			simplifiedString = HanLP.tw2hk(traditionalString);
		}
		simplifiedTextArea.setText(simplifiedString);
	}
}