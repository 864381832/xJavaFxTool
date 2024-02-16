package com.xwintop.xJavaFxTool.controller.littleTools;

import cn.hutool.core.codec.Morse;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.xwintop.xJavaFxTool.utils.DruidConfigTools;
import com.xwintop.xJavaFxTool.utils.GuiUtils;
import com.xwintop.xJavaFxTool.view.littleTools.EncryptAndDecryptView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.ResourceBundle;

/**
 * @ClassName: EncryptAndDecryptController
 * @Description: 加密解密工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:08
 */

public class EncryptAndDecryptController extends EncryptAndDecryptView {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private ToggleGroup toggleGroup = new ToggleGroup();

    /**
     * 加密算法. 空""用于填充一个空位.
     */
    private String[] cryptos = new String[]{GuiUtils.CRYPTO_ASCII, GuiUtils.CRYPTO_HEX, GuiUtils.CRYPTO_BASE64,
        GuiUtils.CRYPTO_BASE32, GuiUtils.CRYPTO_URL, DigestAlgorithm.MD5.name(), DigestAlgorithm.SHA1.name(),
        DigestAlgorithm.SHA256.name(), DigestAlgorithm.SHA384.name(), DigestAlgorithm.SHA512.name(),
        SymmetricAlgorithm.AES.name(), SymmetricAlgorithm.DES.name(), "Sm2", "Sm3", "Sm4", "文件加密MD5", "文件加密SHA1", "摩斯密码", "Druid加密"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        charsetsBox.getItems().addAll(Charset.availableCharsets().keySet());
        charsetsBox.setValue("UTF-8");
        for (int i = 0; i < cryptos.length; i++) {
            if (!"".equals(cryptos[i])) {
                RadioButton radioButton = new RadioButton(cryptos[i]);
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setUserData(cryptos[i]);
                cryptosFlowPane.getChildren().add(radioButton);
                if (i == 0) {
                    radioButton.setSelected(true);
                }
            }
        }
    }

    private void initEvent() {
        JavaFxViewUtil.setPropertyAddChangeListener(encrptyTextArea, () -> {
            encrptyAction(null);
        });
        JavaFxViewUtil.setPropertyAddChangeListener(decrptyTextArea, () -> {
            decrptyAction(null);
        });
    }

    @FXML
    private void encrptyAction(ActionEvent event) {// 加密
        String charSet = charsetsBox.getValue();
        String curCrypto = toggleGroup.getSelectedToggle().getUserData().toString();
        String string = encrptyTextArea.getText();
        if (StringUtils.isEmpty(string)) {
            decrptyTextArea.setText(null);
            return;
        }
        try {
            if (GuiUtils.CRYPTO_ASCII.equals(curCrypto)) {
                decrptyTextArea.setText(BinaryCodec.toAsciiString(string.getBytes(charSet)));
            } else if (GuiUtils.CRYPTO_HEX.equals(curCrypto)) {
                decrptyTextArea.setText(Hex.encodeHexString(string.getBytes(charSet)));
            } else if (GuiUtils.CRYPTO_BASE64.equals(curCrypto)) {
                decrptyTextArea.setText(new String(Base64.encodeBase64(string.getBytes(charSet))));
            } else if (GuiUtils.CRYPTO_BASE32.equals(curCrypto)) {
                Base32 base32 = new Base32();
                decrptyTextArea.setText(new String(base32.encode(string.getBytes(charSet))));
            } else if (GuiUtils.CRYPTO_URL.equals(curCrypto)) {
                decrptyTextArea.setText(new String(URLCodec.encodeUrl(null, string.getBytes(charSet)), charSet));
            } else if (DigestAlgorithm.MD5.name().equals(curCrypto)) {
                String md5Val = DigestUtils.md5Hex(string.getBytes(charSet));
                decrptyTextArea.setText("16Bit：" + md5Val.substring(8, 24) + "\n32Bit：" + md5Val);
            } else if (DigestAlgorithm.SHA1.name().equals(curCrypto)) {
                decrptyTextArea.setText(DigestUtils.sha1Hex(string.getBytes(charSet)));
            } else if (DigestAlgorithm.SHA256.name().equals(curCrypto)) {
                decrptyTextArea.setText(DigestUtils.sha256Hex(string.getBytes(charSet)));
            } else if (DigestAlgorithm.SHA384.name().equals(curCrypto)) {
                decrptyTextArea.setText(DigestUtils.sha384Hex(string.getBytes(charSet)));
            } else if (DigestAlgorithm.SHA512.name().equals(curCrypto)) {
                decrptyTextArea.setText(DigestUtils.sha512Hex(string.getBytes(charSet)));
            } else if (SymmetricAlgorithm.AES.name().equals(curCrypto)) {
                String key = keyTextField.getText();
                AES aes = SecureUtil.aes(key.getBytes(charSet));
                decrptyTextArea.setText(aes.encryptHex(string, charSet));
            } else if (SymmetricAlgorithm.DES.name().equals(curCrypto)) {
                String key = keyTextField.getText();
                DES des = SecureUtil.des(key.getBytes());
                decrptyTextArea.setText(des.encryptHex(string, charSet));
            } else if ("Sm2".equals(curCrypto)) {
                String key = keyTextField.getText();
                SM2 sm2 = SmUtil.sm2(key, null);
                decrptyTextArea.setText(sm2.encryptHex(string, Charset.forName(charSet), KeyType.PrivateKey));
            } else if ("Sm3".equals(curCrypto)) {
                decrptyTextArea.setText(SmUtil.sm3(string));
            } else if ("Sm4".equals(curCrypto)) {
                String key = keyTextField.getText();
                SymmetricCrypto sm4 = SmUtil.sm4(org.bouncycastle.util.encoders.Hex.decode(key));
                decrptyTextArea.setText(sm4.encryptHex(string, charSet));
            } else if ("文件加密MD5".equals(curCrypto)) {
                String md5Val = DigestUtils.md5Hex(new FileInputStream(string));
                decrptyTextArea.setText("16Bit：" + md5Val.substring(8, 24) + "\n32Bit：" + md5Val);
            } else if ("文件加密SHA1".equals(curCrypto)) {
                decrptyTextArea.setText(DigestUtils.sha1Hex(new FileInputStream(new File(string))));
            } else if ("摩斯密码".equals(curCrypto)) {
                decrptyTextArea.setText(new Morse('●', '-', ' ').encode(string));
            } else if ("Druid加密".equals(curCrypto)) {
                String[] arr = DruidConfigTools.genKeyPair(512);
                StringBuilder decrptyStr = new StringBuilder();
                decrptyStr.append("privateKey:" + arr[0]);
                decrptyStr.append("\npublicKey:" + arr[1]);
                decrptyStr.append("\npassword:" + DruidConfigTools.encrypt(arr[0], string));
                decrptyTextArea.setText(decrptyStr.toString());
            }
        } catch (Exception e) {
            decrptyTextArea.setText(e.getMessage());
        }
    }

