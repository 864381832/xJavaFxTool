package com.xwintop.xJavaFxTool.utils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ActionJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Runnable runnable = (Runnable) context.getMergedJobDataMap().get("runnable");
        runnable.run();
    }
}
