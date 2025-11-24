package com.xwintop.xcore.util;

import java.util.Locale;
import java.util.ResourceBundle;

/** 
 * @ClassName: LocaleUtil 
 * @Description: 国际化工具类
 * @author: xufeng
 * @date: 2017年7月10日 上午10:17:41  
 */
public class LocaleUtil {
	private static Locale locale = Locale.getDefault();//语言种类
	private static ResourceBundle bundle;//加载数据资源

	/** 
	 * @Title: getStringByKey 
	 * @Description: 根据主键获取文本
	 */
	public static String getStringByKey(String key){
		if(null == bundle){
			return null;
		}
		return bundle.getString(key);
	}
	
	/** 
	 * @Title: getStringByKeyAndFileName
	 * @Description: 根据主键和文件名获取文本
	 * @param key 主键
	 * @param fileName 文件名
	 */
	public static String getStringByKeyAndFileName(String key,String fileName){
		ResourceBundle bundle = ResourceBundle.getBundle(fileName, locale);
		return bundle.getString(key);
	}
	
	/** 
	 * @Title: getStringByKeyAndFileNameAndLocale 
	 * @Description: 根据主键、文件名和语言种类获取文本
	 * @param key 主键
	 * @param fileName 文件名
	 * @param locale 语言种类
	 */
	public static String getStringByKeyAndFileNameAndLocale(String key,String fileName,Locale locale){
		ResourceBundle bundle = ResourceBundle.getBundle(fileName, locale);
		return bundle.getString(key);
	}
	
	public static void initLocaleUtil(String fileName){
		bundle = ResourceBundle.getBundle(fileName, locale);
	}
	
	public static void initLocaleUtil(String fileName,Locale locale){
		LocaleUtil.locale = locale;
		bundle = ResourceBundle.getBundle(fileName, LocaleUtil.locale);
	}
	
	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		LocaleUtil.locale = locale;
	}
	
	public static ResourceBundle getBundle() {
		return bundle;
	}

	public static void setBundle(ResourceBundle bundle) {
		LocaleUtil.bundle = bundle;
	}

}
