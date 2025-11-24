package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xcore.util.javafx.TooltipUtil;
import de.schlichtherle.license.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

@Slf4j
@Data
public class VerifyLicense {

    private String pubAlias;
    private String keyStorePwd;
    private String subject;
    private String licDir;
    private String pubPath;

    public VerifyLicense() {
    }

    /**
     * 安装证书证书
     */
    public void install() {
        try {
            LicenseManager licenseManager = new LicenseManager(initLicenseParams());
            licenseManager.install(new File(licDir));
            log.info("安装证书成功!");
            TooltipUtil.showToast("安装证书成功");
        } catch (Exception e) {
            log.error("安装证书失败!", e);
            TooltipUtil.showToast("安装证书失败!" + e.getMessage());
//            Runtime.getRuntime().halt(1);
        }

    }

    /**
     * 初始化证书的相关参数
     */
    private LicenseParam initLicenseParams() {
        Class<VerifyLicense> clazz = VerifyLicense.class;
        Preferences pre = Preferences.userNodeForPackage(clazz);
        CipherParam cipherParam = new DefaultCipherParam(keyStorePwd);
        KeyStoreParam pubStoreParam = new DefaultKeyStoreParam(clazz, pubPath, pubAlias, keyStorePwd, null) {
            @Override
            public InputStream getStream() throws IOException {
                try {
                    return super.getStream();
                } catch (Exception e) {
                    InputStream var1 = new FileInputStream(pubPath);
                    if (null == var1) {
                        throw new FileNotFoundException(pubPath);
                    } else {
                        return var1;
                    }
                }
            }
        };
        return new DefaultLicenseParam(subject, pre, pubStoreParam, cipherParam);
    }

    /**
     * 验证证书的合法性
     */
    public boolean vertify() {
        try {
            LicenseManager licenseManager = new LicenseManager(initLicenseParams());
            LicenseContent verify = licenseManager.verify();
            log.info("验证证书成功!");
            TooltipUtil.showToast("验证证书成功");
            Map<String, String> extra = (Map) verify.getExtra();
            String ip = extra.get("ip");
            InetAddress inetAddress = InetAddress.getLocalHost();
            if (StringUtils.isNotBlank(ip) && !"*".equals(ip)) {
                String localIp = inetAddress.getHostAddress();
                if (!Objects.equals(ip, localIp)) {
                    log.error("IP 地址验证不通过");
                    return false;
                }
            }
            String mac = extra.get("mac");
            if (StringUtils.isNotBlank(mac) && !"*".equals(mac)) {
                String localMac = getLocalMac(inetAddress);
                if (!Objects.equals(mac, localMac)) {
                    log.error("MAC 地址验证不通过");
                    return false;
                }
            }
            log.info("IP、MAC地址验证通过");
            return true;
        } catch (LicenseContentException ex) {
            log.error("证书已经过期!", ex);
            return false;
        } catch (Exception e) {
            log.error("验证证书失败!", e);
            return false;
        }
    }

    /**
     * 得到本机 mac 地址
     *
     * @param inetAddress
     * @throws SocketException
     */
    private String getLocalMac(InetAddress inetAddress) throws SocketException {
        //获取网卡，获取地址
        byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString().toUpperCase();
    }
}
