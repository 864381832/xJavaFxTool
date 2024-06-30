package com.xwintop.xJavaFxTool.services.debugTools;

import com.alibaba.fastjson2.JSON;
import com.xwintop.xJavaFxTool.controller.debugTools.ExpressionParserToolController;
import com.xwintop.xJavaFxTool.job.ExpressionParserToolJob;
import com.xwintop.xJavaFxTool.manager.ExpressionParserScheduleManager;
import com.xwintop.xJavaFxTool.manager.script.ExpressionParserManager;
import com.xwintop.xJavaFxTool.model.ExpressionParserToolTableBean;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: ScriptEngineToolService
 * @Description: 脚本引擎调试工具类
 * @author: xufeng
 * @date: 2018/1/28 22:59
 */

@Getter
@Setter
@Slf4j
public class ExpressionParserToolService {
    private ExpressionParserToolController expressionParserToolController;
    private String fileName = "expressionParserToolConfigure.properties";
    private ExpressionParserScheduleManager scheduleManager = new ExpressionParserScheduleManager();

    public ExpressionParserToolService(ExpressionParserToolController expressionParserToolController) {
        this.expressionParserToolController = expressionParserToolController;
    }

    /**
     * @Title: runAllAction
     * @Description: 运行所有动作
     */
    public void runAllAction() {
        for (ExpressionParserToolTableBean scriptEngineToolTableBean : expressionParserToolController.getTableData()) {
            if (scriptEngineToolTableBean.getIsEnabled()) {
                runAction(scriptEngineToolTableBean);
            }
        }
    }

