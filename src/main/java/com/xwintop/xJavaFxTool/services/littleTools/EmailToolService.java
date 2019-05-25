package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.EmailToolController;
import com.xwintop.xJavaFxTool.job.EmailToolJob;
import com.xwintop.xJavaFxTool.model.EmailToolTableBean;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.mail.internet.InternetAddress;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @ClassName: EmailToolService
 * @Description: 邮件发送工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:04
 */
@Getter
@Setter
@Slf4j
public class EmailToolService {
    private EmailToolController emailToolController;

    private String fileName = "emailToolConfigure.properties";
    private SchedulerFactory sf = new StdSchedulerFactory();
    private String schedulerKeyGroup = "emailTool";
    private String schedulerKeyName = "emailTool" + System.currentTimeMillis();

    public void runAllAction() {
        ArrayList<EmailToolTableBean> emailToolTableBeanArrayList = new ArrayList<EmailToolTableBean>();
        for (EmailToolTableBean emailToolTableBean : emailToolController.getTableData()) {
            if (emailToolTableBean.getIsEnabled()) {
                emailToolTableBeanArrayList.add(emailToolTableBean);
            }
        }
        if (!emailToolTableBeanArrayList.isEmpty()) {
            runAction(emailToolTableBeanArrayList.toArray(new EmailToolTableBean[0]));
        }else{
            TooltipUtil.showToast("未选择收件人！！！");
        }
    }

    public void runAction(EmailToolTableBean... emailToolTableBeans) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(emailToolController.getHostNameComboBox().getValue());
            email.setSmtpPort(Integer.parseInt(emailToolController.getPortTextField().getText()));
            email.setAuthentication(emailToolController.getUserNameTextField().getText(), emailToolController.getPasswordPasswordField().getText());
            email.setSSLOnConnect(emailToolController.getSslCheckBox().isSelected());
            email.setFrom(emailToolController.getUserNameTextField().getText());
            email.setSubject(emailToolController.getSubjectTextField().getText());
            email.setCharset("utf-8");
            if (emailToolController.getAttachCheckBox().isSelected()) {
                for (Map<String, String> map : emailToolController.getAttachPathTableData()) {
                    EmailAttachment attachment = new EmailAttachment();
                    attachment.setName(map.get("attachName"));
                    String attachPath = map.get("attachPath");
                    if (attachPath.startsWith("http")) {
                        attachment.setURL(new URL(attachPath));//网络文件
                    } else {
                        attachment.setPath(attachPath);//本地文件
                    }
                    attachment.setDescription(map.get("attachDescription"));
                    email.attach(attachment);
                }
            }
            if (emailToolController.getSentSeparatelyCheckBox().isSelected()) {
                for (EmailToolTableBean emailToolTableBean : emailToolTableBeans) {
                    ArrayList<InternetAddress> toList = new ArrayList<InternetAddress>();
                    toList.add(new InternetAddress(emailToolTableBean.getToEmail(), emailToolTableBean.getToEmailName()));
                    email.setTo(toList);
                    String htmlMsg = emailToolController.getMsgHtmlEditor().getHtmlText()
                            .replace("${1}",emailToolTableBean.getToEmail())
                            .replace("${2}",emailToolTableBean.getToEmailName());
                    email.setHtmlMsg(htmlMsg);
                    email.send();
                }
            } else {
                email.setHtmlMsg(emailToolController.getMsgHtmlEditor().getHtmlText());
                for (EmailToolTableBean emailToolTableBean : emailToolTableBeans) {
                    email.addTo(emailToolTableBean.getToEmail(), emailToolTableBean.getToEmailName());
                }
                email.send();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 导入收件人邮箱
     */
    public void importToEmailAction() {
        try {
            File file = FileChooserUtil.chooseFile();
            if (file != null) {
                List<String> emailList = FileUtils.readLines(file, "utf-8");
                ObservableList<EmailToolTableBean> tableData = emailToolController.getTableData();
                for(String email : emailList){
                    EmailToolTableBean emailToolTableBean = null;
                    if(email.contains(" ")){
                        String[] emailStr = email.split(" ");
                        emailToolTableBean = new EmailToolTableBean(tableData.size()+1,true, emailStr[0], emailStr[1], "");
                    }else{
                        emailToolTableBean = new EmailToolTableBean(tableData.size()+1,true, email, email, "");
                    }
                    tableData.add(emailToolTableBean);
                }
            }
        } catch (Exception e) {
            log.error("导入收件人邮箱失败："+e.getMessage());
            TooltipUtil.showToast("导入收件人邮箱失败："+e.getMessage());
        }
    }

    public EmailToolService(EmailToolController emailToolController) {
        this.emailToolController = emailToolController;
    }

    public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(EmailToolJob.class)
                .withIdentity(schedulerKeyName, schedulerKeyGroup).build();
        jobDetail.getJobDataMap().put("emailToolService", this);
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
        for (int i = 0; i < emailToolController.getTableData().size(); i++) {
            xmlConfigure.setProperty("tableBean" + i, emailToolController.getTableData().get(i).getPropertys());
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
            emailToolController.getTableData().clear();
            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
            xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
                @Override
                public void accept(String t) {
                    emailToolController.getTableData().add(new EmailToolTableBean(xmlConfigure.getString(t)));
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