package com.xwintop.xJavaFxTool.manager.script.stringTemplate;

import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import org.apache.commons.io.FileUtils;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: StringTemplateEngine
 * @Description: StringTemplate模板引擎
 * @author: xufeng
 * @date: 2021/9/12 21:58
 */

public class StringTemplateEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        ST st = new ST(script);
        if (vars != null) {
            vars.forEach((o, o2) -> {
                st.add(String.valueOf(o), o2);
            });
        }
        return st.render();
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
