package com.xwintop.xJavaFxTool.utils;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;

@SuppressWarnings("unchecked")
public class NodeUtils {

    public static <T> T getUserData(Node node, String key) {
        return node.getUserData() == null ? null :
            !(node.getUserData() instanceof Map) ? null :
                (T) ((Map<String, Object>) node.getUserData()).get(key);
    }

    public static void setUserData(Node node, String key, Object value) {
        Map<String, Object> map = (Map<String, Object>) node.getUserData();
        if (map == null) {
            map = new HashMap<>();
            node.setUserData(map);
        }
        map.put(key, value);
    }
}
