package com.xwintop.xcore.util;

/**
 * @ClassName: AttributeConvertUtil
 * @Description: 类属性转换工具类
 * @author: xufeng
 * @date: 2017年7月11日 下午1:20:05
 */
public class AttributeConvertUtil {
	public static String getAttributeNameByXml(String str) {
		StringBuilder stringBuilder = new StringBuilder();
		String[] strings = str.split("_");
		for (int i = 0; i < strings.length; i++) {
			if (i == 0) {
				stringBuilder.append(Character.toLowerCase(strings[i].charAt(0)));
			} else {
				stringBuilder.append(Character.toUpperCase(strings[i].charAt(0)));
			}
			stringBuilder.append(strings[i].substring(1));
		}
		return stringBuilder.toString();
	}

	public static String getAttributeSetNameByXml(String str) {
		StringBuilder stringBuilder = new StringBuilder();
		String[] strings = str.split("_");
		for (int i = 0; i < strings.length; i++) {
			stringBuilder.append(Character.toUpperCase(strings[i].charAt(0)));
			stringBuilder.append(strings[i].substring(1));
		}
		return stringBuilder.toString();
	}

	/** 
	 * @Title: humpToLine 
	 * @Description: 驼峰转下横线写法互转
	 */
	public static String humpToLine(String param) {
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		sb.append(param.charAt(0));
		for (int i = 1; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c) && Character.isLowerCase(param.charAt(i-1))) {
				sb.append("_");
			}
			sb.append(c);
		}
		return sb.toString().toUpperCase();
	}
}
