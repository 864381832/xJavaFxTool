package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.CmdToolController;
import com.xwintop.xJavaFxTool.job.CmdToolJob;
import com.xwintop.xJavaFxTool.model.CmdToolTableBean;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.util.function.Consumer;

/**
 * @ClassName: CmdToolService
 * @Description: Cmd调试工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:28
 */

@Getter
@Setter
@Slf4j
public class CmdToolService {
    private CmdToolController cmdToolController;

    private String fileName = "cmdToolConfigure.properties";
    private SchedulerFactory sf = new StdSchedulerFactory();
    private String schedulerKeyGroup = "runCmdTool";
    private String schedulerKeyName = "runCmdTool" + System.currentTimeMillis();

    public CmdToolService(CmdToolController cmdToolController) {
        this.cmdToolController = cmdToolController;
    }

    /**
     * @Title: runAllAction
     * @Description: 运行所有动作
     */
    public void runAllAction() {
        for (CmdToolTableBean cmdToolTableBean : cmdToolController.getTableData()) {
            if (cmdToolTableBean.getIsEnabled()) {
                runAction(cmdToolTableBean);
            }
        }
    }

    /**
     * @Title: runAction
     * @Description: 单独运行
     */
    public void runAction(CmdToolTableBean cmdToolTableBean) {
        String type = cmdToolTableBean.getType();
        String script = cmdToolTableBean.getScript();
        System.out.println("运行:" + type + " : " + script);
        try {
            if (cmdToolController.getTypeChoiceBoxStrings()[0].equals(type)) {// 命令行
                String[] strings = script.split("&&&");
                if (strings.length > 1) {
                    Runtime.getRuntime().exec(strings[0], null, new File(strings[1]));
                } else {
                    Runtime.getRuntime().exec(script);
                }
            } else if (cmdToolController.getTypeChoiceBoxStrings()[1].equals(type)) {// 脚本文件
                File file = new File(script);
                Runtime.getRuntime().exec("cmd /k start " + file.getName(), null, file.getParentFile());
            }
            //继续执行后触发任务
            if (cmdToolTableBean.getIsRunAfterActivate()) {
                for (CmdToolTableBean tableBean : cmdToolController.getTableData()) {
                    if (tableBean.getOrder().equals(cmdToolTableBean.getRunAfterActivate())) {
                        runAction(tableBean);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("运行cmd失败", e);
        }
    }

    /**
     * @Title: showScriptAction
     * @Description: 查看脚本文件
     */
    public void showScriptAction(CmdToolTableBean cmdToolTableBean) {
        String type = cmdToolTableBean.getType();
        String script = cmdToolTableBean.getScript();
        log.info("查看:" + type + " : " + script);
        try {
            if (cmdToolController.getTypeChoiceBoxStrings()[0].equals(type)) {// 命令行
                AlertUtil.showInfoAlert("脚本命令", script);
            } else if (cmdToolController.getTypeChoiceBoxStrings()[1].equals(type)) {// 脚本文件
                Runtime.getRuntime().exec("NotePad.exe " + script);
            }
        } catch (Exception e) {
            log.error("查看脚本文件失败", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(CmdToolJob.class).withIdentity(schedulerKeyName, schedulerKeyGroup)
                .build();
        jobDetail.getJobDataMap().put("cmdToolService", this);
        ScheduleBuilder scheduleBuilder = null;
        if ("简单表达式".equals(quartzType)) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)// 时间间隔
                    .withRepeatCount(repeatCount);// 重复次数（将执行6次）
        } else if ("Cron表达式".equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                TooltipUtil.showToast("cron表达式不能为空。");
                return false;
            }
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronText);
        }
        // 描叙触发Job执行的时间触发规则,Trigger实例化一个触发器
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(schedulerKeyName, schedulerKeyGroup)// 给触发器一个名字和组名
                .startNow()// 立即执行
                .withSchedule(scheduleBuilder).build();// 产生触发器

        // 运行容器，使用SchedulerFactory创建Scheduler实例
        Scheduler scheduler = sf.getScheduler();
        // 向Scheduler添加一个job和trigger
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
        return true;
    }

    public boolean stopQuartzAction() throws Exception {
        Scheduler sched = sf.getScheduler();
        sched.unscheduleJob(new TriggerKey(schedulerKeyName, schedulerKeyGroup));
        sched.deleteJob(new JobKey(schedulerKeyName, schedulerKeyGroup));
        return true;
    }

    public void saveConfigure() throws Exception {
        saveConfigure(ConfigureUtil.getConfigureFile(fileName));
    }

    public void saveConfigure(File file) throws Exception {
        FileUtils.touch(file);
        PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
        xmlConfigure.clear();
        for (int i = 0; i < cmdToolController.getTableData().size(); i++) {
            xmlConfigure.setProperty("tableBean" + i, cmdToolController.getTableData().get(i).getPropertys());
        }
        xmlConfigure.save();
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
            cmdToolController.getTableData().clear();
            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
            xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
                @Override
                public void accept(String t) {
                    cmdToolController.getTableData().add(new CmdToolTableBean(xmlConfigure.getString(t)));
                }
            });
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
