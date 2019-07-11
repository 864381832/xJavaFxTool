package com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrayProcessor {

    private final static Pattern pattern = Pattern.compile("(.*)\\[(\\d+)\\]");

    private final PropertyTree tree;

    public ArrayProcessor(PropertyTree tree) {
        this.tree = tree;
    }

    public PropertyTree apply() {
        return process(tree);
    }

    private PropertyTree process(final PropertyTree root) {
        final PropertyTree result = new PropertyTree();
        final Map<String, List<Object>> entriesFromList = new HashMap<>();
        root.entrySet().stream().forEach((entry) -> {
            Matcher matcher = pattern.matcher(entry.getKey());
            if (matcher.find()) {
                String label = matcher.group(1);
                int index = Integer.parseInt(matcher.group(2));
                entriesFromList.put(label, processListElement(entriesFromList.get(label), entry.getValue(), index));
            } else {
                result.put(entry.getKey(), getValue(entry.getValue()));
            }
        });
        result.putAll(entriesFromList);
        return result;
    }

    private List<Object> processListElement(final List<Object> elements, final Object value, final int index) {
        List<Object> result = elements == null ? new ArrayList<>() : new ArrayList<>(elements);
        adjustArray(index, result);
        result.add(index, getValue(value));
        return result;
    }

    private Object getValue(final Object value) {
        return value instanceof PropertyTree ? process((PropertyTree) value) : value;
    }

    private void adjustArray(final int index, List<Object> elementList) {
        if (elementList.size() < index) {
            for (int i = elementList.size(); i < index; i++) {
                elementList.add(i, null);
            }
        }
    }
}
