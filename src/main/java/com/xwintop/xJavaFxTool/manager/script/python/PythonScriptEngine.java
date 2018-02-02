package com.xwintop.xJavaFxTool.manager.script.python;

import com.xwintop.xJavaFxTool.manager.script.ScriptEngine;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: PythonScriptEngine
 * @Description: Python脚本引擎
 * @author: xufeng
 * @date: 2018/1/28 22:56
 */

public class PythonScriptEngine implements ScriptEngine {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script,null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        PythonInterpreter jy = new PythonInterpreter();
        if (vars != null) {
            Iterator i = vars.keySet().iterator();
            while(i.hasNext()) {
                String k = (String)i.next();
                jy.set(k, vars.get(k));
            }
        }
        return jy.eval(script);
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        exec(scriptFile);
        return "执行Python脚本文件成功！";
    }

    @Override
    public void exec(String script) throws Exception {
        exec(script,null);
    }

    @Override
    public void exec(String script, Map vars) throws Exception {
        PythonInterpreter jy = new PythonInterpreter();
        if (vars != null) {
            Iterator i = vars.keySet().iterator();
            while(i.hasNext()) {
                String k = (String)i.next();
                jy.set(k, vars.get(k));
            }
        }
        jy.exec(script);
    }

    @Override
    public void exec(File scriptFile) throws Exception {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(scriptFile.getPath());
    }
}