    /**
     * @Title: runAction
     * @Description: 单独运行
     */
    public void runAction(ExpressionParserToolTableBean scriptEngineToolTableBean) {
        String type = scriptEngineToolTableBean.getType();
        String script = scriptEngineToolTableBean.getScript();
        System.out.println("运行:" + type + " : " + script);
        addLog("运行:" + type + " : " + script);
        Map parameterMap = null;
        if (StringUtils.isNotEmpty(scriptEngineToolTableBean.getParameter())) {
            parameterMap = JSON.parseObject(scriptEngineToolTableBean.getParameter(), Map.class);
        }
        try {
            Object log = null;
            if (expressionParserToolController.getTypeChoiceBoxStrings()[0].equals(type)) {// SpringEl脚本
                log = new ExpressionParserManager("SpringEl").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[1].equals(type)) {// SpringEl脚本文件
                log = new ExpressionParserManager("SpringEl").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[2].equals(type)) {// Aviator脚本
                log = new ExpressionParserManager("Aviator").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[3].equals(type)) {// Aviator脚本文件
                log = new ExpressionParserManager("Aviator").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[4].equals(type)) {// Jexl脚本
                log = new ExpressionParserManager("Jexl").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[5].equals(type)) {// Jexl脚本文件
                log = new ExpressionParserManager("Jexl").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[6].equals(type)) {// Mvel脚本
                log = new ExpressionParserManager("Mvel").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[7].equals(type)) {// Mvel脚本文件
                log = new ExpressionParserManager("Mvel").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[8].equals(type)) {// BeanShell脚本
                log = new ExpressionParserManager("BeanShell").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[9].equals(type)) {// BeanShell脚本文件
                log = new ExpressionParserManager("BeanShell").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[10].equals(type)) {// Velocity脚本
                log = new ExpressionParserManager("Velocity").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[11].equals(type)) {// Velocity脚本文件
                log = new ExpressionParserManager("Velocity").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[12].equals(type)) {// FreeMarker脚本
                log = new ExpressionParserManager("FreeMarker").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[13].equals(type)) {// FreeMarker脚本文件
                log = new ExpressionParserManager("FreeMarker").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[14].equals(type)) {// StringTemplate脚本
                log = new ExpressionParserManager("StringTemplate").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[15].equals(type)) {// StringTemplate脚本文件
                log = new ExpressionParserManager("StringTemplate").eval(new File(script));
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[16].equals(type)) {// QLExpress脚本
                log = new ExpressionParserManager("QLExpress").eval(script, parameterMap);
            } else if (expressionParserToolController.getTypeChoiceBoxStrings()[17].equals(type)) {// QLExpress脚本文件
                log = new ExpressionParserManager("QLExpress").eval(new File(script));
            }
            addLog(log);
            //继续执行后触发任务
            if (scriptEngineToolTableBean.getIsRunAfterActivate()) {
                for (ExpressionParserToolTableBean tableBean : expressionParserToolController.getTableData()) {
                    if (tableBean.getOrder().equals(scriptEngineToolTableBean.getRunAfterActivate())) {
                        runAction(tableBean);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("运行脚本出错", e);
        }
    }


    /**
     * 向控制台添加日志信息
     *
     * @param log 日志
     */
    public void addLog(Object log) {
        if (log != null) {
            this.getExpressionParserToolController().getLogTextArea().appendText("\n" + log.toString());
        }
    }

    /**
     * @Title: showScriptAction
     * @Description: 查看脚本文件
     */
    public void showScriptAction(ExpressionParserToolTableBean scriptEngineToolTableBean) {
        String type = scriptEngineToolTableBean.getType();
        String script = scriptEngineToolTableBean.getScript();
        System.out.println("查看:" + type + " : " + script);
        try {
//            if (scriptEngineToolController.getTypeChoiceBoxStrings()[0].equals(type)) {// 命令行
//                AlertUtil.showInfoAlert("脚本命令", script);
//            } else if (scriptEngineToolController.getTypeChoiceBoxStrings()[1].equals(type)) {// 脚本文件
//                Runtime.getRuntime().exec("NotePad.exe " + script);
//            }
            if (type.contains("文件")) {// 命令行
                Runtime.getRuntime().exec("NotePad.exe " + script);
            } else {// 脚本文件
                AlertUtil.showInfoAlert("脚本命令", script);
            }
        } catch (Exception e) {
            log.error("查看脚本失败！！", e);
            TooltipUtil.showToast("查看脚本失败！！" + e.getMessage());
        }
    }

    public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        if ("简单表达式".equals(quartzType)) {
            scheduleManager.runQuartzAction(ExpressionParserToolJob.class, this, interval, repeatCount);
        } else if ("Cron表达式".equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                TooltipUtil.showToast("cron表达式不能为空。");
                return false;
            }
            scheduleManager.runQuartzAction(ExpressionParserToolJob.class, this, cronText);
        }
        return true;
    }

    public boolean stopQuartzAction() throws Exception {
        scheduleManager.stopQuartzAction();
        return true;
    }

    public void saveConfigure() throws Exception {
        saveConfigure(ConfigureUtil.getConfigureFile(fileName));
    }

    public void saveConfigure(File file) throws Exception {
        ConfigureUtil.getConfig(file).clear();
        for (int i = 0; i < expressionParserToolController.getTableData().size(); i++) {
            ConfigureUtil.set(file, "tableBean" + i, expressionParserToolController.getTableData().get(i).getPropertys());
        }
        TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
    }

    public void otherSaveConfigureAction() throws Exception {
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        if (file != null) {
            saveConfigure(file);
            TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
        }
    }

    public void loadingConfigure() {
        loadingConfigure(ConfigureUtil.getConfigureFile(fileName));
    }

    public void loadingConfigure(File file) {
        try {
            expressionParserToolController.getTableData().clear();
            Map xmlConfigure = ConfigureUtil.getConfig(file);
            for (Object t : xmlConfigure.keySet()) {
                expressionParserToolController.getTableData().add(new ExpressionParserToolTableBean((String) xmlConfigure.get(t)));
            }
        } catch (Exception e) {
            try {
                log.error("加载配置失败：" + e.getMessage());
                TooltipUtil.showToast("加载配置失败：" + e.getMessage());
            } catch (Exception e2) {
            }
        }
    }

    public void loadingConfigureAction() {
        File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        if (file != null) {
            loadingConfigure(file);
        }
    }
}