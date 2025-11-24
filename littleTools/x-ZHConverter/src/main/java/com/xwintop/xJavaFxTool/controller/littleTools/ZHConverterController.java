package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.ZHConverterService;
import com.xwintop.xJavaFxTool.view.littleTools.ZHConverterView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ZHConverterController
 * @Description: 字符串转换
 * @author: xufeng
 * @date: 2017年8月20日 下午1:13:45
 */
public class ZHConverterController extends ZHConverterView {
    private ZHConverterService zhConverterService = new ZHConverterService();
    private String[] codeTypes = new String[]{"拼音", "简-繁", "简体-臺灣正體", "简体-香港繁體", "繁體-臺灣正體", "繁體-香港繁體", "香港繁體-臺灣正體", "数字金额-大写金额"};
    private String[] pinyinTypes = new String[]{"无音调", "数字音调", "符号音调", "声调", "声母", "韵母", "输入法头"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        codeTypesChoiceBox.getItems().addAll(codeTypes);
        codeTypesChoiceBox.setValue(codeTypes[0]);
        pinyinTypeChoiceBox.getItems().addAll(pinyinTypes);
        pinyinTypeChoiceBox.setValue(pinyinTypes[0]);
    }

    private void initEvent() {
//        JavaFxViewUtil.setPropertyAddChangeListener(simplifiedTextArea, () -> {
//            changeAction(null);
//        });
//        JavaFxViewUtil.setPropertyAddChangeListener(traditionalTextArea, () -> {
//            restoreAction(null);
//        });
    }

    private void initService() {
        zhConverterService.setCodeTypes(codeTypes);
        zhConverterService.setPinyinTypes(pinyinTypes);
    }

    @FXML
    private void changeAction(ActionEvent event) {
        String codeTypeString = codeTypesChoiceBox.getValue();
        String simplifiedString = simplifiedTextArea.getText();
        String pinyinTypeString = pinyinTypeChoiceBox.getValue();
        String pinyinTypeSpaceString = pinyinTypeTextField.getText();
        String traditionalString = zhConverterService.changeAction(simplifiedString, codeTypeString, pinyinTypeString, pinyinTypeSpaceString);
        traditionalTextArea.setText(traditionalString);
    }

    @FXML
    private void restoreAction(ActionEvent event) {
        String codeTypeString = codeTypesChoiceBox.getValue();
        String traditionalString = traditionalTextArea.getText();
        String simplifiedString = zhConverterService.restoreAction(traditionalString, codeTypeString);
        simplifiedTextArea.setText(simplifiedString);
    }
}