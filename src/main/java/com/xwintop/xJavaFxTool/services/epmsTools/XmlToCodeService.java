package com.xwintop.xJavaFxTool.services.epmsTools;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.xwintop.xJavaFxTool.utils.AttributeConvertUtil;

public class XmlToCodeService {
	/**
	 * @Title: xmlToCode
	 * @Description: 将xml结合表结构转换为代码
	 * @param xmlStr
	 *            xml结构
	 * @param string2
	 *            表结构
	 * @param tableSpace
	 *            结点名
	 * @param tableName
	 *            类名
	 * @return 代码字符串
	 * @return: String
	 */
	public static String xmlToCode(String xmlStr, String string2, String tableSpace, String tableName) throws Exception{
		int matchingNumber = 0;//匹配到总数
		StringBuilder sBuilder = new StringBuilder();// 创建属性值获取
		StringBuilder sBuilder1 = new StringBuilder();// 创建类属性设置
		StringBuilder sBuilder2 = new StringBuilder();// 记录匹配数

		String[] tableStrings = string2.split("\n");
		Document document = DocumentHelper.parseText(xmlStr);
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> list = root.elements();
		// 遍历属性节点
		for (String tableString : tableStrings) {
			boolean isMatching = false;
			for (Element attribute : list) {
				if (tableString.toUpperCase().contains(attribute.getName().toUpperCase())
						|| attribute.getName().toUpperCase().contains(tableString.toUpperCase())) {
					matchingNumber++;
					isMatching = true;
					//拼接属性值获取
					sBuilder.append("String ");
					sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(tableString));
					sBuilder.append(" = ").append(tableSpace);
					sBuilder.append(".getChildTextTrim(\"");
					sBuilder.append(attribute.getName());
					sBuilder.append("\");\n");
					
					//拼接类属性设置
					sBuilder1.append(tableName).append(".set");
					sBuilder1.append(AttributeConvertUtil.getAttributeSetNameByXml(tableString));
					sBuilder1.append("(").append(AttributeConvertUtil.getAttributeNameByXml(tableString));
					sBuilder1.append(");\n");
					break;
				}
			}
			if(!isMatching){
				sBuilder.append("//String ");
				sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(tableString));
				sBuilder.append(" = ").append(tableSpace);
				sBuilder.append(".getChildTextTrim(\"\");\n");
				
				//拼接类属性设置
				sBuilder1.append(tableName).append(".set");
				sBuilder1.append(AttributeConvertUtil.getAttributeSetNameByXml(tableString));
				sBuilder1.append("(").append(AttributeConvertUtil.getAttributeNameByXml(tableString));
				sBuilder1.append(");\n");
			}
		}

		sBuilder2.append("xml中结点数：").append(list.size());
		sBuilder2.append("\n表中属性数：").append(tableStrings.length);
		sBuilder2.append("\n匹配到总数：").append(matchingNumber);
		return sBuilder.toString() + sBuilder1.toString() + sBuilder2.toString();
	}
	
	/** 
	 * @Title: xmlTemplateToCode 
	 * @Description: 通过模版转换代码
	 * @param xmlStr xml字符串
	 * @param string2 表字符串
	 * @param tableSpace 
	 * @param tableName
	 * @return
	 * @return: String
	 */
	@SuppressWarnings("unchecked")
	public static String xmlTemplateToCode(String xmlStr, String string2, String tableSpace, String tableName) throws Exception{
		int matchingNumber = 0;//匹配到总数
		StringBuilder sBuilder = new StringBuilder();// 创建属性值获取
		StringBuilder sBuilder1 = new StringBuilder();// 创建类属性设置
		StringBuilder sBuilder2 = new StringBuilder();// 记录匹配数

		String[] tableStrings = string2.split("\n");
		Document document = DocumentHelper.parseText(xmlStr);
		Element root = document.getRootElement();
		
		String recNum = root.attributeValue("recordid");
		sBuilder.append("Element rec").append(recNum);
		sBuilder.append(" = body.getChild(\"").append(root.attributeValue("name"));
		sBuilder.append("\");\n");
		sBuilder.append("Element rec").append(recNum).append("Fields = rec");
		sBuilder.append(recNum).append(".getChild(Common.TO_XML_FIELDS_TAG);\n");
		
		sBuilder1.append(AttributeConvertUtil.getAttributeSetNameByXml(tableName));
		sBuilder1.append(" ").append(AttributeConvertUtil.getAttributeNameByXml(tableName));
		sBuilder1.append(" = new ").append(AttributeConvertUtil.getAttributeSetNameByXml(tableName));
		sBuilder1.append("();\n");
		
		List<Element> list = root.elements();
		// 遍历属性节点
		for (String tableString : tableStrings) {
			boolean isMatching = false;
			for (Element attribute : list) {
				if("MCFields".equals(attribute.getName())){
					List<Element> list2 = attribute.elements();
					for(Element attribute2 : list2){
						String EnglishName = attribute2.elementTextTrim("EnglishName").toUpperCase();
						if (tableString.toUpperCase().contains(EnglishName)
								|| EnglishName.contains(tableString.toUpperCase())) {
							matchingNumber++;
							isMatching = true;
							//拼接属性值获取
							sBuilder.append("String ");
							sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(tableString));
							sBuilder.append(" = rec").append(recNum).append("Fields.getChildTextTrim(\"");
							sBuilder.append(EnglishName);
							sBuilder.append("\");\n");
							
							//拼接类属性设置
							sBuilder1.append(tableName).append(".set");
							sBuilder1.append(AttributeConvertUtil.getAttributeSetNameByXml(tableString));
							sBuilder1.append("(").append(AttributeConvertUtil.getAttributeNameByXml(tableString));
							sBuilder1.append(");\n");
							break;
						}
					}
					break;
				}
				String EnglishName = attribute.elementTextTrim("EnglishName").toUpperCase();
				if (tableString.toUpperCase().contains(EnglishName)
						|| EnglishName.contains(tableString.toUpperCase())) {
					matchingNumber++;
					isMatching = true;
					//拼接属性值获取
					sBuilder.append("String ");
					sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(tableString));
					sBuilder.append(" = rec").append(recNum).append("Fields.getChildTextTrim(\"");
					sBuilder.append(EnglishName);
					sBuilder.append("\");\n");
					
					//拼接类属性设置
					sBuilder1.append(tableName).append(".set");
					sBuilder1.append(AttributeConvertUtil.getAttributeSetNameByXml(tableString));
					sBuilder1.append("(").append(AttributeConvertUtil.getAttributeNameByXml(tableString));
					sBuilder1.append(");\n");
					break;
				}
			}
			if(!isMatching){
				sBuilder.append("//String ");
				sBuilder.append(AttributeConvertUtil.getAttributeNameByXml(tableString));
				sBuilder.append(" = rec").append(recNum).append("Fields.getChildTextTrim(\"\");\n");
				
				//拼接类属性设置
				sBuilder1.append("//").append(tableName).append(".set");
				sBuilder1.append(AttributeConvertUtil.getAttributeSetNameByXml(tableString));
				sBuilder1.append("(").append(AttributeConvertUtil.getAttributeNameByXml(tableString));
				sBuilder1.append(");\n");
			}
		}

		sBuilder2.append("xml中结点数：").append(list.size());
		sBuilder2.append("\n表中属性数：").append(tableStrings.length);
		sBuilder2.append("\n匹配到总数：").append(matchingNumber);
		return sBuilder.toString() + sBuilder1.toString() + sBuilder2.toString();
	}
}
