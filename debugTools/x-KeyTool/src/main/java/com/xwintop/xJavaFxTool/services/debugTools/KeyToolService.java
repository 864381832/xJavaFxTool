package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.KeyToolController;
import com.xwintop.xJavaFxTool.utils.CreateLicense;
import com.xwintop.xJavaFxTool.utils.VerifyLicense;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class KeyToolService {
    private KeyToolController keyToolController;

    public KeyToolService(KeyToolController keyToolController) {
        this.keyToolController = keyToolController;
    }

    //生成私钥库
    public void genkeypairOnAction() {
        StringBuffer argStringBuffer = new StringBuffer();
        argStringBuffer.append("-genkey ");
        argStringBuffer.append("-alias ").append(keyToolController.getAliasTextField().getText());
        argStringBuffer.append(" -keysize ").append(keyToolController.getKeysizeSpinner().getValue());
        argStringBuffer.append(" -keystore ").append(keyToolController.getKeystoreTextField().getText());
        argStringBuffer.append(" -validity ").append(keyToolController.getValiditySpinner().getValue());
        argStringBuffer.append(" -storepass ").append(keyToolController.getStorepassTextField().getText());
        argStringBuffer.append(" -keypass ").append(keyToolController.getKeypassTextField().getText());
        argStringBuffer.append(" -dname ").append(String.format("CN=%s,OU=%s,O=%s,L=%s,ST=%s,C=%s", keyToolController.getInfo6TextField().getText(), keyToolController.getInfo5TextField().getText(), keyToolController.getInfo4TextField().getText(), keyToolController.getInfo3TextField().getText(), keyToolController.getInfo2TextField().getText(), keyToolController.getInfo1TextField().getText()));
        try {
            sun.security.tools.keytool.Main.main(argStringBuffer.toString().split(" "));
            TooltipUtil.showToast("执行成功");
        } catch (Exception e) {
            log.error("执行异常", e);
            TooltipUtil.showToast("执行异常:" + e.getMessage());
        }
    }

    //把私匙库内的证书导出到一个文件当中
    public void exportcertOnAction() {
        StringBuffer argStringBuffer = new StringBuffer();
        argStringBuffer.append("-export ");
        argStringBuffer.append("-alias ").append(keyToolController.getAliasTextField().getText());
        argStringBuffer.append(" -file ").append(keyToolController.getPublicFileTextField().getText());
        argStringBuffer.append(" -keystore ").append(keyToolController.getKeystoreTextField().getText());
        argStringBuffer.append(" -storepass ").append(keyToolController.getStorepassTextField().getText());
        try {
            sun.security.tools.keytool.Main.main(argStringBuffer.toString().split(" "));
            TooltipUtil.showToast("执行成功");
        } catch (Exception e) {
            log.error("执行异常", e);
            TooltipUtil.showToast("执行异常:" + e.getMessage());
        }
    }

    //把证书文件导入到公匙库
    public void importOnAction() {
        StringBuffer argStringBuffer = new StringBuffer();
        argStringBuffer.append("-import ");
        argStringBuffer.append("-alias ").append(keyToolController.getPublicAliasTextField().getText());
        argStringBuffer.append(" -file ").append(keyToolController.getPublicFileTextField().getText());
        argStringBuffer.append(" -keystore ").append(keyToolController.getPublicCertsTextField().getText());
        argStringBuffer.append(" -storepass ").append(keyToolController.getStorepassTextField().getText());
        argStringBuffer.append(" -trustcacerts -noprompt");//移除确认信息
        try {
            sun.security.tools.keytool.Main.main(argStringBuffer.toString().split(" "));
            TooltipUtil.showToast("执行成功");
        } catch (Exception e) {
            log.error("执行异常", e);
            TooltipUtil.showToast("执行异常:" + e.getMessage());
        }
    }

    public void createLicenseOnAction() {
        CreateLicense createLicense = new CreateLicense();
        createLicense.setPriAlias(keyToolController.getAliasTextField().getText());
        createLicense.setPrivateKeyPwd(keyToolController.getKeypassTextField().getText());
        createLicense.setKeyStorePwd(keyToolController.getStorepassTextField().getText());
        createLicense.setSubject(keyToolController.getSubjectTextField().getText());
        createLicense.setPriPath(keyToolController.getKeystoreTextField().getText());
        createLicense.setIssued(keyToolController.getIssuedTimeDatePicker().getValue().toString());
        createLicense.setNotBefore(keyToolController.getNotBeforeDatePicker().getValue().toString());
        createLicense.setNotAfter(keyToolController.getNotAfterDatePicker().getValue().toString());
        createLicense.setConsumerType(keyToolController.getConsumerTypeChoiceBox().getValue());
        createLicense.setConsumerAmount(keyToolController.getConsumerAmountSpinner().getValue());
        createLicense.setInfo(keyToolController.getLicenseInfoTextField().getText());
        Map<String, String> extra = new HashMap<>(4);
        for (Map<String, String> propertiesTableDatum : keyToolController.getPropertiesTableData()) {
            extra.put(propertiesTableDatum.get("key"), propertiesTableDatum.get("value"));
        }
        createLicense.setExtra(extra);
        createLicense.setLicPath(keyToolController.getLicPathTextField().getText());
        try {
            createLicense.create();
            TooltipUtil.showToast("执行成功");
        } catch (Exception e) {
            log.error("生成trueLicense失败：", e);
            TooltipUtil.showToast("生成trueLicense失败:" + e.getMessage());
        }
    }

    public void verifyLicenseOnAction() {
        VerifyLicense verifyLicense = new VerifyLicense();
        verifyLicense.setSubject(keyToolController.getSubjectTextField().getText());
        verifyLicense.setPubAlias(keyToolController.getPublicAliasTextField().getText());
        verifyLicense.setKeyStorePwd(keyToolController.getStorepassTextField().getText());
        verifyLicense.setLicDir(keyToolController.getLicPathTextField().getText());
        verifyLicense.setPubPath(keyToolController.getPublicCertsTextField().getText());

        verifyLicense.install();
        TooltipUtil.showToast("验证license：" + verifyLicense.vertify());
    }

}