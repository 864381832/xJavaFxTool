package com.xwintop.xJavaFxTool.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import com.xwintop.xJavaFxTool.services.debugTools.ActiveMqToolService;

public class ActiveMqToolJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ActiveMqToolService activeMqToolService = (ActiveMqToolService) context.getMergedJobDataMap().get("activeMqToolService");
		try {
			activeMqToolService.sendAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 JobKey jobKey = context.getJobDetail().getKey();
		 System.out.println("执行了"+jobKey);
	}
}