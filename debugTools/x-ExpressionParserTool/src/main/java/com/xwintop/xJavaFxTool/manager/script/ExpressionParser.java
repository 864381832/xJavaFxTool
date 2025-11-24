package com.xwintop.xJavaFxTool.manager.script;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: ExpressionParser
 * @Description: 表达式解析器接口
 * @author: xufeng
 * @date: 2021/9/12 22:50
 */

public interface ExpressionParser {
    public Object eval(String script) throws Exception;//运行脚本并返回结果
    public Object eval(String script, Map vars) throws Exception;//带参数运行脚本并返回结果
    public Object eval(File scriptFile) throws Exception;//运行脚本文件并返回结果

    public void exec(String script) throws Exception;//运行脚本
    public void exec(String script, Map vars) throws Exception;//带参数运行脚本
    public void exec(File scriptFile) throws Exception;//运行脚本文件
}
