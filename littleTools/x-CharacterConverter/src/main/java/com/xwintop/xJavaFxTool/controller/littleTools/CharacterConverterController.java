package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.CharacterConverterService;
import com.xwintop.xJavaFxTool.utils.GuiUtils;
import com.xwintop.xJavaFxTool.utils.RadixUtils;
import com.xwintop.xJavaFxTool.view.littleTools.CharacterConverterView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: CharacterConverterController
 * @Description: 编码转换工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:07
 */

public class CharacterConverterController extends CharacterConverterView {
    private CharacterConverterService characterConverterService = new CharacterConverterService(this);
    /**
     * 编解码类别.
     */
    private final String[] codeTypes = new String[]{"二进制", "八进制", "十进制", "十六进制", "乱码解码"};
    /**
     * 进制前缀符.
     */
    private final String[] prefixs = new String[]{"空格", "空", "-", "%", "\\u"};
    private final String[] prefixsStr = new String[]{" ", "", "-", "%", "\\u"};
    /**
     * 进制编码大小写.
     */
    private final String[] lowUpCase = new String[]{"小写", "大写"};
    /**
     * 字符集.
     */
    private String[] charsets = new String[]{GuiUtils.CHARSET_UTF_16BE, GuiUtils.CHARSET_UTF_16LE,
            GuiUtils.CHARSET_UTF_8, GuiUtils.CHARSET_UTF_16, GuiUtils.CHARSET_GB2312, GuiUtils.CHARSET_GBK,
            GuiUtils.CHARSET_GB18030, GuiUtils.CHARSET_Big5, GuiUtils.CHARSET_ISO_8859_1, StringUtils.EMPTY};

