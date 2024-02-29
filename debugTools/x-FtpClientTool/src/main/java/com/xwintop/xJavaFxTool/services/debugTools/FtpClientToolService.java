package com.xwintop.xJavaFxTool.services.debugTools;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.xwintop.xJavaFxTool.controller.debugTools.FtpClientToolController;
import com.xwintop.xJavaFxTool.job.FtpClientToolJob;
import com.xwintop.xJavaFxTool.model.FtpClientToolTableBean;
import com.xwintop.xJavaFxTool.utils.FtpUtil;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: FtpClientToolService
 * @Description: Ftp(s)/Sftp客户端调试工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:28
 */

@Getter
@Setter
@Slf4j
public class FtpClientToolService {
    private FtpClientToolController ftpClientToolController;

    private String fileName = "ftpClientToolConfigure.json";
    private SchedulerFactory sf = new StdSchedulerFactory();
    private String schedulerKeyGroup = "ftpClientTool";
    private String schedulerKeyName = "ftpClientTool" + System.currentTimeMillis();

    /**
     * @Title: runAllAction
     * @Description: 运行所有动作
     */
    public void runAllAction() {
        for (FtpClientToolTableBean ftpClientToolTableBean : ftpClientToolController.getTableData()) {
            if (ftpClientToolTableBean.getIsEnabled()) {
                runAction(ftpClientToolTableBean);
            }
        }
    }

