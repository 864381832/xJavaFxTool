package com.xwintop.xJavaFxTool.services.epmsTools;

public class XmlToBeanService {
	public static String xmlTobean(String string) {
		StringBuilder sBuilder = new StringBuilder();
		
		String[] strings = string.split("\n");
		for(String str : strings){
			String[] rawDataStrings = str.split("\t");
			if("NUMBER".equals(rawDataStrings[1])){
				sBuilder.append("private int ");
			}else if ("VARCHAR2".equals(rawDataStrings[1])) {
				sBuilder.append("private String ");
			}else if ("DATE".equals(rawDataStrings[1])) {
				sBuilder.append("private Date ");
			}else{
				sBuilder.append("private String ");
			}
			sBuilder.append(rawDataStrings[0]).append(";\n");
		}
		return sBuilder.toString();
	}
	
	public static String sqlToJdbcInsert1(String string,String tableName) {
		StringBuilder sBuilder = new StringBuilder("insert into "+tableName+"(");
		StringBuilder sBuilder2 = new StringBuilder();
		
		String[] strings = string.split("\n");
		for(String str : strings){
			String[] rawDataStrings = str.split(" ");
			sBuilder.append(rawDataStrings[0]).append(",");
			sBuilder2.append("?,");
		}
		sBuilder.deleteCharAt(sBuilder.length()-1);
		sBuilder2.deleteCharAt(sBuilder2.length()-1);
		sBuilder.append(") values("+sBuilder2.toString()+")");
		return sBuilder.toString();
	}
	
	public static String sqlToJdbcInsert(String string,String tableName) {
		tableName = "insert into ODI_SRC.SHIP_AGENT_HS_CODE(id,HACODE_LEVEL,HSCODE,HSCODE_NAME) values (";
		StringBuilder sBuilder = new StringBuilder();
		String[] strings = string.split("\n");
		int i = 1;
		for(String str : strings){
			sBuilder.append(tableName).append(i++).append(",'");
			String[] rawDataStrings = str.split("\t");
			sBuilder.append(rawDataStrings[0]).append("','").append(rawDataStrings[1]).append("','").append(rawDataStrings[2]);
			sBuilder.append("');\n");
		}
		return sBuilder.toString();
	}
	
	public static String sqlToJdbcUpdate(String string,String tableName) {
		StringBuilder sBuilder = new StringBuilder("update "+tableName+" set ");
		
		String[] strings = string.split("\n");
		for(String str : strings){
			String[] rawDataStrings = str.split(" ");
			sBuilder.append(rawDataStrings[0]).append("=?, ");
		}
		sBuilder.deleteCharAt(sBuilder.length()-1);
		sBuilder.deleteCharAt(sBuilder.length()-1);
		sBuilder.append(" where name = ?");
		return sBuilder.toString();
	}
}
