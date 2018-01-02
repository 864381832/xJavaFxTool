package com.xwintop.xJavaFxTool.job;

import com.xwintop.xJavaFxTool.services.littleTools.EmailToolService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @ClassName: EmailToolJob
 * @Description: 邮件工具类job
 * @author: xufeng
 * @date: 2018/1/3 0003 0:07
 */

public class EmailToolJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		EmailToolService emailToolService = (EmailToolService) context.getMergedJobDataMap().get("emailToolService");
		try {
			emailToolService.runAllAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JobKey jobKey = context.getJobDetail().getKey();
		System.out.println("执行了EmailToolJob:"+jobKey);
	}
}
