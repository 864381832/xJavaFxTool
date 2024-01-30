package com.xwintop.xJavaFxTool.job;

import com.xwintop.xJavaFxTool.services.debugTools.ActiveMqToolService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

public class ActiveMqToolJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ActiveMqToolService activeMqToolService = (ActiveMqToolService) context.getMergedJobDataMap().get("Service");
		try {
			activeMqToolService.sendAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 JobKey jobKey = context.getJobDetail().getKey();
		 System.out.println("执行了"+jobKey);
	}
}