package com.xwintop.xJavaFxTool.services.developTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.developTools.JsonConvertToolController;
import com.xwintop.xcore.util.StrUtil;
import com.xwintop.xcore.util.XML2BeanUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.betwixt.schema.Element;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @ClassName: JsonConvertToolService
 * @Description: Json转换工具
 * @author: xufeng
 * @date: 2018/2/5 17:04
 */

@Getter
@Setter
@Slf4j
public class JsonConvertToolService {
    private JsonConvertToolController jsonConvertToolController;

    public void jsonToXmlAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        String afterString = XML2BeanUtils.bean2XmlString(JSON.parse(jsonString));
        jsonConvertToolController.getAfterTextArea().setText(afterString);
    }

    public void xmlToJsonAction() throws Exception {
        SAXReader saxReader = new SAXReader();
        String afterString = jsonConvertToolController.getAfterTextArea().getText();
//        Document document = saxReader.read(new StringReader(afterString));
//        String jsonString = JSON.toJSONString(document.getRootElement());
        String jsonString = JSON.toJSONString(XML2BeanUtils.xmlSring2Bean(Element.class, afterString), true);
        jsonConvertToolController.getJsonTextArea().setText(jsonString);
    }

    public void jsonToJavaBeanAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        Object jsonObject = JSON.parse(jsonString);
        String afterString = jsonToJavaBean("XRootBean", jsonObject);
        jsonConvertToolController.getAfterTextArea().setText(afterString.toString());
    }

    private String jsonToJavaBean(String beanName, Object jsonObject) {
        if (jsonObject instanceof List) {
            jsonObject = ((List) jsonObject).get(0);
        }
        StringBuilder afterString = new StringBuilder();
        StringBuilder importString = new StringBuilder("package ;");
        StringBuilder propertyString = new StringBuilder();
        StringBuilder getSetString = new StringBuilder();
        Map<String,String> subclass = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : ((JSONObject) jsonObject).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            String keyName = StrUtil.fristToUpCase(key);
            if (value instanceof String) {
                propertyString.append("\n\tprivate String " + key + ";");
                getSetString.append("\n\tpublic void set" + keyName + "(String " + key + "){\n\t\tthis." + key + " = " + key + ";\n\t}");
                getSetString.append("\n\tpublic String get" + keyName + "(){\n\t\treturn this." + key + ";\n\t}");
            } else if (value instanceof Integer) {
                propertyString.append("\n\tprivate int " + key + ";");
                getSetString.append("\n\tpublic void set" + keyName + "(int " + key + "){\n\t\tthis." + key + " = " + key + ";\n\t}");
                getSetString.append("\n\tpublic int get" + keyName + "(){\n\t\treturn this." + key + ";\n\t}");
            } else if (value instanceof BigDecimal) {
                propertyString.append("\n\tprivate double " + key + ";");
                getSetString.append("\n\tpublic void set" + keyName + "(double " + key + "){\n\t\tthis." + key + " = " + key + ";\n\t}");
                getSetString.append("\n\tpublic double get" + keyName + "(){\n\t\treturn this." + key + ";\n\t}");
            } else if (value instanceof List) {
                propertyString.append("\n\tprivate List<" + keyName + "> " + key + ";");
                importString.append("\nimport java.util.List;");
                getSetString.append("\n\tpublic void set" + keyName + "(List<" + keyName + "> " + key + "){\n\t\tthis." + key + " = " + key + ";\n\t}");
                getSetString.append("\n\tpublic List<" + keyName + "> " + "get" + keyName + "(){\n\t\treturn this." + key + ";\n\t}");
                String subclassString = jsonToJavaBean(keyName, value);
                subclass.put(keyName,subclassString);
            } else if (value instanceof Map) {
                propertyString.append("\n\tprivate " + keyName + " " + key + ";");
                String subclassString = jsonToJavaBean(keyName, value);
                subclass.put(keyName,subclassString);
                getSetString.append("\n\tpublic void set" + keyName + "(" + keyName + " " + key + "){\n\t\tthis." + key + " = " + key + ";\n\t}");
                getSetString.append("\n\tpublic " + keyName + " get" + keyName + "(){\n\t\treturn this." + key + ";\n\t}");
            }
        }
        afterString.append(importString.toString());
        afterString.append("\npublic class "+beanName+"\n{");
        afterString.append(propertyString.toString());
        afterString.append("\n"+getSetString.toString());
        afterString.append("\n}");
        subclass.forEach((key,value)->{
            afterString.append("\n\n=============================\n");
            afterString.append(value.toString());
        });
        return afterString.toString();
    }

    public void jsonToCAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        Object jsonObject = JSON.parse(jsonString);
        String afterString = jsonToCBean("XRootBean", jsonObject);
        jsonConvertToolController.getAfterTextArea().setText(afterString.toString());
    }

    private String jsonToCBean(String beanName, Object jsonObject) {
        if (jsonObject instanceof List) {
            jsonObject = ((List) jsonObject).get(0);
        }
        StringBuilder afterString = new StringBuilder();
        StringBuilder propertyString = new StringBuilder();
        Map<String,String> subclass = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : ((JSONObject) jsonObject).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            String keyName = StrUtil.fristToUpCase(key);
            if (value instanceof String) {
                propertyString.append("\n\tpublic string " + key + " {get; set;}");
            } else if (value instanceof Integer) {
                propertyString.append("\n\tpublic int " + key + " {get; set;}");
            } else if (value instanceof BigDecimal) {
                propertyString.append("\n\tpublic double " + key + " {get; set;}");
            } else if (value instanceof List) {
                propertyString.append("\n\tpublic List<" + keyName + "> " + key + " {get; set;}");
                String subclassString = jsonToCBean(keyName, value);
                subclass.put(keyName,subclassString);
            } else if (value instanceof Map) {
                propertyString.append("\n\tpublic " + keyName + " " + key + " {get; set;}");
                String subclassString = jsonToCBean(keyName, value);
                subclass.put(keyName,subclassString);
            }
        }
        afterString.append("\npublic class "+beanName+"\n{");
        afterString.append(propertyString.toString());
        afterString.append("\n}");
        subclass.forEach((key,value)->{
            afterString.append("\n\n=============================\n");
            afterString.append(value.toString());
        });
        return afterString.toString();
    }

    public void excelToJsonAction() throws Exception {
        String afterString = jsonConvertToolController.getAfterTextArea().getText();
        String[] afterStringStr = afterString.split("\n");
        String[] header = afterStringStr[0].split("\t");
        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i < afterStringStr.length; i++) {
            String[] objectStr = afterStringStr[i].split("\t");
            JSONObject jsonObject = new JSONObject();
            for (int j = 0; j < header.length; j++) {
                jsonObject.put(header[j], objectStr[j]);
            }
            jsonArray.add(jsonObject);
        }
        String jsonString = JSON.toJSONString(jsonArray, true);
        jsonConvertToolController.getJsonTextArea().setText(jsonString);
    }

    public void jsonToExcelAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        JSONArray jsonArray = JSON.parseArray(jsonString);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.join(jsonArray.getJSONObject(0).keySet(), "\t"));
        for (JSONObject jsonObject : jsonArray.toArray(new JSONObject[0])) {
            stringBuilder.append("\n" + StringUtils.join(jsonObject.values(), "\t"));
        }
        jsonConvertToolController.getAfterTextArea().setText(stringBuilder.toString());
    }

    public void jsonToYamlAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        String afterString = new Yaml().dump(JSON.parse(jsonString));
        jsonConvertToolController.getAfterTextArea().setText(afterString);

    }

    public void yamlToJsonAction() throws Exception {
        String afterString = jsonConvertToolController.getAfterTextArea().getText();
        String jsonString = JSON.toJSONString(new Yaml().load(afterString), true);
        jsonConvertToolController.getJsonTextArea().setText(jsonString);
    }

    public JsonConvertToolService(JsonConvertToolController jsonConvertToolController) {
        this.jsonConvertToolController = jsonConvertToolController;
    }
}