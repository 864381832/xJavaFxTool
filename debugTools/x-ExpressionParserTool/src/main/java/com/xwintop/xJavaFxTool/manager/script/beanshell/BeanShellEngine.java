package com.xwintop.xJavaFxTool.manager.script.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: BeanShellEngine
 * @Description: BeanShell表达式引擎
 * @author: xufeng
 * @date: 2021/9/12 14:49
 */

@Slf4j
public class BeanShellEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        Interpreter interpreter = new Interpreter();
        if (vars != null) {
            vars.forEach((o, o2) -> {
                try {
                    interpreter.set(String.valueOf(o), o2);
                } catch (EvalError e) {
                    log.error("BeanShell设置属性失败！", e);
                }
            });
        }
        return interpreter.eval(script);
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        String script = FileUtils.readFileToString(scriptFile, StandardCharsets.UTF_8);
        return eval(script);
    }

    @Override
    public void exec(String script) throws Exception {
        exec(script, null);
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
