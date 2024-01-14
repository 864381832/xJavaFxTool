package com.xwintop.xJavaFxTool.utils;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class GuiUtils {

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

	/**************************** 字体 ****************************/

	/**
	 * 所有字体.
	 */
	public static Map<String, Font> availableFontsMap = getAvailableFontsMap();

	/**
	 * 中文字体集.
	 */
	public static String[] fontStyles_cn;
	/**
	 * 中文字体.
	 */
	public static String fontStyle_cn;
	/**
	 * 英文字体集.
	 */
	public static String[] fontStyles;
	/**
	 * 英文字体.
	 */
	public static String fontStyle;
	/**
	 * 支持Unicode的字体集.
	 */
	public static String[] fontStyles_un;
	/**
	 * 支持Unicode的字体.
	 */
	public static String fontStyle_un;

	public static Font font12_cn;

	public static Font font13;
	public static Font font13_cn;

	public static Font font14_cn;
	public static Font font14_un;
	public static Font font14b;
	public static Font font14b_cn;

	public static Font font16;

	/**************************** 文件大小单位 ****************************/
	public static String FileSize_PB = "PB";
	public static String FileSize_TB = "TB";
	public static String FileSize_G = "G";
	public static String FileSize_M = "M";
	public static String FileSize_KB = "KB";
	public static String FileSize_Byte = "Byte";

	/**
	 * 初始化字体.
	 */
	public static void initFont() {
		font12_cn = new Font(fontStyle_cn, Font.PLAIN, 12);

		font13 = new Font(fontStyle, Font.PLAIN, 13);
		font13_cn = new Font(fontStyle_cn, Font.PLAIN, 13);

		font14_cn = new Font(fontStyle_cn, Font.PLAIN, 14);
		font14_un = new Font(fontStyle_un, Font.PLAIN, 14);
		font14b = new Font(fontStyle, Font.BOLD, 14);
		font14b_cn = new Font(fontStyle_cn, Font.BOLD, 14);

		font16 = new Font(fontStyle, Font.PLAIN, 16);
	}

	/**
	 * 当前Java虚拟机支持的charset.
	 */
	public static SortedMap<String, Charset> availableCharsets() {
		return Charset.availableCharsets();
	}

	/**
	 * 支持的字体.
	 */
	public static Font[] availableFonts() {
//		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		return environment.getAllFonts();
		return null;
	}

	/**
	 * 支持的字体.
	 */
	public static Map<String, Font> getAvailableFontsMap() {
		Font[] fonts = availableFonts();
		Map<String, Font> fontsMap = new HashMap<String, Font>();
//		for (Font font : fonts) {
//			fontsMap.put(font.getFontName(), font);
//		}
		return fontsMap;
	}

	/**
	 * 字符串前缀字符填充.
	 */
	public static String addFillString(String string, String fill, int interval) {
		StringBuilder sb = new StringBuilder();
		int len = string.length();
		int loop = len / interval;
		for (int i = 0; i < loop; i++) {
			sb.append(fill).append(string.substring(interval * i, interval * (i + 1)));
		}
		if (loop * interval != len) {
			sb.append(fill).append(string.substring(loop * interval, len));
		}
		return sb.toString();
	}

	/**
	 * 字符串前填充以满足固定长度.
	 */
	public static String fillStringBefore(String string, String fill, int size) {
		StringBuilder sb = new StringBuilder();
		int len = string.length();
		for (int i = 0; i < size - len; i++) {
			sb.append(fill);
		}
		return sb.append(string).toString();
	}

	/**
	 * 优先使用前面的字体，如果不存在，则一个一个向后查找..
	 */
	public static String getAvailableFont(String[] fontStyles) {
		String fontName = "";
		for (String fontStyle : fontStyles) {
			if (availableFontsMap.containsKey(fontStyle)) {
				fontName = fontStyle;
				break;
			}
		}
		return fontName;
	}

	/**
	 * 右填充字符串.
	 * 
	 * <pre>
	 * 依据UTF-8编码中文占三个字节, 英文占一个字节, 且宋体下中文占两个半角空格位, 英文占一个半角空格位的特点;
	 * 变通填充为字符中有一个中文字符即当两个半角空格位, 一个英文字符即当一个半角空格位.
	 * </pre>
	 */
	public static String getFillUpString(String string, int size) {
		StringBuilder sb = new StringBuilder(string);
		int len = 0;
		try {
			len = string.length();
			len = len + (string.getBytes("UTF-8").length - len) / 2;
		} catch (UnsupportedEncodingException e) {
		}
		for (int i = 0; i < size - len; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

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

	/**
	 * 获取类加载路径下的图标.
	 */
	public static Icon getIconFromClassloader(String classLoaderImagePath, Toolkit kit) {
		return new ImageIcon(getImageFromClassloader(classLoaderImagePath, kit));
	}

	/**
	 * 获取类加载路径下的图片.
	 */
	public static Image getImageFromClassloader(String classLoaderImagePath, Toolkit kit) {
		URL imgURL = ClassLoader.getSystemResource(classLoaderImagePath); // 这种方式可以从jar包中获取资源路径
		return kit.getImage(imgURL);
	}

}
