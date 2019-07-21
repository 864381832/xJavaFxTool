package com.xwintop.xJavaFxTool.manager.script;

/**
 * @ClassName: ScriptEngineType
 * @Description: 脚本引擎类型
 * @author: xufeng
 * @date: 2018/1/28 22:59
 */
public enum ScriptEngineType {
    JavaScript("JavaScript")
    ,Groovy("Groovy")
    ,Python("Python")
    ,Lua("Lua");
    private String value;
    ScriptEngineType(String value){
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static ScriptEngineType getEnum(String value) {
        ScriptEngineType e = null;
        for (ScriptEngineType e1 : ScriptEngineType.values()) {
            if (e1.value.equals(value)) {
                e = e1;
                break;
            }
        }
        return e;
    }
}
