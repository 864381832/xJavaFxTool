package com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolServiceUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class PropertyTree extends TreeMap<String, Object> {

    public PropertyTree() {
    }

    public PropertyTree(String key, Object value) {
        put(key, value);
    }

    public void appendBranchFromKeyValue(List<String> keyPath, Object value) {
        appendBranch(new TreeBranchBuilder(keyPath, value).build());
    }

    private void appendBranch(PropertyTree branchTree) {
        branchTree.forEach((key, value) -> {
            Optional<PropertyTree> removedBranch = removeMixedTypedBranch(key, value);
            merge(key, value, (root, branch) -> resolveDuplicates((PropertyTree) root, (PropertyTree) branch));
            removedBranch.ifPresent(this::appendBranch);
        });
    }

    private Optional<PropertyTree> removeMixedTypedBranch(String key, Object value) {
        PropertyTree removedBranch = null;
        if (containsKey(key) && isTerminationNode(key, value)) {
            removedBranch = new PropertyTree(key + flatKey(get(key)), flatValue(get(key)));
            remove(key);
        }
        return Optional.ofNullable(removedBranch);
    }

    private boolean isTerminationNode(String key, Object value) {
        return !(get(key) instanceof PropertyTree && value instanceof PropertyTree);
    }

    private String flatKey(Object tree) {
        if (tree instanceof PropertyTree) {
            Map.Entry<String, Object> next = getNext((PropertyTree) tree);
            return "." + next.getKey() + flatKey(next.getValue());
        } else {
            return "";
        }
    }

    private Object flatValue(Object tree) {
        if (tree instanceof PropertyTree) {
            return flatValue(getNext((PropertyTree) tree).getValue());
        } else {
            return tree;
        }
    }

    private Map.Entry<String, Object> getNext(PropertyTree tree) {
        return tree.entrySet().iterator().next();
    }

    private static Object resolveDuplicates(PropertyTree root, PropertyTree branch) {
        root.appendBranch(branch);
        return root;
    }

    public String toYAML() {
        return new YamlPrinter(this).invoke();
    }
}
