package com.xwintop.xJavaFxTool.manager.script.QLExpress;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: QLExpressEngine
 * @Description: QLExpress脚本引擎
 * @author: xufeng
 * @date: 2021/9/13 18:34
 */

@Slf4j
public class QLExpressEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        if (vars != null) {
            vars.forEach((o, o2) -> {
                context.put(String.valueOf(o), o2);
            });
        }
        return runner.execute(script, context, null, true, false);
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