    @FXML
    private void decrptyAction(ActionEvent event) {// 解密
        String charSet = charsetsBox.getValue();
        String curCrypto = toggleGroup.getSelectedToggle().getUserData().toString();
        String string = decrptyTextArea.getText();
        if (StringUtils.isEmpty(string)) {
            decrptyTextArea.setText(null);
            return;
        }
        try {
            if (GuiUtils.CRYPTO_ASCII.equals(curCrypto)) {
                encrptyTextArea.setText(new String(BinaryCodec.fromAscii(string.toCharArray()), charSet));
            } else if (GuiUtils.CRYPTO_HEX.equals(curCrypto)) {
                encrptyTextArea.setText(new String(Hex.decodeHex(string.toCharArray()), charSet));
            } else if (GuiUtils.CRYPTO_BASE64.equals(curCrypto)) {
                encrptyTextArea.setText(new String(Base64.decodeBase64(string.getBytes(charSet)), charSet));
            } else if (GuiUtils.CRYPTO_BASE32.equals(curCrypto)) {
                Base32 base32 = new Base32();
                encrptyTextArea.setText(new String(base32.decode(string.getBytes(charSet)), charSet));
            } else if (GuiUtils.CRYPTO_URL.equals(curCrypto)) {
                encrptyTextArea.setText(new String(URLCodec.decodeUrl(string.getBytes(charSet)), charSet));
            } else if (SymmetricAlgorithm.AES.name().equals(curCrypto)) {
                String key = keyTextField.getText();
                AES aes = SecureUtil.aes(key.getBytes(charSet));
                encrptyTextArea.setText(aes.decryptStr(string, Charset.forName(charSet)));
            } else if (SymmetricAlgorithm.DES.name().equals(curCrypto)) {
                String key = keyTextField.getText();
                DES des = SecureUtil.des(key.getBytes());
                encrptyTextArea.setText(des.decryptStr(string, Charset.forName(charSet)));
            } else if ("Sm2".equals(curCrypto)) {
                String key = keyTextField.getText();
                SM2 sm2 = SmUtil.sm2(null, key);
                decrptyTextArea.setText(sm2.decryptStr(string, KeyType.PublicKey));
            } else if ("Sm4".equals(curCrypto)) {
                String key = keyTextField.getText();
                SymmetricCrypto sm4 = SmUtil.sm4(org.bouncycastle.util.encoders.Hex.decode(key));
                encrptyTextArea.setText(sm4.decryptStr(string, Charset.forName(charSet)));
            } else if ("摩斯密码".equals(curCrypto)) {
                encrptyTextArea.setText(new Morse('●', '-', ' ').decode(string));
            } else if ("Druid加密".equals(curCrypto)) {
                encrptyTextArea.setText(DruidConfigTools.decrypt(keyTextField.getText(), string));
            } else {
                encrptyTextArea.setText("不支持此种加密算法的解密！");
            }
        } catch (Exception e) {
            encrptyTextArea.setText(e.getMessage());
        }
    }
}
