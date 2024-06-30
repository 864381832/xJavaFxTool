package com.xwintop.xJavaFxTool.manager.script.jexl;

import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: JexlEngine
 * @Description: Jexl表达式引擎
 * @author: xufeng
 * @date: 2021/9/12 11:40
 */

public class JexlEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        org.apache.commons.jexl3.JexlEngine jexl = new JexlBuilder().create();
        // Create an expression
        JexlExpression e = jexl.createExpression(script);
        // Create a context and add data
        JexlContext jc = new MapContext(vars);
        // Now evaluate the expression, getting the result
        return e.evaluate(jc);
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
