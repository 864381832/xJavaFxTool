package com.xwintop.xJavaFxTool.utils;

public class GuiUtils {
    /**************************** 算法 ****************************/
    // 可解密/解码算法
    public static String CRYPTO_ASCII = "Ascii";

    public static String CRYPTO_HEX = "Hex";

    public static String CRYPTO_BASE32 = "Base32";

    public static String CRYPTO_BASE64 = "Base64";

    public static String CRYPTO_URL = "URL";

    // 不可解密算法
    public static String CRYPTO_SHA = "SHA";

    public static String CRYPTO_SHA256 = "SHA256";

    public static String CRYPTO_SHA384 = "SHA384";

    public static String CRYPTO_SHA512 = "SHA512";

    /**************************** 文件大小单位 ****************************/
    public static String FileSize_PB = "PB";

    public static String FileSize_TB = "TB";

    public static String FileSize_G = "G";

    public static String FileSize_M = "M";

    public static String FileSize_KB = "KB";

    public static String FileSize_Byte = "Byte";

    /**
     * 计算文件(单位)大小，单位为字节Byte.
     */
    public static Double getCountFileSizeUnit(String size, String unit) {
        return getCountFileSizeUnit(size.length() == 0 ? null : Double.parseDouble(size), unit);
    }

    /**
     * 计算文件(单位)大小，单位为字节Byte.
     */
    public static Double getCountFileSizeUnit(Double size, String unit) {
        if (size == null) {
            return null;
        }
        Double bSize = null;
        int cas = 1024;
        if (unit.equals(FileSize_Byte)) {
            bSize = size;
        } else if (unit.equals(FileSize_KB)) {
            bSize = size * cas;
        } else if (unit.equals(FileSize_M)) {
            bSize = size * cas * cas;
        } else if (unit.equals(FileSize_G)) {
            bSize = size * cas * cas * cas;
        } else if (unit.equals(FileSize_TB)) {
            bSize = size * cas * cas * cas * cas;
        } else if (unit.equals(FileSize_PB)) {
            bSize = size * cas * cas * cas * cas * cas;
        }
        return bSize;
    }
}
