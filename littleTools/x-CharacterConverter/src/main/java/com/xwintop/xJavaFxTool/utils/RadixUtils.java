package com.xwintop.xJavaFxTool.utils;

/**
 * 进制转换类.
 * 
 * <pre>
 * 特别说明：此处的字符皆为1个字节8位为1个转换单位，因此：
 *           1，1个字节转16进制为2位16进制，不足前面补零；
 *           2，1个字节转10进制为3位10进制，不足前面补零；
 *           3，1个字节转8进制为3位8进制，不足前面补零；
 *           这个类中的所有进制字符转换方法都是依据以上条件而定，当不满足以上条件时，程序会发生异常或不能得到预期结果！
 * </pre>
 */
public class RadixUtils {

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
	 * 将16进制字符转为10进制字符.
	 */
	public static String convertRadixString16To10(String radix16) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < radix16.length() / 2; i++) {
			sb.append(fillStringBefore(Integer.toString(Integer.valueOf(radix16.substring(i * 2, (i + 1) * 2), 16)),
					"0", 3));
		}
		return sb.toString();
	}

	/**
	 * 将10进制字符转为16进制字符.
	 */
	public static String convertRadixString10To16(String radix10) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < radix10.length() / 3; i++) {
			sb.append(Integer.toHexString(Integer.valueOf(radix10.substring(i * 3, (i + 1) * 3))));
		}
		return sb.toString();
	}

	/**
	 * 将16进制字符转为8进制字符.
	 */
	public static String convertRadixString16To8(String radix16) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < radix16.length() / 2; i++) {
			sb.append(fillStringBefore(
					Integer.toOctalString(Integer.valueOf(radix16.substring(i * 2, (i + 1) * 2), 16)), "0", 3));
		}
		return sb.toString();
	}

	/**
	 * 将8进制字符转为16进制字符.
	 */
	public static String convertRadixString8To16(String radix8) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < radix8.length() / 3; i++) {
			sb.append(Integer.toHexString(Integer.valueOf(radix8.substring(i * 3, (i + 1) * 3), 8)));
		}
		return sb.toString();
	}

	/**
	 * 将16进制字符转为2进制字符.
	 */
	public static String convertRadixString16To2(String radix16) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < radix16.length(); i++) {
			sb.append(fillStringBefore(Integer.toBinaryString(Integer.valueOf(radix16.substring(i, i + 1), 16)), "0", 4));
		}
		return sb.toString();
	}

	/**
	 * 将2进制字符转为16进制字符.
	 */
	public static String convertRadixString2To16(String radix2) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < radix2.length() / 4; i++) {
			sb.append(Integer.toHexString(Integer.valueOf(radix2.substring(i * 4, (i + 1) * 4), 2)));
		}
		return sb.toString();
	}

}
