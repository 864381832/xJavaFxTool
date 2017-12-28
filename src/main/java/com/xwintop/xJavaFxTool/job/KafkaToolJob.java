package com.xwintop.xJavaFxTool.job;

import com.xwintop.xJavaFxTool.services.debugTools.KafkaToolService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

public class KafkaToolJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		KafkaToolService kafkaToolService = (KafkaToolService) context.getMergedJobDataMap().get("kafkaToolService");
		try {
			kafkaToolService.sendAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 JobKey jobKey = context.getJobDetail().getKey();
		 System.out.println("执行了"+jobKey);
	}
}