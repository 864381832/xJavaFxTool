package com.xwintop.xJavaFxTool.manager.script.SpringEl;

import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import org.apache.commons.io.FileUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: SpringElEngine
 * @Description: SpringEl表达式解析器
 * @author: xufeng
 * @date: 2021/9/11 23:38
 */

public class SpringElEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        org.springframework.expression.ExpressionParser paser = new SpelExpressionParser();//创建表达式解析器
        //通过evaluationContext.setVariable可以在上下文中设定变量。
        EvaluationContext context = new StandardEvaluationContext();
        if (vars != null) {
            vars.forEach((o, o2) -> {
                context.setVariable(String.valueOf(o), o2);
            });
        }
        //解析表达式，如果表达式是一个模板表达式，需要为解析传入模板解析器上下文。
        Expression expression = paser.parseExpression(script);
        return expression.getValue(context);
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
