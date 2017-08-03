package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.codec.DecoderException;

import com.xwintop.xJavaFxTool.common.ExCodec;
import com.xwintop.xJavaFxTool.utils.GuiUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class EncryptAndDecryptController implements Initializable {
	@FXML
	private AnchorPane mainAnchorPane;
	@FXML
	private TextArea encrptyTextArea;
	@FXML
	private TextArea decrptyTextArea;
	@FXML
	private ChoiceBox<String> charsetsBox;
	@FXML
	private Button encrptyButton;
	@FXML
	private Button decrptyButton;

	private ToggleGroup toggleGroup = new ToggleGroup();
	/**
	 * 字符集.
	 */
	private String[] charsets = new String[] { GuiUtils.CHARSET_UTF_8, GuiUtils.CHARSET_UTF_16BE,
			GuiUtils.CHARSET_UTF_16LE, GuiUtils.CHARSET_UTF_16, GuiUtils.CHARSET_GBK, GuiUtils.CHARSET_Big5,
			GuiUtils.CHARSET_ISO_8859_1 };

	/**
	 * 加密算法. 空""用于填充一个空位.
	 */
	private String[] cryptos = new String[] { GuiUtils.CRYPTO_ASCII, GuiUtils.CRYPTO_HEX, GuiUtils.CRYPTO_BASE64,
			GuiUtils.CRYPTO_BASE32, GuiUtils.CRYPTO_URL, "", "", "", GuiUtils.CRYPTO_MD5, "", GuiUtils.CRYPTO_SHA,
			GuiUtils.CRYPTO_SHA256, GuiUtils.CRYPTO_SHA384, GuiUtils.CRYPTO_SHA512 };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		charsetsBox.getItems().addAll(charsets);
		charsetsBox.setValue(charsetsBox.getItems().get(0));
		for (int i = 0; i < cryptos.length; i++) {
			if (!"".equals(cryptos[i])) {
				RadioButton radioButton = new RadioButton(cryptos[i]);
				radioButton.setLayoutX(440 + i % 2 * 100);
				radioButton.setLayoutY(60 + i / 2 * 26);
				radioButton.setToggleGroup(toggleGroup);
				radioButton.setUserData(cryptos[i]);
				mainAnchorPane.getChildren().add(radioButton);
				if (i == 0) {
					radioButton.setSelected(true);
				}
			}
		}
	}

	private void initEvent() {
	}

	@FXML
	private void encrptyAction(ActionEvent event) {// 加密
		String curCharset = charsetsBox.getValue();
		String curCrypto = toggleGroup.getSelectedToggle().getUserData().toString();
		String input = encrptyTextArea.getText();
		try {
			if (GuiUtils.CRYPTO_ASCII.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encodeAscii(input, curCharset));
			} else if (GuiUtils.CRYPTO_HEX.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encodeHex(input, curCharset));
			} else if (GuiUtils.CRYPTO_BASE64.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encodeBase64(input, curCharset));
			} else if (GuiUtils.CRYPTO_BASE32.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encodeBase32(input, curCharset));
			} else if (GuiUtils.CRYPTO_URL.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encodeURL(input, curCharset));
			} else if (GuiUtils.CRYPTO_MD5.equals(curCrypto)) {
				String md5Val = ExCodec.encryptMd5(input, curCharset);
				decrptyTextArea.setText("16Bit：" + md5Val.substring(8, 24) + "\n32Bit：" + md5Val);
			} else if (GuiUtils.CRYPTO_SHA.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encryptSha(input, curCharset));
			} else if (GuiUtils.CRYPTO_SHA256.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encryptSha256(input, curCharset));
			} else if (GuiUtils.CRYPTO_SHA384.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encryptSha384(input, curCharset));
			} else if (GuiUtils.CRYPTO_SHA512.equals(curCrypto)) {
				decrptyTextArea.setText(ExCodec.encryptSha512(input, curCharset));
			}
		} catch (UnsupportedEncodingException e) {
			// showExceptionMessage(e);
			decrptyTextArea.setText(e.getMessage());
		}
	}

	@FXML
	private void decrptyAction(ActionEvent event) {// 解密
		String curCharset = charsetsBox.getValue();
		String curCrypto = toggleGroup.getSelectedToggle().getUserData().toString();
		String input = decrptyTextArea.getText();
		try {
			if (GuiUtils.CRYPTO_ASCII.equals(curCrypto)) {
				encrptyTextArea.setText(ExCodec.decodeAscii(input, curCharset));
			} else if (GuiUtils.CRYPTO_HEX.equals(curCrypto)) {
				encrptyTextArea.setText(ExCodec.decodeHex(input, curCharset));
			} else if (GuiUtils.CRYPTO_BASE64.equals(curCrypto)) {
				encrptyTextArea.setText(ExCodec.decodeBase64(input, curCharset));
			} else if (GuiUtils.CRYPTO_BASE32.equals(curCrypto)) {
				encrptyTextArea.setText(ExCodec.decodeBase32(input, curCharset));
			} else if (GuiUtils.CRYPTO_URL.equals(curCrypto)) {
				encrptyTextArea.setText(ExCodec.decodeURL(input, curCharset));
			} else {
				encrptyTextArea.setText("不支持此种加密算法的解密！");
			}
		} catch (UnsupportedEncodingException e) {
			// showExceptionMessage(e);
			encrptyTextArea.setText(e.getMessage());
		} catch (DecoderException e) {
			// showExceptionMessage(e);
			encrptyTextArea.setText(e.getMessage());
		}
	}
}
