package com.xwintop.xJavaFxTool.manager.script.velocity;

import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: VelocityEngine
 * @Description: Velocity模板引擎
 * @author: xufeng
 * @date: 2021/9/12 17:01
 */

@Slf4j
public class VelocityEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map ctx) throws Exception {
        org.apache.velocity.app.VelocityEngine velocityEngine = new org.apache.velocity.app.VelocityEngine();
        velocityEngine.init();
        VelocityContext velocityContext = new VelocityContext(ctx);
        StringWriter stringWriter = new StringWriter();
        if (!velocityEngine.evaluate(velocityContext, stringWriter, "VelocityEngine", script)) {
            log.error("解析Velocity模板引擎失败！");
        }
        return stringWriter.toString();
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
