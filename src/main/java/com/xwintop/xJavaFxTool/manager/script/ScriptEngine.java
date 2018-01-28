package com.xwintop.xJavaFxTool.manager.script;

import javax.script.ScriptContext;
import java.io.File;
import java.util.Map;

/**
 * @ClassName: ScriptEngine
 * @Description: 脚本引擎接口
 * @author: xufeng
 * @date: 2018/1/28 22:56
 */
public interface ScriptEngine {
    public Object eval(String script) throws Exception;//运行脚本并返回结果
    public Object eval(String script, Map vars) throws Exception;//带参数运行脚本并返回结果
    public Object eval(File scriptFile) throws Exception;//运行脚本文件并返回结果

    public void exec(String script) throws Exception;//运行脚本
    public void exec(String script, Map vars) throws Exception;//带参数运行脚本
    public void exec(File scriptFile) throws Exception;//运行脚本文件
}
