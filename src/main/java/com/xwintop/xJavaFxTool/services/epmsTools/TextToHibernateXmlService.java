package com.xwintop.xJavaFxTool.services.epmsTools;

import com.xwintop.xJavaFxTool.utils.AttributeConvertUtil;

public class TextToHibernateXmlService {
	/**
	 * @Title: textToHibernateXml
	 * @Description: 文本转hibernateXml文件
	 * @param string
	 * @param tableSpace
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @return: String
	 */
	public static String textToHibernateXml(String string, String tableSpace, String tableName) throws Exception {
		StringBuilder sBuilder = new StringBuilder();// 创建属性值获取

		String[] tableStrings = string.split("\n");
		sBuilder.append("<?xml version=\"1.0\"?>\n");
		sBuilder.append("<!DOCTYPE hibernate-mapping\n");
		sBuilder.append("PUBLIC \"-//Hibernate/Hibernate Mapping DTD//EN\"\n");
		sBuilder.append("\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n");
		sBuilder.append("<hibernate-mapping>\n");
		sBuilder.append("	<class name=\"com.easipass.mf.app.m2db.model.");
		sBuilder.append(AttributeConvertUtil.getAttributeSetNameByXml(tableName.toLowerCase()));
		sBuilder.append("\" table=\"" + tableName + "\">\n");
		// 遍历属性节点
		boolean isHaveID = false;
		for (String tableString : tableStrings) {
			String[] rawDataStrings = tableString.split("\t");
//			if("id".equals(rawDataStrings[0])){
			if (!isHaveID) {
				isHaveID = true;
				sBuilder.append("		<id name=\"id\" type=\"long\" column=\"ID\">\n");
				sBuilder.append("			<generator class=\"sequence\">\n");
				sBuilder.append("				<param name=\"sequence\">SEQ_" + tableName + "_ID</param>\n");
				sBuilder.append("			</generator>\n");
				sBuilder.append("		</id>\n");
			} else {
				sBuilder.append("		<property name=\"");
				sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(rawDataStrings[0]));
				sBuilder.append("\" column=\"").append(rawDataStrings[0].toUpperCase());
				sBuilder.append("\" type=\"");
				if ("INTEGER".equals(rawDataStrings[1])) {
					sBuilder.append("java.lang.Integer");
				} else if ("NUMBER".equals(rawDataStrings[1])) {
					if (rawDataStrings[2].contains(",")) {
						sBuilder.append("java.lang.Double");
					} else {
						sBuilder.append("java.lang.Long");
					}
				} else if ("DATE".equals(rawDataStrings[1])) {
					sBuilder.append("java.util.Date");
				} else {
					sBuilder.append("string");
				}
				if ("VARCHAR2".equals(rawDataStrings[1])) {
					sBuilder.append("\" length=\"").append(rawDataStrings[2]);
				}
				sBuilder.append("\"/>\n");
			}
		}
		sBuilder.append("	</class>\n");
		sBuilder.append("</hibernate-mapping>");

		return sBuilder.toString();
	}
	
	public static String sqlToHibernateXml(String string, String tableSpace, String tableName) throws Exception {
		StringBuilder sBuilder = new StringBuilder();// 创建属性值获取

		String[] tableStrings = string.split("\n");
		sBuilder.append("<?xml version=\"1.0\"?>\n");
		sBuilder.append("<!DOCTYPE hibernate-mapping\n");
		sBuilder.append("PUBLIC \"-//Hibernate/Hibernate Mapping DTD//EN\"\n");
		sBuilder.append("\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n");
		sBuilder.append("<hibernate-mapping>\n");
		sBuilder.append("	<class name=\"com.easipass.mf.app.m2db.model.");
		sBuilder.append(AttributeConvertUtil.getAttributeSetNameByXml(tableName.toLowerCase()));
		sBuilder.append("\" table=\"" + tableName + "\">\n");
		// 遍历属性节点
		boolean isHaveID = false;
		for (String tableString : tableStrings) {
			String[] rawDataStrings = tableString.split("\t");
//			if("id".equals(rawDataStrings[0])){
			if (!isHaveID) {
				isHaveID = true;
				sBuilder.append("		<id name=\"id\" type=\"long\" column=\"ID\">\n");
				sBuilder.append("			<generator class=\"sequence\">\n");
				sBuilder.append("				<param name=\"sequence\">SEQ_" + tableName + "_ID</param>\n");
				sBuilder.append("			</generator>\n");
				sBuilder.append("		</id>\n");
			} else {
				sBuilder.append("		<property name=\"");
				sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(rawDataStrings[0]));
				sBuilder.append("\" column=\"").append(rawDataStrings[0].toUpperCase());
				sBuilder.append("\" type=\"");
				if ("INTEGER".equals(rawDataStrings[1])) {
					sBuilder.append("java.lang.Integer");
				} else if ("NUMBER".equals(rawDataStrings[1])) {
					if (rawDataStrings[2].contains(",")) {
						sBuilder.append("java.lang.Double");
					} else {
						sBuilder.append("java.lang.Long");
					}
				} else if ("DATE".equals(rawDataStrings[1])) {
					sBuilder.append("java.util.Date");
				} else {
					sBuilder.append("string");
				}
				if ("VARCHAR2".equals(rawDataStrings[1])) {
					sBuilder.append("\" length=\"").append(rawDataStrings[2]);
				}
				sBuilder.append("\"/>\n");
			}
		}
		sBuilder.append("	</class>\n");
		sBuilder.append("</hibernate-mapping>");

		return sBuilder.toString();
	}
}
