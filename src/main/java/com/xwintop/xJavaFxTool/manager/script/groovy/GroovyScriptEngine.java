package com.xwintop.xJavaFxTool.manager.script.groovy;

import com.xwintop.xJavaFxTool.manager.script.ScriptEngine;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.Reader;
import java.util.Map;

/**
 * @ClassName: GroovyScriptEngine
 * @Description: Groovy脚本引擎
 * @author: xufeng
 * @date: 2018/1/28 22:56
 */

public class GroovyScriptEngine implements ScriptEngine {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map ctx) throws Exception {
        Binding binding = new Binding(ctx);
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate(script);
        return value;
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(scriptFile);
        // 调用实例中的某个方法
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        Object[] args = {};
        Object value = groovyObject.invokeMethod("run", args);
        return value;
    }

    @Override
    public void exec(String script) throws Exception {
        eval(script, null);
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
