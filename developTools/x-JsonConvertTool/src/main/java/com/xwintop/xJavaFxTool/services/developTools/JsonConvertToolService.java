package com.xwintop.xJavaFxTool.services.developTools;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.xwintop.xJavaFxTool.controller.developTools.JsonConvertToolController;
import com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolServiceUtil.ArrayProcessor;
import com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolServiceUtil.PropertyTree;
import com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolServiceUtil.TreeBuilder;
import com.xwintop.xJavaFxTool.utils.XML2BeanUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.betwixt.schema.Element;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.Yaml;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

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
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
//        Document document = saxReader.read(new StringReader(afterString));
//        String jsonString = JSON.toJSONString(document.getRootElement());
        String afterString = JSON.toJSONString(XML2BeanUtils.xmlSring2Bean(Element.class, jsonString));
        jsonConvertToolController.getAfterTextArea().setText(afterString);
    }

    public void jsonToJavaBeanAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        Object jsonObject = JSON.parse(jsonString);
        String afterString = jsonToJavaBean("XRootBean", jsonObject);
        jsonConvertToolController.getAfterTextArea().setText(afterString.toString());
    }

    public void jsonToJsonPathAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        Object jsonObject = JSON.parse(jsonString);
        Map pathMap = JSONPath.paths(jsonObject);
        StringBuffer afterString = new StringBuffer();
        TreeMap treeMap = new TreeMap(pathMap);
        treeMap.forEach((key, value) -> {
            if (!(value instanceof JSONObject) && !(value instanceof JSONArray)) {
//                System.out.println("$."+key.toString().substring(1).replace("/",".") + " = " + value);
                afterString.append("$.").append(key.toString().substring(1).replace("/", ".")).append("=").append(value).append("\n");
            }
        });
        jsonConvertToolController.getAfterTextArea().setText(afterString.toString());
    }

    private String jsonToJavaBean(String beanName, Object jsonObject) {
        if (jsonObject instanceof List) {
            jsonObject = ((List) jsonObject).get(0);
        }
        if (!(jsonObject instanceof JSONObject)) {
            return "";
        }
        StringBuilder afterString = new StringBuilder();
        StringBuilder importString = new StringBuilder("package ;");
        StringBuilder propertyString = new StringBuilder();
        StringBuilder getSetString = new StringBuilder();
        Map<String, String> subclass = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : ((JSONObject) jsonObject).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            String keyName = StrUtil.upperFirst(key);
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
                subclass.put(keyName, subclassString);
            } else if (value instanceof Map) {
                propertyString.append("\n\tprivate " + keyName + " " + key + ";");
                String subclassString = jsonToJavaBean(keyName, value);
                subclass.put(keyName, subclassString);
                getSetString.append("\n\tpublic void set" + keyName + "(" + keyName + " " + key + "){\n\t\tthis." + key + " = " + key + ";\n\t}");
                getSetString.append("\n\tpublic " + keyName + " get" + keyName + "(){\n\t\treturn this." + key + ";\n\t}");
            }
        }
        afterString.append(importString.toString());
        afterString.append("\npublic class " + beanName + "\n{");
        afterString.append(propertyString.toString());
        afterString.append("\n" + getSetString.toString());
        afterString.append("\n}");
        subclass.forEach((key, value) -> {
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
        if (!(jsonObject instanceof JSONObject)) {
            return "";
        }
        StringBuilder afterString = new StringBuilder();
        StringBuilder propertyString = new StringBuilder();
        Map<String, String> subclass = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : ((JSONObject) jsonObject).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            String keyName = StrUtil.upperFirst(key);
            if (value instanceof String) {
                propertyString.append("\n\tpublic string " + key + " {get; set;}");
            } else if (value instanceof Integer) {
                propertyString.append("\n\tpublic int " + key + " {get; set;}");
            } else if (value instanceof BigDecimal) {
                propertyString.append("\n\tpublic double " + key + " {get; set;}");
            } else if (value instanceof List) {
                propertyString.append("\n\tpublic List<" + keyName + "> " + key + " {get; set;}");
                String subclassString = jsonToCBean(keyName, value);
                subclass.put(keyName, subclassString);
            } else if (value instanceof Map) {
                propertyString.append("\n\tpublic " + keyName + " " + key + " {get; set;}");
                String subclassString = jsonToCBean(keyName, value);
                subclass.put(keyName, subclassString);
            }
        }
        afterString.append("\npublic class " + beanName + "\n{");
        afterString.append(propertyString.toString());
        afterString.append("\n}");
        subclass.forEach((key, value) -> {
            afterString.append("\n\n=============================\n");
            afterString.append(value.toString());
        });
        return afterString.toString();
    }

    public void excelToJsonAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        String[] afterStringStr = jsonString.split("\n");
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
        String afterString = JSON.toJSONString(jsonArray);
        jsonConvertToolController.getAfterTextArea().setText(afterString);
    }

    public void jsonToExcelAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        JSONArray jsonArray = null;
        if (JSON.isValidObject(jsonString)) {
            jsonArray = new JSONArray();
            jsonArray.add(JSON.parseObject(jsonString));
        } else {
            jsonArray = JSON.parseArray(jsonString);
        }
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
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        String afterString = JSON.toJSONString(new Yaml().load(jsonString));
        jsonConvertToolController.getAfterTextArea().setText(afterString);
    }

    public void propsToYamlAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        Properties properties = new Properties();
        properties.load(new StringReader(jsonString));
        PropertyTree tree = new TreeBuilder(properties, true).build();
        tree = new ArrayProcessor(tree).apply();
        jsonConvertToolController.getAfterTextArea().setText(tree.toYAML());
    }

    public void yamlToPropsAction() throws Exception {
        String jsonString = jsonConvertToolController.getJsonTextArea().getText();
        Yaml yaml = new Yaml();
        Map<String, Object> map = (Map<String, Object>) yaml.load(jsonString);
        Properties properties = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        iterateAndProcess(properties, map, "");
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            stringBuffer.append(key + "=" + properties.get(key) + "\n");
        }
        jsonConvertToolController.getAfterTextArea().setText(stringBuffer.toString());
    }

    public JsonConvertToolService(JsonConvertToolController jsonConvertToolController) {
        this.jsonConvertToolController = jsonConvertToolController;
    }

    private static void iterateAndProcess(Properties properties, Map<String, Object> ymlEntry, String rootKey) {
        for (String key : ymlEntry.keySet()) {
            Object value = ymlEntry.get(key);
            if (value instanceof Map) {
                iterateAndProcess(properties, (Map<String, Object>) value, StringUtils.isEmpty(rootKey) ? key : rootKey
                    + "." + key);
            } else {
                properties.setProperty(StringUtils.isEmpty(rootKey) ? key : rootKey + "." + key, value.toString());
            }
        }
    }
}
