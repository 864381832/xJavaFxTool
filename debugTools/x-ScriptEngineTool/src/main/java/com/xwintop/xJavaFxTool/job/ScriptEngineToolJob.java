package com.xwintop.xJavaFxTool.job;

import com.xwintop.xJavaFxTool.services.debugTools.ScriptEngineToolService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @ClassName: ScriptEngineToolJob
 * @Description: 脚本引擎调试工具
 * @author: xufeng
 * @date: 2018/1/28 1:04
 */

public class ScriptEngineToolJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScriptEngineToolService scriptEngineToolService = (ScriptEngineToolService) context.getMergedJobDataMap().get("Service");
		try {
			scriptEngineToolService.runAllAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JobKey jobKey = context.getJobDetail().getKey();
		System.out.println("执行了"+jobKey);
	}
}
