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
}
