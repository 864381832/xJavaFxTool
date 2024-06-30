package com.xwintop.xJavaFxTool.manager.script.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: AviatorEngine
 * @Description: Aviator表达式解析器
 * @author: xufeng
 * @date: 2021/9/12 10:43
 */

public class AviatorEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map ctx) throws Exception {
        if (ctx == null) {
            return AviatorEvaluator.execute(script);
        } else {
            return AviatorEvaluator.execute(script, ctx);
        }
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        String script = FileUtils.readFileToString(scriptFile, StandardCharsets.UTF_8);
        return eval(script);
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
