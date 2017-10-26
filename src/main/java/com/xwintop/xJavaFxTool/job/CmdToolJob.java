package com.xwintop.xJavaFxTool.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import com.xwintop.xJavaFxTool.services.debugTools.CmdToolService;

/** 
 * @ClassName: CmdToolJob 
 * @Description: Cmd工具类Job
 * @author: xufeng
 * @date: 2017年10月26日 下午1:53:46  
 */
public class CmdToolJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		CmdToolService cmdToolService = (CmdToolService) context.getMergedJobDataMap().get("cmdToolService");
		try {
			cmdToolService.runAllAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JobKey jobKey = context.getJobDetail().getKey();
		System.out.println("执行了"+jobKey);
	}
}
