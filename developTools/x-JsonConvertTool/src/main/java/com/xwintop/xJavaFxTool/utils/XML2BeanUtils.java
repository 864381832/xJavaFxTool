package com.xwintop.xJavaFxTool.utils;

import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.IntrospectionConfiguration;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.DefaultObjectStringConverter;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Xml文件与javaBean对象互转工具类
 *
 * @author xufeng
 * @date 2017-11-21下午1:38:56
 */
public class XML2BeanUtils {
	private static final String xmlHead = "<?xml version='1.0' ?>";
	/**
	 * 将javaBean对象转换成xml文件，对于没有设置的属性将不会生成xml标签
	 * @param obj 待转换的javabean对象
	 * @return String 转换后的xml 字符串
	 */
	public static String bean2XmlString(Object obj) throws IOException, SAXException, IntrospectionException {
		if (obj == null) {
            throw new IllegalArgumentException("给定的参数不能为null！");
        }
		StringWriter sw = new StringWriter();
		sw.write(xmlHead);// 写xml文件头
		BeanWriter writer = new BeanWriter(sw);
		IntrospectionConfiguration config = writer.getXMLIntrospector().getConfiguration();
		BindingConfiguration bc = writer.getBindingConfiguration();
		bc.setObjectStringConverter(new DateConverter());
		bc.setMapIDs(false);
		config.setAttributesForPrimitives(false);
		config.setAttributeNameMapper(new HyphenatedNameMapper());
		config.setElementNameMapper(new DecapitalizeNameMapper());
		writer.enablePrettyPrint();
		writer.write(obj.getClass().getSimpleName(), obj);
		writer.close();
		return sw.toString();
	}

	/**
	 * 将xml文件转换成相应的javabean对象,对于List，Map，Array转换时需要在需要保证Bean类中有单个添加方法
	 * 例如：List<{@link String}> userNameList --> addUserNameList(String username)
	 *      String[] items            --> addItems(String item)
	 *      Map<String,User> userMap               --> addUserMap(String key,User user)
	 * 注意：
	 * 目前还没有找到好的方法解决Map与Map嵌套，List与List等嵌套的问题，使用时应当避免以上几种情况。
	 * @param beanClass 待转换的javabean字节码
	 * @param xmlFile xml文件字符串
	 * @return 转换后的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlSring2Bean(Class<T> beanClass, String xmlFile)
			throws IntrospectionException, IOException, SAXException {
		if (beanClass == null || xmlFile == null || xmlFile.isEmpty()) {
            throw new IllegalArgumentException("给定的参数不能为null！");
        }
		StringReader xmlReader = new StringReader(xmlFile);
		BeanReader reader = new BeanReader();
		reader.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
		BindingConfiguration bc = reader.getBindingConfiguration();
		bc.setObjectStringConverter(new DateConverter());
		bc.setMapIDs(false);
		T obj = null;
		reader.registerBeanClass(beanClass.getSimpleName(), beanClass);
		obj = (T) reader.parse(xmlReader);
		xmlReader.close();
		return obj;
	}

	/**
	 * 日期转换，主要是解决日期为null或者空字符串解析报错问题
	 */
	private static class DateConverter extends DefaultObjectStringConverter {
		private static final long serialVersionUID = -197858851188189916L;

		@Override
		@SuppressWarnings("rawtypes")
		public String objectToString(Object object, Class type, String flavour, Context context) {
			return super.objectToString(object, type, flavour, context);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Object stringToObject(String string, Class type, String flavour, Context context) {
			if (string == null || "".equals(string)) {
                return null;
            }
			return super.stringToObject(string, type, flavour, context);
		}

	}
}
