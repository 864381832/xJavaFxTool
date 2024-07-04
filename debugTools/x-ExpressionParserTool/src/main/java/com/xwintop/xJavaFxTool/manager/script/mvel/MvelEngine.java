package com.xwintop.xJavaFxTool.manager.script.mvel;

import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import org.mvel2.MVEL;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: MvelEngine
 * @Description: Mvel表达式解析器
 * @author: xufeng
 * @date: 2021/9/12 10:42
 */

public class MvelEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        return MVEL.eval(script, vars);
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        return MVEL.evalFile(scriptFile);
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