    /**
     * @Title: runAction
     * @Description: 单独运行
     */
    public void runAction(FtpClientToolTableBean ftpClientToolTableBean) {
        String type = ftpClientToolTableBean.getType();
        String localFile = ftpClientToolTableBean.getLocalFile();
        String serverFile = ftpClientToolTableBean.getServerFile();
        System.out.println("运行:" + type + " : " + localFile + " : " + serverFile);
        String url = ftpClientToolController.getUrlTextField().getText();
        int port = Integer.parseInt(ftpClientToolController.getPortTextField().getText());
        String username = ftpClientToolController.getUserNameTextField().getText();
        String password = ftpClientToolController.getPasswordTextField().getText();
        int connectionType = ftpClientToolController.getConnectionTypeChoiceBox().getSelectionModel().getSelectedIndex();
        FtpUtil ftpUtil = new FtpUtil(url, port, username, password);
        ChannelSftp sftp = null;
        try {
            if (connectionType == 0) {

            } else if (connectionType == 1) {
                JSch jSch = new JSch(); //创建JSch对象
                Session session = jSch.getSession(username, url, port);//根据用户名，主机ip和端口获取一个Session对象
                session.setPassword(password);//设置密码
                Properties sftpConfig = new Properties();
                sftpConfig.put("StrictHostKeyChecking", "no");
                session.setConfig(sftpConfig);//为Session对象设置properties
                session.connect();//通过Session建立连接
                sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect();
            } else if (connectionType == 2) {
                ftpUtil.setFtps(true);
                ftpUtil.setImplicit(true);
            } else if (connectionType == 3) {
                ftpUtil.setFtps(true);
                ftpUtil.setImplicit(false);
                ftpUtil.setProtocol("SSL");
            } else if (connectionType == 4) {
                ftpUtil.setFtps(true);
                ftpUtil.setImplicit(false);
                ftpUtil.setProtocol("TLS");
            }
            if (connectionType != 1) {
                ftpUtil.setPassive(ftpClientToolController.getPassiveCheckBox().isSelected());
                ftpUtil.checkAndConnect();
            }
            if (ftpClientToolController.getTypeChoiceBoxStrings()[0].equals(type)) {// 上传
                if (connectionType != 1) {
                    ftpUtil.changeStringDirectory(serverFile);
                }
                File file = new File(localFile);
                if (file.isFile()) {
                    boolean flag = true;
                    if (connectionType == 1) {
                        serverFile = StringUtils.appendIfMissing(serverFile, "/", "/", "\\");
                        sftp.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(file)), serverFile + file.getName());
                    } else {
                        flag = ftpUtil.uploadFile(file.getName(), FileUtils.readFileToByteArray(file));
                    }
                    if (flag) {
                        TooltipUtil.showToast("文件上传成功");
                    } else {
                        TooltipUtil.showToast("文件：" + file.getName() + "上传失败：" + ftpUtil.getFtp().getReplyString());
                    }
                } else {
                    for (File file1 : file.listFiles()) {
                        boolean flag = true;
                        if (connectionType == 1) {
                            serverFile = StringUtils.appendIfMissing(serverFile, "/", "/", "\\");
                            sftp.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(file1)), serverFile + file1.getName());
                        } else {
                            flag = ftpUtil.uploadFile(file1.getName(), FileUtils.readFileToByteArray(file1));
                        }
                        if (flag) {
                            TooltipUtil.showToast("文件上传成功");
                        } else {
                            TooltipUtil.showToast("文件：" + file1.getName() + "上传失败：" + ftpUtil.getFtp().getReplyString());
                        }
                    }
                }

            } else if (ftpClientToolController.getTypeChoiceBoxStrings()[1].equals(type)) {// 下载
                File file = new File(serverFile);
                if (connectionType == 1) {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(localFile, file.getName()));
                    try {
                        sftp.get(serverFile, fileOutputStream);
                    } finally {
                        fileOutputStream.close();
                    }
                } else {
                    byte[] messageByte = ftpUtil.downFile(serverFile);
                    FileUtils.writeByteArrayToFile(new File(localFile, file.getName()), messageByte);
                }
                TooltipUtil.showToast("文件" + file.getName() + "下载成功，保存在:" + localFile);
            } else if (ftpClientToolController.getTypeChoiceBoxStrings()[2].equals(type)) {// 删除文件
                boolean flag = true;
                if (connectionType == 1) {
                    sftp.rm(serverFile);
                } else {
                    flag = ftpUtil.deleteFile(serverFile);
                }
                if (flag) {
                    TooltipUtil.showToast("文件：" + serverFile + "删除成功");
                } else {
                    TooltipUtil.showToast("文件：" + serverFile + "删除失败：" + ftpUtil.getFtp().getReplyString());
                }
            } else if (ftpClientToolController.getTypeChoiceBoxStrings()[3].equals(type)) {// 删除文件夹
                boolean flag = true;
                if (connectionType == 1) {
                    sftp.rmdir(serverFile);
                } else {
                    flag = ftpUtil.removeDirectory(serverFile);
                }
                if (flag) {
                    TooltipUtil.showToast("文件夹：" + serverFile + "删除成功");
                } else {
                    TooltipUtil.showToast("文件夹：" + serverFile + "删除失败：" + ftpUtil.getFtp().getReplyString());
                }
            }
            if (connectionType == 1) {
                try {
                    sftp.disconnect();
                    sftp.getSession().disconnect();
                } catch (Exception e) {
                    log.error("关闭连接失败：", e);
                }
            } else {
                ftpUtil.destroy();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TooltipUtil.showToast("运行失败:" + e.getMessage());
        }
    }

    public FtpClientToolService(FtpClientToolController ftpClientToolController) {
        this.ftpClientToolController = ftpClientToolController;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(FtpClientToolJob.class)
                .withIdentity(schedulerKeyName, schedulerKeyGroup).build();
        jobDetail.getJobDataMap().put("ftpClientToolService", this);
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
        ConfigureUtil.getConfig(file).clear();
        for (int i = 0; i < ftpClientToolController.getTableData().size(); i++) {
            ConfigureUtil.set(file, "tableBean" + i, ftpClientToolController.getTableData().get(i).getPropertys());
        }
        TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
    }

    public void otherSaveConfigureAction() throws Exception {
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.json"));
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
            ftpClientToolController.getTableData().clear();
            Map xmlConfigure = ConfigureUtil.getConfig(file);
            for (Object key : xmlConfigure.keySet()) {
                ftpClientToolController.getTableData().add(new FtpClientToolTableBean((String) xmlConfigure.get(key)));
            }
        } catch (Exception e) {
            try {
                log.error("加载配置失败：", e);
                TooltipUtil.showToast("加载配置失败：" + e.getMessage());
            } catch (Exception e2) {
            }
        }
    }

    public void loadingConfigureAction() {
        File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.json"));
        if (file != null) {
            loadingConfigure(file);
        }
    }

}
