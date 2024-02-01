package com.xwintop.xJavaFxTool.utils;

import de.schlichtherle.license.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

@Slf4j
@Data
public class CreateLicense {
    private static volatile LicenseManager licenseManager = null;
    /**
     * X500Princal 是一个证书文件的固有格式，详见API
     */
    private final static X500Principal DEFAULT_HOLDERAND_ISSUER = new X500Principal("CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US");

    private String priAlias;
    private String privateKeyPwd;
    private String keyStorePwd;
    private String subject;
    private String priPath;

    private String issued;
    private String notBefore;
    private String notAfter;
    private String consumerType;
    private int consumerAmount;
    private String info;
    private Map<String, String> extra = new HashMap<>(4);

    private String licPath;

    /**
     * 生成证书，在证书发布者端执行
     *
     * @throws Exception
     */
    public void create() throws Exception {
        LicenseManager licenseManager = new LicenseManager(initLicenseParams());
        licenseManager.store(buildLicenseContent(), new File(licPath));
        log.info("------ 证书发布成功 ------");
    }

    /**
     * 初始化证书的相关参数
     *
     * @return
     */
    private LicenseParam initLicenseParams() {
        Class<CreateLicense> clazz = CreateLicense.class;
        Preferences preferences = Preferences.userNodeForPackage(clazz);
        // 设置对证书内容加密的对称密码
        CipherParam cipherParam = new DefaultCipherParam(keyStorePwd);
        // 参数 1,2 从哪个Class.getResource()获得密钥库;
        // 参数 3 密钥库的别名;
        // 参数 4 密钥库存储密码;
        // 参数 5 密钥库密码
        KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(clazz, priPath, priAlias, keyStorePwd, privateKeyPwd){
            @Override
            public InputStream getStream() throws IOException {
                try {
                    return super.getStream();
                } catch (Exception e) {
                    InputStream var1 = new FileInputStream(priPath);
                    if (null == var1) {
                        throw new FileNotFoundException(priPath);
                    } else {
                        return var1;
                    }
                }
            }
        };
        // 返回生成证书时需要的参数
        return new DefaultLicenseParam(subject, preferences, privateStoreParam, cipherParam);
    }

    /**
     * 通过外部配置文件构建证书的的相关信息
     *
     * @return
     * @throws ParseException
     */
    public LicenseContent buildLicenseContent() throws ParseException {
        LicenseContent content = new LicenseContent();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        content.setConsumerAmount(consumerAmount);
        content.setConsumerType(consumerType);
        content.setHolder(DEFAULT_HOLDERAND_ISSUER);
        content.setIssuer(DEFAULT_HOLDERAND_ISSUER);
        content.setIssued(formate.parse(issued));
        content.setNotBefore(formate.parse(notBefore));
        content.setNotAfter(formate.parse(notAfter));
        content.setInfo(info);
        content.setExtra(extra);// 扩展字段
        return content;
    }
}
