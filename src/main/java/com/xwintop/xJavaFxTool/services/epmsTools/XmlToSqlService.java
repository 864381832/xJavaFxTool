package com.xwintop.xJavaFxTool.services.epmsTools;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.xwintop.xJavaFxTool.utils.AttributeConvertUtil;

public class XmlToSqlService {
	public static String xmlToSql(String string,String tableSpace,String tableName) {
//		String TableSpace = "EDI2";//表空间名
//		String tableName = "COSTRP_VSL";//表名
		tableSpace = tableSpace.toUpperCase().trim();
		tableName = tableName.toUpperCase().trim();
		StringBuilder sBuilder = new StringBuilder();//创建表Sql
		StringBuilder sBuilder1 = new StringBuilder();//创建主键Sql
		StringBuilder sBuilder2 = new StringBuilder();//创建备注Sql
		StringBuilder sBuilder3 = new StringBuilder();//创建序列Sql
		String[] strings = string.split("\n");
		sBuilder.append("CREATE TABLE ").append(tableSpace).append(".").append(tableName).append("(\n");
		for(String str : strings){
			String[] rawDataStrings = str.split("\t");
			String attrUpperCaseStr = rawDataStrings[0].toUpperCase();
			sBuilder.append(" ").append(attrUpperCaseStr).append(" ");
			if("INTEGER".equals(rawDataStrings[1])){
				sBuilder.append(rawDataStrings[1]).append(" NOT NULL");
			}else if ("DATE".equals(rawDataStrings[1])) {
				sBuilder.append(rawDataStrings[1]).append(" DEFAULT sysdate NOT NULL,");
			}else{
				sBuilder.append(rawDataStrings[1]).append("(");
				sBuilder.append(rawDataStrings[2]).append("),");
			}
			sBuilder.append("\n");
			
			//拼接注释
			sBuilder2.append("COMMENT ON COLUMN ");
			sBuilder2.append(tableSpace).append(".").append(tableName).append(".");
			sBuilder2.append(attrUpperCaseStr).append(" IS '");
			sBuilder2.append(rawDataStrings[4].trim()).append("';\n");
			
			//拼接主键
			if(sBuilder1.length() == 0){
				sBuilder1.append("ALTER TABLE ");
				sBuilder1.append(tableSpace).append(".").append(tableName);
				sBuilder1.append(" ADD (\n\tCONSTRAINT ").append(tableName);
				sBuilder1.append("_PK\n\tPRIMARY KEY\n\t(").append(attrUpperCaseStr);
				sBuilder1.append(")\n\tUSING INDEX\n\tTABLESPACE ").append(tableSpace).append(");\n\n");
			}
		}
		sBuilder.deleteCharAt(sBuilder.length()-2);
		sBuilder.append(")TABLESPACE ").append(tableSpace).append(";\n\n");
		
		sBuilder3.append("CREATE SEQUENCE EDI2.SEQ_"+tableName+"_ID\n");
		sBuilder3.append("  START WITH 1\n  MAXVALUE 999999999999999999\n");
		sBuilder3.append("  MINVALUE 1\n  NOCYCLE\n  NOCACHE\n  ORDER;\n");
		
		return sBuilder.toString()+sBuilder1.toString()+sBuilder2.toString()+sBuilder3.toString();
	}
	
	public static String xmlElementToSqlAction(String xmlStr,String tableSpace,String tableName) throws Exception{
		tableSpace = tableSpace.toUpperCase().trim();
		tableName = tableName.toUpperCase().trim();
		StringBuilder sBuilder = new StringBuilder();//创建表Sql
		sBuilder.append("CREATE TABLE ").append(tableSpace).append(".").append(tableName).append("(\n");
		xmlStr = xmlStr.replace("xs:", "").replace("msdata:", "");
		Document document = DocumentHelper.parseText(xmlStr);
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> list = root.elements();
		for (Element attribute : list) {
			String name = attribute.attributeValue("name");
			String type = attribute.attributeValue("type");
			String maxLength = null;
			if(type == null){
				Element restriction = attribute.element("simpleType").element("restriction");
				type = restriction.attributeValue("base");
				maxLength = restriction.element("maxLength").attributeValue("value");
			}
			sBuilder.append(AttributeConvertUtil.humpToLine(name));
			if("int".equals(type)){
				sBuilder.append(" NUMBER,\n");
			}else if("string".equals(type)){
				sBuilder.append(" VARCHAR2");
				if(maxLength != null){
					sBuilder.append("(").append(maxLength).append(" BYTE),\n");
				}else{
					sBuilder.append("(255 BYTE),\n");
				}
			}else if("short".equals(type)){
				sBuilder.append(" NUMBER(20,5),\n");
			}else if("float".equals(type)){
				sBuilder.append(" NUMBER(20,5),\n");
			}else if("boolean".equals(type)){
				sBuilder.append(" VARCHAR2(5 BYTE),\n");
			}else if("dateTime".equals(type)){
				sBuilder.append(" DATE,\n");
			}else if("decimal".equals(type)){
				sBuilder.append(" DECIMAL(20,10),\n");
			}
		}
		sBuilder.deleteCharAt(sBuilder.length()-2);
		sBuilder.append(")TABLESPACE ").append(tableSpace).append(";\n\n");
		return sBuilder.toString();
	}
}
