package com.xwintop.xJavaFxTool.javafx.tool;

import com.xwintop.xcore.util.StrUtil;
import java.lang.reflect.Field;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

public class TableBeanTool {
	@Test
	public void buildTableBean(){
//		Class<?> beanClass = EmailToolTableBean.class;
		Class<?> beanClass = this.getClass();
		Field[] fields = FieldUtils.getAllFields(beanClass);
		StringBuffer soutStringBuffer = new StringBuffer();//输出字符串
		StringBuffer stringBuffer = new StringBuffer();//构造函数头
		StringBuffer stringBuffer2 = new StringBuffer();//构造函数结构
		StringBuffer stringBuffer3 = new StringBuffer();//构造函数2头
		StringBuffer stringBuffer4 = new StringBuffer();//获取构造函数
		StringBuffer stringBuffer5 = new StringBuffer();//获取getSet方法
		stringBuffer.append("public ").append(beanClass.getSimpleName()).append("(");
		stringBuffer3.append("public ").append(beanClass.getSimpleName()).append("(String propertys) {\nString[] strings = propertys.split(\"__\","+fields.length+");\n");
		stringBuffer4.append("public String getPropertys() {\nreturn ");
		int i = 0;
		for (Field field : fields) {
			String fieldName = field.getName();
			String typeName = field.getType().getSimpleName();
			String typeSimpleName = typeName.substring(6, typeName.indexOf("Property"));
			String typeClassName = typeName.substring(6);
			String UpFieldName = StrUtil.firstToUpCase(fieldName);
			
			stringBuffer.append(typeSimpleName).append(" "+fieldName+",");
			stringBuffer2.append("this."+fieldName+" = new "+typeName+"("+fieldName+");\n");
			
			if("Boolean".equals(typeSimpleName)){
				stringBuffer3.append("this."+fieldName+" = new "+typeName+"(Boolean.valueOf(strings["+i+"]));\n");
			}else if("Integer".equals(typeSimpleName)){
				stringBuffer3.append("this."+fieldName+" = new "+typeName+"(Integer.valueOf(strings["+i+"]));\n");
			}else{
				stringBuffer3.append("this."+fieldName+" = new "+typeName+"(strings["+i+"]);\n");
			}
			
			stringBuffer4.append(fieldName +".get() + \"__\" + ");
			
			if(!"String".equals(typeSimpleName)){
				stringBuffer5.append("public "+typeClassName+" "+fieldName+"Property(){\n");
				stringBuffer5.append("return "+fieldName+";\n}\n\n");
			}
			stringBuffer5.append("public "+typeSimpleName+" get"+UpFieldName+"(){\n");
			stringBuffer5.append("return "+fieldName+".get();\n}\n\n");
			stringBuffer5.append("public void set"+UpFieldName+"("+typeSimpleName+" "+fieldName+"){\n");
			stringBuffer5.append("this."+fieldName+".set("+fieldName+");\n}\n\n");
			
			i++;
		}
		stringBuffer.deleteCharAt(stringBuffer.length()-1).append("){\n");
		stringBuffer4.delete(stringBuffer4.length()-10, stringBuffer4.length()).append(";\n");
		
		soutStringBuffer.append(stringBuffer.toString()+stringBuffer2+"}\n\n");
		soutStringBuffer.append(stringBuffer3.toString()+"}\n\n");
		soutStringBuffer.append(stringBuffer4.toString()+"}\n\n");
		soutStringBuffer.append(stringBuffer5.toString()+"\n\n");
		System.out.println(soutStringBuffer);
	}
}
