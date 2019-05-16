package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xJavaFxTool.utils.SpringUtil;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigPythonScript;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.python.util.PythonInterpreter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @ClassName: FilterPythonScriptImpl
 * @Description: 执行Python脚本实现类
 * @author: xufeng
 * @date: 2019/5/13 0013 23:36
 */

@Service("filterPythonScript")
@Scope("prototype")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FilterPythonScriptImpl implements Filter {
    private FilterConfigPythonScript filterConfigPythonScript;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigPythonScript.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(filterConfigPythonScript.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigPythonScript.getId() + "跳过fileName：" + iMessage.getFileName());
                    continue;
                }
            }
            doFilter(iMessage, params);
        }
    }

    public void doFilter(IMessage msg, Map params) throws Exception {
        PythonInterpreter jy = new PythonInterpreter();
        jy.set("message", msg);
        jy.set("params", params);
        jy.set("applicationContext", SpringUtil.getApplicationContext());
        if (StringUtils.isNotEmpty(filterConfigPythonScript.getScriptString())) {
            jy.eval(filterConfigPythonScript.getScriptString());
        }
        if (StringUtils.isNotEmpty(filterConfigPythonScript.getScriptFilePath())) {
            String script = new String(Files.readAllBytes(Paths.get(filterConfigPythonScript.getScriptFilePath())));
            jy.eval(script);
        }
        log.info("filterPythonScript success! msgId:" + msg.getId());
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
        this.filterConfigPythonScript = (FilterConfigPythonScript) filterConfig;
    }

    @Override
    public void destroy() {
    }
}
