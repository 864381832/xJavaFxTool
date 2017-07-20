package com.xwintop.xJavaFxTool.utils;

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
		for(int i = 0;i<strings.length;i++){
			if(i == 0){
				stringBuilder.append(Character.toLowerCase(strings[i].charAt(0)));
			}else{
				stringBuilder.append(Character.toUpperCase(strings[i].charAt(0)));
			}
			stringBuilder.append(strings[i].substring(1));
		}
		return stringBuilder.toString();
	}
	
	public static String getAttributeSetNameByXml(String str) {
		StringBuilder stringBuilder = new StringBuilder();
		String[] strings = str.split("_");
		for(int i = 0;i<strings.length;i++){
			stringBuilder.append(Character.toUpperCase(strings[i].charAt(0)));
			stringBuilder.append(strings[i].substring(1));
		}
		return stringBuilder.toString();
	}
}
