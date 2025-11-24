package com.xwintop.xJavaFxTool.manager.script.lua;

import com.xwintop.xJavaFxTool.manager.script.ScriptEngine;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: LuaScriptEngine
 * @Description: Lua脚本引擎
 * @author: xufeng
 * @date: 2018/1/29 13:06
 */
public class LuaScriptEngine implements ScriptEngine{
    @Override
    public Object eval(String script) throws Exception {
        return eval(script,null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load(script);
        return chunk.call();
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.loadfile(scriptFile.getPath());
        return chunk.call();
    }

    @Override
    public void exec(String script) throws Exception {
        eval(script,null);
    }

    @Override
    public void exec(String script, Map vars) throws Exception {
        eval(script,vars);
    }

    @Override
    public void exec(File scriptFile) throws Exception {
        eval(scriptFile);
    }
}
