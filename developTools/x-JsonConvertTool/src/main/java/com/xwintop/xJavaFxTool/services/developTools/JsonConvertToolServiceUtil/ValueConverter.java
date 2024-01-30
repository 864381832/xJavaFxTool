package com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolServiceUtil;

class ValueConverter {

    public static Object asObject(String string) {
        if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
            return Boolean.valueOf(string);
        } else {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
            }
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e) {
            }
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
            }
            return string;
        }
    }
}
