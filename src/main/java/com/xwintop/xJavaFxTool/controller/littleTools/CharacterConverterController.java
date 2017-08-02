package com.xwintop.xJavaFxTool.controller.littleTools;

import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class CharacterConverterController implements Initializable {
	@FXML
	private AnchorPane mainAnchorPane;
	@FXML
	private TextField encodeTextField;
	@FXML
	private Button encodeButton;
	@FXML
	private ChoiceBox<String> codeTypesBox;
	@FXML
	private ChoiceBox<String> prefixsBox;
	@FXML
	private ChoiceBox<String> lowUpCaseBox;
	@FXML
	private Button clearButton;

	private TextField customCharsetField;

	private TextField[] fields = new TextField[CodeType.charsets.length];

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		codeTypesBox.getItems().addAll(CodeType.codeTypes);
		codeTypesBox.setValue(codeTypesBox.getItems().get(0));
		prefixsBox.getItems().addAll(CodeType.prefixs);
		prefixsBox.setValue(prefixsBox.getItems().get(0));
		lowUpCaseBox.getItems().addAll(CodeType.lowUpCase);
		lowUpCaseBox.setValue(lowUpCaseBox.getItems().get(0));
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new TextField();
			fields[i].setLayoutX(112);
			fields[i].setLayoutY(75 + 30 * i);
			fields[i].setPrefWidth(390);
			mainAnchorPane.getChildren().add(fields[i]);
			if ("".equals(CodeType.charsets[i])) {
				customCharsetField = new TextField();
				customCharsetField.setLayoutX(14);
				customCharsetField.setLayoutY(75 + 30 * i);
				customCharsetField.setPrefWidth(90);
				mainAnchorPane.getChildren().add(customCharsetField);
			} else {
				Label label = new Label(CodeType.charsets[i]);
				label.setLayoutX(14);
				label.setLayoutY(75 + 30 * i);
				mainAnchorPane.getChildren().add(label);
			}
			Button button = new Button("Decode");
			button.setLayoutX(577);
			button.setLayoutY(75 + 30 * i);
			mainAnchorPane.getChildren().add(button);
		}
	}

	private void initEvent() {
	}

	@FXML
	private void clearTextFields(ActionEvent event) {
		encodeTextField.setText("");
		for (int i = 0; i < fields.length; i++) {
			fields[i].setText("");
		}
	}

	public static class CodeType {
		/**
		 * 2进制.
		 */
		public static final String codeType_2Radix = "二进制";
		/**
		 * 8进制.
		 */
		public static final String codeType_8Radix = "八进制";
		/**
		 * 10进制.
		 */
		public static final String codeType_10Radix = "十进制";
		/**
		 * 16进制.
		 */
		public static final String codeType_16Radix = "十六进制";

		/**
		 * 乱码解码.
		 */
		public static final String codeType_Decode = "乱码解码";

		/**
		 * 编解码类别.
		 */
		public static final String[] codeTypes = new String[] { codeType_2Radix, codeType_8Radix, codeType_10Radix,
				codeType_16Radix, codeType_Decode };

		/**
		 * 进制前缀符.
		 */
		public static final String[] prefixs = new String[] { "空格", "空", "-", "%", "\\u" };

		/**
		 * 进制编码大小写.
		 */
		public static final String[] lowUpCase = new String[] { "小写", "大写" };

		/**
		 * 字符集.
		 */
		public static String[] charsets = new String[] { GuiUtils.CHARSET_UTF_16BE, GuiUtils.CHARSET_UTF_16LE,
				GuiUtils.CHARSET_UTF_8, GuiUtils.CHARSET_UTF_16, GuiUtils.CHARSET_GB2312, GuiUtils.CHARSET_GBK,
				GuiUtils.CHARSET_GB18030, GuiUtils.CHARSET_Big5, GuiUtils.CHARSET_ISO_8859_1, "" };
	}

	public static class GuiUtils {
		/**************************** 编码 ****************************/
		// 下面两个转码结果一样
		public static String CHARSET_ISO_8859_1 = "ISO-8859-1";
		public static String CHARSET_US_ASCII = "US-ASCII";

		public static String CHARSET_UTF_16BE = "UTF-16BE"; // java转义\ u后面跟的编码, 即Java Unicode转义字符
		public static String CHARSET_UTF_16LE = "UTF-16LE";

		public static String CHARSET_UTF_8 = "UTF-8";

		// 下面两个转码结果一样
		public static String CHARSET_UTF_16 = "UTF-16";
		public static String CHARSET_Unicode = "Unicode";

		// GB2312 < GBK < GB18030
		public static String CHARSET_GB2312 = "GB2312";
		public static String CHARSET_GBK = "GBK";
		public static String CHARSET_GB18030 = "GB18030";

		public static String CHARSET_Big5 = "Big5";

		/**************************** 算法 ****************************/
		// 可解密/解码算法
		public static String CRYPTO_ASCII = "Ascii";
		public static String CRYPTO_HEX = "Hex";
		public static String CRYPTO_BASE32 = "Base32";
		public static String CRYPTO_BASE64 = "Base64";
		public static String CRYPTO_URL = "URL";
		// 不可解密算法
		public static String CRYPTO_MD5 = "MD5";
		public static String CRYPTO_SHA = "SHA";
		public static String CRYPTO_SHA256 = "SHA256";
		public static String CRYPTO_SHA384 = "SHA384";
		public static String CRYPTO_SHA512 = "SHA512";
	}

}
