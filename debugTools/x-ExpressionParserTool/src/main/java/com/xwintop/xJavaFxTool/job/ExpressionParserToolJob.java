package com.xwintop.xJavaFxTool.job;

import com.xwintop.xJavaFxTool.services.debugTools.ExpressionParserToolService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @ClassName: ExpressionParserToolJob
 * @Description: 表达式解析器调试工具Job
 * @author: xufeng
 * @date: 2021/9/12 22:52
 */

public class ExpressionParserToolJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ExpressionParserToolService expressionParserToolService = (ExpressionParserToolService) context.getMergedJobDataMap().get("Service");
		try {
			expressionParserToolService.runAllAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JobKey jobKey = context.getJobDetail().getKey();
		System.out.println("执行了"+jobKey);
	}
}
