package com.xwintop.xJavaFxTool.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.xwintop.xJavaFxTool.services.littleTools.FileCopyService;

public class FileCopyJob implements Job{

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
