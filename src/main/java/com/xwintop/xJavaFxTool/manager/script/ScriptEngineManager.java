package com.xwintop.xJavaFxTool.manager.script;

import com.xwintop.xJavaFxTool.manager.script.groovy.GroovyScriptEngine;
import com.xwintop.xJavaFxTool.manager.script.javaScript.JavaScriptEngine;
import com.xwintop.xJavaFxTool.manager.script.lua.LuaScriptEngine;
import com.xwintop.xJavaFxTool.manager.script.python.PythonScriptEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;
/**
 * @ClassName: ScriptEngineManager
 * @Description: 脚本引擎管理类
 * @author: xufeng
 * @date: 2018/1/28 22:58
 */
@Getter
@Setter
@Slf4j
public class ScriptEngineManager implements ScriptEngine{
    private ScriptEngineType scriptEngineType;
    private ScriptEngine scriptEngine = null;

    public ScriptEngineManager() {
    }

    public ScriptEngineManager(String scriptEngineType) {
        this.scriptEngineType = ScriptEngineType.getEnum(scriptEngineType);

    }
    public ScriptEngineManager(ScriptEngineType scriptEngineType) {
        this.scriptEngineType = scriptEngineType;
    }

    public ScriptEngine getScriptEngine(){
        if(scriptEngine == null){
            scriptEngine = getEngineByName(this.scriptEngineType);
        }
        return scriptEngine;
    }

    public static ScriptEngine getEngineByName(String scriptEngineType) {
        return getEngineByName(ScriptEngineType.getEnum(scriptEngineType));
    }

    /**
     * 根据类型获取脚本引擎
     */
    public static ScriptEngine getEngineByName(ScriptEngineType scriptEngineType) {
        ScriptEngine scriptEngine = null;
        switch (scriptEngineType) {
            case JavaScript:
                scriptEngine = new JavaScriptEngine();
                break;
            case Groovy:
                scriptEngine = new GroovyScriptEngine();
                break;
            case Python:
                scriptEngine = new PythonScriptEngine();
                break;
            case Lua:
                scriptEngine = new LuaScriptEngine();
                break;
            default:
                break;
        }
        return scriptEngine;
    }

    @Override
    public Object eval(String script) throws Exception {
        return getScriptEngine().eval(script);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        return getScriptEngine().eval(script,vars);
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        return getScriptEngine().eval(scriptFile);
    }

    @Override
    public void exec(String script) throws Exception {
        getScriptEngine().exec(script);
    }

    @Override
    public void exec(String script, Map vars) throws Exception {
        getScriptEngine().exec(script,vars);
    }

    @Override
    public void exec(File scriptFile) throws Exception {
        getScriptEngine().exec(scriptFile);
    }
}
