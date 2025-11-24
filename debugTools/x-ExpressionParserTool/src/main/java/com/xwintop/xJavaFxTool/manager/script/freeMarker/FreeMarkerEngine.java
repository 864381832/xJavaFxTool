package com.xwintop.xJavaFxTool.manager.script.freeMarker;

import com.xwintop.xJavaFxTool.manager.script.ExpressionParser;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName: FreeMarkerEngine
 * @Description: FreeMarker模板引擎
 * @author: xufeng
 * @date: 2021/9/12 19:03
 */

@Slf4j
public class FreeMarkerEngine implements ExpressionParser {
    @Override
    public Object eval(String script) throws Exception {
        return eval(script, null);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        Template template;

        stringTemplateLoader.putTemplate("freeMarker.ftl", script);
        configuration.setTemplateLoader(stringTemplateLoader);
        template = configuration.getTemplate("freeMarker.ftl");

        StringWriter stringWriter = new StringWriter();
        template.process(vars, stringWriter);
        return stringWriter.toString();
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