    private TextField customCharsetField;
    private TextField[] fields = new TextField[charsets.length];
    private Button[] buttons = new Button[charsets.length];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        encodeButton.setAccessibleText("0");
        codeTypesBox.getItems().addAll(codeTypes);
        codeTypesBox.setValue(codeTypesBox.getItems().get(0));
        prefixsBox.getItems().addAll(prefixs);
        prefixsBox.setValue(prefixsBox.getItems().get(0));
        lowUpCaseBox.getItems().addAll(lowUpCase);
        lowUpCaseBox.setValue(lowUpCaseBox.getItems().get(0));
        for (int i = 0; i < fields.length; i++) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(10);
            fields[i] = new TextField();
//			fields[i].setLayoutX(112);
//			fields[i].setLayoutY(75 + 30 * i);
//			fields[i].setPrefWidth(390);
//			mainAnchorPane.getChildren().add(fields[i]);
            if ("".equals(charsets[i])) {
                customCharsetField = new TextField("UTF-8");
//				customCharsetField.setLayoutX(14);
//				customCharsetField.setLayoutY(75 + 30 * i);
                customCharsetField.setPrefWidth(90);
//				mainAnchorPane.getChildren().add(customCharsetField);
                hBox.getChildren().add(customCharsetField);
            } else {
                Label label = new Label(charsets[i]);
//				label.setLayoutX(14);
//				label.setLayoutY(75 + 30 * i);
//				mainAnchorPane.getChildren().add(label);
                label.setPrefWidth(90);
                hBox.getChildren().add(label);
            }
            buttons[i] = new Button("Decode");
//			buttons[i].setLayoutX(577);
//			buttons[i].setLayoutY(75 + 30 * i);
            buttons[i].setAccessibleText(Integer.toString(i + 1));
            buttons[i].setOnAction(getButtonActionListener(i + 1));
//			mainAnchorPane.getChildren().add(buttons[i]);
            hBox.getChildren().add(fields[i]);
            hBox.getChildren().add(buttons[i]);
            HBox.setHgrow(fields[i], Priority.ALWAYS);
            mainVBox.getChildren().add(hBox);
        }
    }

    private void initEvent() {
        encodeButton.setOnAction(getButtonActionListener(0));
        for (int i = 0; i < fields.length; i++) {
            buttons[i].setOnAction(getButtonActionListener(i + 1));
        }
    }

    @FXML
    private void clearTextFields(ActionEvent event) {
        encodeTextField.setText("");
        for (int i = 0; i < fields.length; i++) {
            fields[i].setText("");
        }
    }

    /**
     * 按钮点击事件.
     */
    private EventHandler<ActionEvent> getButtonActionListener(int current) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String curCodeType = codeTypesBox.getValue();
                String curPrefix = prefixsStr[prefixsBox.getSelectionModel().getSelectedIndex()];
                String curLowUpCase = lowUpCaseBox.getValue();
                // 手动输入的字符编码
                charsets[charsets.length - 1] = customCharsetField.getText().trim();
                int sort = Integer.parseInt(((Button) event.getSource()).getAccessibleText());
                if (sort == 0) {
                    String input = encodeTextField.getText();
                    clearTextFields(null);
                    encodeTextField.setText(input);
                    if (input.length() == 0) {
                        return;
                    }
                    for (int i = 0; i < fields.length; i++) {
                        try {
                            if (codeTypes[3].equals(curCodeType)) {
                                fields[i].setText(characterConverterService.encode16RadixAddPrefix(input, charsets[i], curPrefix));
                            } else if (codeTypes[2].equals(curCodeType)) {
                                fields[i].setText(characterConverterService.encode10RadixAddPrefix(input, charsets[i], curPrefix));
                            } else if (codeTypes[1].equals(curCodeType)) {
                                fields[i].setText(characterConverterService.encode8RadixAddPrefix(input, charsets[i], curPrefix));
                            } else if (codeTypes[0].equals(curCodeType)) {
                                fields[i].setText(characterConverterService.encode2RadixAddPrefix(input, charsets[i], curPrefix));
                            }
                        } catch (Exception e) {
//							showExceptionMessage(e);
                            return;
                        }
                    }
                } else {
                    String input = fields[sort - 1].getText();
                    clearTextFields(null);
                    fields[sort - 1].setText(input);
                    input = input.replace(curPrefix, ""); // 去除前缀符
                    if (input.length() == 0) {
                        return;
                    }
                    try {
                        String decodeString = "";
                        if (codeTypes[3].equals(curCodeType)) {
                            decodeString = new String(Hex.decodeHex(input), charsets[sort - 1]);
                        } else if (codeTypes[2].equals(curCodeType)) {
                            decodeString = new String(Hex.decodeHex(RadixUtils.convertRadixString10To16(input)), charsets[sort - 1]);
                        } else if (codeTypes[1].equals(curCodeType)) {
                            decodeString = new String(Hex.decodeHex(RadixUtils.convertRadixString8To16(input)), charsets[sort - 1]);
                        } else if (codeTypes[0].equals(curCodeType)) {
                            decodeString = new String(Hex.decodeHex(RadixUtils.convertRadixString2To16(input)), charsets[sort - 1]);
                        }
                        encodeTextField.setText(decodeString);
                        if (codeTypes[4].equals(curCodeType)) {
                            encodeTextField.setText("\"" + codeTypes[4] + "\"与Encode String无关！");
                        }
                        for (int i = 0; i < fields.length; i++) {
                            if (i != sort - 1) {
                                if (codeTypes[3].equals(curCodeType)) {
                                    fields[i].setText(characterConverterService.encode16RadixAddPrefix(decodeString, charsets[i], curPrefix));
                                } else if (codeTypes[2].equals(curCodeType)) {
                                    fields[i].setText(characterConverterService.encode10RadixAddPrefix(decodeString, charsets[i], curPrefix));
                                } else if (codeTypes[1].equals(curCodeType)) {
                                    fields[i].setText(characterConverterService.encode8RadixAddPrefix(decodeString, charsets[i], curPrefix));
                                } else if (codeTypes[0].equals(curCodeType)) {
                                    fields[i].setText(characterConverterService.encode2RadixAddPrefix(decodeString, charsets[i], curPrefix));
                                } else if (codeTypes[4].equals(curCodeType)) {
                                    fields[i].setText(new String(input.getBytes(charsets[sort - 1]), charsets[i]));
                                }
                            }
                        }
                    } catch (Exception e) {
//						showExceptionMessage(e);
                        return;
                    }

                }
                // 大小写
                if (codeTypes[3].equals(curCodeType) || codeTypes[0].equals(curCodeType)) {
                    if (lowUpCase[0].equals(curLowUpCase)) {
                        for (int fi = 0; fi < fields.length; fi++) {
                            fields[fi].setText(fields[fi].getText().toLowerCase());
                        }
                    } else if (lowUpCase[1].equals(curLowUpCase)) {
                        for (int fi = 0; fi < fields.length; fi++) {
                            fields[fi].setText(fields[fi].getText().toUpperCase());
                        }
                    }
                }
            }
        };
    }

}
