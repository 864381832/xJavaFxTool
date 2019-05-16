package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xJavaFxTool.utils.SpringUtil;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigGroovyScript;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @ClassName: FilterGroovyScriptImpl
 * @Description: 执行Groovy脚本实现类
 * @author: xufeng
 * @date: 2019/5/13 0013 23:36
 */


@Service("filterGroovyScript")
@Scope("prototype")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FilterGroovyScriptImpl implements Filter {
    private FilterConfigGroovyScript filterConfigGroovyScript;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigGroovyScript.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(filterConfigGroovyScript.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigGroovyScript.getId() + "跳过fileName：" + iMessage.getFileName());
                    continue;
                }
            }
            doFilter(iMessage, params);
        }
    }

    public void doFilter(IMessage msg, Map params) throws Exception {
        Binding binding = new Binding();
        binding.setVariable("message", msg);
        binding.setVariable("params", params);
        binding.setVariable("applicationContext", SpringUtil.getApplicationContext());
        GroovyShell shell = new GroovyShell(binding);

        if (StringUtils.isNotEmpty(filterConfigGroovyScript.getScriptString())) {
            Object value = shell.evaluate(filterConfigGroovyScript.getScriptString());
        }
        if (StringUtils.isNotEmpty(filterConfigGroovyScript.getScriptFilePath())) {
            String script = new String(Files.readAllBytes(Paths.get(filterConfigGroovyScript.getScriptFilePath())));
            shell.evaluate(script);
        }
        log.info("filterGroovyScript success! msgId:" + msg.getId());
        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_FILTER, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigGroovyScript = (FilterConfigGroovyScript) filterConfig;
    }

    @Override
    public void destroy() {
    }
}
