package com.xwintop.xJavaFxTool.manager.script;

import com.xwintop.xJavaFxTool.manager.script.QLExpress.QLExpressEngine;
import com.xwintop.xJavaFxTool.manager.script.SpringEl.SpringElEngine;
import com.xwintop.xJavaFxTool.manager.script.aviator.AviatorEngine;
import com.xwintop.xJavaFxTool.manager.script.beanshell.BeanShellEngine;
import com.xwintop.xJavaFxTool.manager.script.freeMarker.FreeMarkerEngine;
import com.xwintop.xJavaFxTool.manager.script.mvel.MvelEngine;
import com.xwintop.xJavaFxTool.manager.script.jexl.JexlEngine;
import com.xwintop.xJavaFxTool.manager.script.stringTemplate.StringTemplateEngine;
import com.xwintop.xJavaFxTool.manager.script.velocity.VelocityEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: ExpressionParserManager
 * @Description: 表达式解析器管理类
 * @author: xufeng
 * @date: 2021/9/12 22:51
 */

@Getter
@Setter
@Slf4j
public class ExpressionParserManager implements ExpressionParser {
    private ExpressionParserType expressionParserType;
    private ExpressionParser expressionParser = null;

    public ExpressionParserManager() {
    }

    public ExpressionParserManager(String expressionParserType) {
        this.expressionParserType = ExpressionParserType.getEnum(expressionParserType);

    }

    public ExpressionParserManager(ExpressionParserType expressionParserType) {
        this.expressionParserType = expressionParserType;
    }

    public ExpressionParser getExpressionParser() {
        if (expressionParser == null) {
            expressionParser = getEngineByName(this.expressionParserType);
        }
        return expressionParser;
    }

    public static ExpressionParser getEngineByName(String scriptEngineType) {
        return getEngineByName(ExpressionParserType.getEnum(scriptEngineType));
    }

    /**
     * 根据类型获取脚本引擎
     */
    public static ExpressionParser getEngineByName(ExpressionParserType scriptEngineType) {
        ExpressionParser scriptEngine = null;
        switch (scriptEngineType) {
            case SpringEl:
                scriptEngine = new SpringElEngine();
                break;
            case Aviator:
                scriptEngine = new AviatorEngine();
                break;
            case Jexl:
                scriptEngine = new JexlEngine();
                break;
            case Mvel:
                scriptEngine = new MvelEngine();
                break;
            case BeanShell:
                scriptEngine = new BeanShellEngine();
                break;
            case Velocity:
                scriptEngine = new VelocityEngine();
                break;
            case FreeMarker:
                scriptEngine = new FreeMarkerEngine();
                break;
            case StringTemplate:
                scriptEngine = new StringTemplateEngine();
                break;
            case QLExpress:
                scriptEngine = new QLExpressEngine();
                break;
            default:
                break;
        }
        return scriptEngine;
    }

    @Override
    public Object eval(String script) throws Exception {
        return getExpressionParser().eval(script);
    }

    @Override
    public Object eval(String script, Map vars) throws Exception {
        return getExpressionParser().eval(script, vars);
    }

    @Override
    public Object eval(File scriptFile) throws Exception {
        return getExpressionParser().eval(scriptFile);
    }

    @Override
    public void exec(String script) throws Exception {
        getExpressionParser().exec(script);
    }

    @Override
    public void exec(String script, Map vars) throws Exception {
        getExpressionParser().exec(script, vars);
    }

    @Override
    public void exec(File scriptFile) throws Exception {
        getExpressionParser().exec(scriptFile);
    }
}
