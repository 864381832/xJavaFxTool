package com.xwintop.xJavaFxTool.services.littleTools;

import java.io.File;
import java.util.Collection;
import java.util.function.Consumer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.xwintop.xJavaFxTool.job.FileCopyJob;
import com.xwintop.xJavaFxTool.model.FileCopyTableBean;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

public class FileCopyService {
	private ObservableList<FileCopyTableBean> tableData;
	private SchedulerFactory sf = new StdSchedulerFactory();
	private String schedulerKeyGroup = "runFileCopy";
	private String schedulerKeyName = "runFileCopy" + System.currentTimeMillis();

	public void saveConfigure() throws Exception {
		saveConfigure(ConfigureUtil.getConfigureFile("fileCopyConfigure.properties"));
	}

	public void saveConfigure(File file) throws Exception {
		FileUtils.touch(file);
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
		xmlConfigure.clear();
		for (int i = 0; i < tableData.size(); i++) {
			xmlConfigure.setProperty("tableBean" + i, tableData.get(i).getPropertys());
		}
		xmlConfigure.save();
		TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
	}

	public void otherSaveConfigureAction() throws Exception {
		String fileName = "fileCopyConfigure.properties";
		File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.properties"));
		if (file != null) {
			saveConfigure(file);
			TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
		}
	}

	public void loadingConfigure() {
		loadingConfigure(ConfigureUtil.getConfigureFile("fileCopyConfigure.properties"));
	}

	public void loadingConfigure(File file) {
		try {
			tableData.clear();
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
			xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
				@Override
				public void accept(String t) {
					tableData.add(new FileCopyTableBean(xmlConfigure.getString(t)));
				}
			});
		} catch (Exception e) {
			try {
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

	public void copyAction() throws Exception {
		for (FileCopyTableBean tableBean : tableData) {
			if (tableBean.getIsCopy()) {
				int number = Integer.parseInt(tableBean.getCopyNumber());
				File fileOriginal = new File(tableBean.getCopyFileOriginalPath());
				File fileTarget = new File(tableBean.getCopyFileTargetPath());
				for (int i = 0; i < number; i++) {
					if (fileOriginal.isDirectory()) {
						if(tableBean.getIsRename()){
							Collection<File> files = FileUtils.listFiles(fileOriginal, null, false);
							for (File file : files) {
								String fileName = FileUtil.getRandomFileName(file);
								if (i != 0) {
									fileName = i+fileName;
								}
								FileUtils.copyFile(file, new File(fileTarget.getPath(), fileName));
							}
						}else{
							if (number == 1) {
								FileUtils.copyDirectory(fileOriginal, fileTarget, false);
							} else {
								Collection<File> files = FileUtils.listFiles(fileOriginal, null, false);
								for (File file : files) {
									FileUtils.copyFile(file, new File(fileTarget.getPath(), (i==0?"":i) + file.getName()));
								}
							}
						}
					} else {
						if(tableBean.getIsRename()){
							String fileName = FileUtil.getRandomFileName(fileOriginal);
							if (i != 0) {
								fileName = i+fileName;
							}
							File file = new File(fileTarget.getPath(), fileName);
							FileUtils.copyFile(fileOriginal,file);
						}else{
							if (i == 0) {
								FileUtils.copyFileToDirectory(fileOriginal, fileTarget);
							} else {
								FileUtils.copyFile(fileOriginal,
										new File(fileTarget.getPath(), i + fileOriginal.getName()));
							}
						}
					}
				}
				if (tableBean.getIsDelete()) {
					if (fileOriginal.isDirectory()) {
						FileUtils.deleteDirectory(fileOriginal);
					} else {
						FileUtils.deleteQuietly(fileOriginal);
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(FileCopyJob.class).withIdentity(schedulerKeyName, schedulerKeyGroup).build();
		jobDetail.getJobDataMap().put("fileCopyService", this);
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
		if(!scheduler.isStarted()){
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

	public ObservableList<FileCopyTableBean> getTableData() {
		return tableData;
	}

	public void setTableData(ObservableList<FileCopyTableBean> tableData) {
		this.tableData = tableData;
	}
	
}
