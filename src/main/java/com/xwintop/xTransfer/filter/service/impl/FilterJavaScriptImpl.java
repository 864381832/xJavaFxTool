package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xJavaFxTool.utils.SpringUtil;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigJavaScript;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @ClassName: FilterJavaScriptImpl
 * @Description: 执行JavaScript脚本实现类
 * @author: xufeng
 * @date: 2019/5/13 0013 23:36
 */

@Service("filterJavaScript")
@Scope("prototype")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FilterJavaScriptImpl implements Filter {
    private FilterConfigJavaScript filterConfigJavaScript;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigJavaScript.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(filterConfigJavaScript.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigJavaScript.getId() + "跳过fileName：" + iMessage.getFileName());
                    continue;
                }
            }
            doFilter(iMessage, params);
        }
    }

    public void doFilter(IMessage msg, Map params) throws Exception {
        Bindings bindings = new SimpleBindings();
        bindings.put("message", msg);
        bindings.put("params", params);
        bindings.put("applicationContext", SpringUtil.getApplicationContext());
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

        if (StringUtils.isNotEmpty(filterConfigJavaScript.getScriptString())) {
            engine.eval(filterConfigJavaScript.getScriptString(), bindings);
        }
        if (StringUtils.isNotEmpty(filterConfigJavaScript.getScriptFilePath())) {
            String script = new String(Files.readAllBytes(Paths.get(filterConfigJavaScript.getScriptFilePath())));
            engine.eval(script, bindings);
        }
        log.info("filterJavaScript success! msgId:" + msg.getId());
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
        this.filterConfigJavaScript = (FilterConfigJavaScript) filterConfig;
    }

    @Override
    public void destroy() {
    }
}
