package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.common.ExCodec;
import com.xwintop.xJavaFxTool.utils.RadixUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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
	private Button[] buttons = new Button[CodeType.charsets.length];

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		encodeButton.setAccessibleText("0");
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
				customCharsetField = new TextField("UTF-8");
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
			buttons[i] = new Button("Decode");
			buttons[i].setLayoutX(577);
			buttons[i].setLayoutY(75 + 30 * i);
			buttons[i].setAccessibleText(Integer.toString(i+1));
			buttons[i].setOnAction(getButtonActionListener(i+1));
			mainAnchorPane.getChildren().add(buttons[i]);
		}
	}

	private void initEvent() {
		encodeButton.setOnAction(getButtonActionListener(0));
		for (int i = 0; i < fields.length; i++) {
			buttons[i].setOnAction(getButtonActionListener(i+1));
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
				String curPrefix = CodeType.getCorrectSeparator(prefixsBox.getValue());
				String curLowUpCase = lowUpCaseBox.getValue();
				// 手动输入的字符编码
				CodeType.charsets[CodeType.charsets.length - 1] = customCharsetField.getText().trim();
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
							if (CodeType.codeType_16Radix.equals(curCodeType)) {
								fields[i].setText(encode16RadixAddPrefix(input, CodeType.charsets[i], curPrefix));
							} else if (CodeType.codeType_10Radix.equals(curCodeType)) {
								fields[i].setText(encode10RadixAddPrefix(input, CodeType.charsets[i], curPrefix));
							} else if (CodeType.codeType_8Radix.equals(curCodeType)) {
								fields[i].setText(encode8RadixAddPrefix(input, CodeType.charsets[i], curPrefix));
							} else if (CodeType.codeType_2Radix.equals(curCodeType)) {
								fields[i].setText(encode2RadixAddPrefix(input, CodeType.charsets[i], curPrefix));
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
						if (CodeType.codeType_16Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(input, CodeType.charsets[sort - 1]);
						} else if (CodeType.codeType_10Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(RadixUtils.convertRadixString10To16(input),
									CodeType.charsets[sort - 1]);
						} else if (CodeType.codeType_8Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(RadixUtils.convertRadixString8To16(input),
									CodeType.charsets[sort - 1]);
						} else if (CodeType.codeType_2Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(RadixUtils.convertRadixString2To16(input),
									CodeType.charsets[sort - 1]);
						}
						encodeTextField.setText(decodeString);
						if (CodeType.codeType_Decode.equals(curCodeType)) {
							encodeTextField.setText("\"" + CodeType.codeType_Decode + "\"与Encode String无关！");
						}
						for (int i = 0; i < fields.length; i++) {
							if (i != sort - 1) {
								if (CodeType.codeType_16Radix.equals(curCodeType)) {
									fields[i].setText(encode16RadixAddPrefix(decodeString, CodeType.charsets[i], curPrefix));
								} else if (CodeType.codeType_10Radix.equals(curCodeType)) {
									fields[i].setText(encode10RadixAddPrefix(decodeString, CodeType.charsets[i], curPrefix));
								} else if (CodeType.codeType_8Radix.equals(curCodeType)) {
									fields[i].setText(encode8RadixAddPrefix(decodeString, CodeType.charsets[i], curPrefix));
								} else if (CodeType.codeType_2Radix.equals(curCodeType)) {
									fields[i].setText(encode2RadixAddPrefix(decodeString, CodeType.charsets[i], curPrefix));
								} else if (CodeType.codeType_Decode.equals(curCodeType)) {
									fields[i].setText(GuiUtils.encode(input, CodeType.charsets[sort - 1], CodeType.charsets[i]));
								}
							}
						}
					} catch (Exception e) {
//						showExceptionMessage(e);
						return;
					}

				}
				// 大小写
				if (CodeType.codeType_16Radix.equals(curCodeType) || CodeType.codeType_2Radix.equals(curCodeType)) {
					if (CodeType.lowUpCase[0].equals(curLowUpCase)) {
						for (int fi = 0; fi < fields.length; fi++) {
							fields[fi].setText(fields[fi].getText().toLowerCase());
						}
					} else if (CodeType.lowUpCase[1].equals(curLowUpCase)) {
						for (int fi = 0; fi < fields.length; fi++) {
							fields[fi].setText(fields[fi].getText().toUpperCase());
						}
					}
				}
			}
		};
	}

	/**
	 * 字符编码进制前缀字符填充 - 16进制.
	 */
	private String encode16RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			// ExCodec.encodeHex 转16进制
			sb.append(prefix).append(ExCodec.encodeHex(input.substring(i, i + 1), charset));
		}
		return sb.toString();
	}

	/**
	 * 字符编码进制前缀字符填充 - 10进制.
	 */
	private String encode10RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(prefix).append(
					RadixUtils.convertRadixString16To10(ExCodec.encodeHex(input.substring(i, i + 1), charset)));
		}
		return sb.toString();
	}

	/**
	 * 字符编码进制前缀字符填充 - 8进制.
	 */
	private String encode8RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(prefix).append(
					RadixUtils.convertRadixString16To8(ExCodec.encodeHex(input.substring(i, i + 1), charset)));
		}
		return sb.toString();
	}

	/**
	 * 字符编码进制前缀字符填充 - 2进制.
	 */
	private String encode2RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(prefix).append(
					RadixUtils.convertRadixString16To2(ExCodec.encodeHex(input.substring(i, i + 1), charset)));
		}
		return sb.toString();
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
		
		public static String getCorrectSeparator(String separ) {
			if (prefixs[0].equals(separ)) {
				return " ";
			} else if (prefixs[1].equals(separ)) {
				return "";
			} else {
				return separ;
			}
		}

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
		
		/**
		 * 按字符集解码.
		 */
		public static String encode(String string, String encode, String decode) throws UnsupportedEncodingException {
			return new String(string.getBytes(encode), decode);
		}
	}

}
