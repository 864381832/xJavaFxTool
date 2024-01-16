package com.xwintop.xcore.util;

import org.apache.commons.lang3.StringUtils;

public class StrUtil {

    ///////////////////////////////////////////////////////////////

    /**
     * 去掉下划线并将字符串转换成帕斯卡命名规范
     *
     * @deprecated 使用 {@link org.apache.commons.text.CaseUtils}
     */
    public static String unlineToPascal(String str) {
        if (str != null) {
            StringBuilder result = new StringBuilder();
            String[] temp = str.split("_");
            for (String s : temp) {
                if ("".equals(s) || s.isEmpty()) {
                    continue;
                }
                result.append(firstToUpCaseLaterToLoCase(s));
            }
            return result.toString();
        }

        return null;
    }

    /**
     * 去掉下划线并将字符串转换成驼峰命名规范
     *
     * @deprecated 使用 {@link org.apache.commons.text.CaseUtils}
     */
    public static String unlineToCamel(String str) {
        if (str != null) {
            StringBuilder result = new StringBuilder();
            String[] temp = str.split("_");
            boolean somethingBoolean = false;
            for (String s : temp) {
                if ("".equals(s) || s.isEmpty()) {
                    continue;
                }
                if (!somethingBoolean) {
                    somethingBoolean = true;
                    result.append(s.toLowerCase());
                } else {
                    result.append(StringUtils.capitalize(s.toLowerCase()));
                }
            }
            return result.toString();
        }

        return str;
    }

    /**
     * 将字符串首字母大写其后小写
     *
     * @deprecated 使用 {@code StringUtils.capitalize(s.toLowerCase())}
     */
    public static String firstToUpCaseLaterToLoCase(String str) {
        if (str != null && str.length() > 1) {
            str = (str.substring(0, 1).toUpperCase()) + (str.substring(1).toLowerCase());
        }
        return str;
    }

    /**
     * 将字符串首字母小写其后大写
     *
     * @deprecated 使用 {@code StringUtils.uncapitalize(s.toUpperCase())}
     */
    public static String firstToLoCaseLaterToUpCase(String str) {
        if (str != null && str.length() > 1) {
            str = (str.substring(0, 1).toLowerCase()) + (str.substring(1).toUpperCase());

        }
        return str;
    }

    /**
     * 将字符串首字母大写
     *
     * @deprecated 使用 {@link org.apache.commons.lang3.StringUtils#capitalize(String)}
     */
    public static String firstToUpCase(String str) {
        if (str != null && str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    /**
     * 将字符串首字母小写
     *
     * @deprecated 使用 {@link org.apache.commons.lang3.StringUtils#uncapitalize(String)}
     */
    public static String firstToLoCase(String str) {
        if (str != null && str.length() > 1) {
            str = str.substring(0, 1).toLowerCase() + str.substring(1);
        }
        return str;
    }

}
