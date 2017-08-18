package com.xwintop.xJavaFxTool.services.littleTools;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class FileCopyServiceJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		FileCopyService fileCopyService = (FileCopyService) context.getMergedJobDataMap().get("fileCopyService");
		try {
			fileCopyService.copyAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		JobKey jobKey = context.getJobDetail().getKey();
//		System.out.println("执行了"+jobKey);
	}
}
