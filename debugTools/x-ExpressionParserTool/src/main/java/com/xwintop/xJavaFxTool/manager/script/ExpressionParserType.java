package com.xwintop.xJavaFxTool.manager.script;

/**
 * @ClassName: ExpressionParserType
 * @Description: 表达式解析器类型
 * @author: xufeng
 * @date: 2021/9/12 22:55
 */

public enum ExpressionParserType {
    SpringEl("SpringEl"),
    Aviator("Aviator"),
    Jexl("Jexl"),
    BeanShell("BeanShell"),
    Mvel("Mvel"),
    Velocity("Velocity"),
    StringTemplate("StringTemplate"),
    QLExpress("QLExpress"),
    FreeMarker("FreeMarker");
    private String value;

    ExpressionParserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExpressionParserType getEnum(String value) {
        ExpressionParserType e = null;
        for (ExpressionParserType e1 : ExpressionParserType.values()) {
            if (e1.value.equals(value)) {
                e = e1;
                break;
            }
        }
        return e;
    }
}
