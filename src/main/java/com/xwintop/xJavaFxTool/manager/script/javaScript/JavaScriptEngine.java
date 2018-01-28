package com.xwintop.xJavaFxTool.manager.script.javaScript;

import com.xwintop.xJavaFxTool.manager.script.ScriptEngine;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @ClassName: JavaScriptEngine
 * @Description: javaScript脚本引擎
 * @author: xufeng
 * @date: 2018/1/28 22:55
 */
public class JavaScriptEngine implements ScriptEngine {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        javax.script.ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Object result = null;
        if(vars != null){
            Bindings bindings = new SimpleBindings(vars); //Local级别的Binding
            result = engine.eval(script, bindings);
        }else{
            result = engine.eval(script);
        }
        return result;
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        javax.script.ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Object result = engine.eval(new FileReader(scriptFile));
        return result;
    }

    @Override
    public void exec(String script) throws Exception {
        eval(script,null);
    }

    @Override
    public void exec(String script, Map vars) throws Exception {
        eval(script, vars);
    }

    @Override
    public void exec(File scriptFile) throws Exception {
        eval(scriptFile);
    }
}
