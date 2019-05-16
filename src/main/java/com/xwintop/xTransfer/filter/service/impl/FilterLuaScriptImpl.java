package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xJavaFxTool.utils.SpringUtil;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigLuaScript;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @ClassName: FilterLuaScriptImpl
 * @Description: 执行Lua脚本实现类
 * @author: xufeng
 * @date: 2019/5/13 0013 23:36
 */

@Service("filterLuaScript")
@Scope("prototype")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FilterLuaScriptImpl implements Filter {
    private FilterConfigLuaScript filterConfigLuaScript;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigLuaScript.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(filterConfigLuaScript.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigLuaScript.getId() + "跳过fileName：" + iMessage.getFileName());
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
        if (StringUtils.isNotEmpty(filterConfigLuaScript.getScriptString())) {
            Globals globals = JsePlatform.standardGlobals();
            LuaValue chunk = globals.load(filterConfigLuaScript.getScriptString());
            chunk.call();
        }
        if (StringUtils.isNotEmpty(filterConfigLuaScript.getScriptFilePath())) {
            String script = new String(Files.readAllBytes(Paths.get(filterConfigLuaScript.getScriptFilePath())));
            Globals globals = JsePlatform.standardGlobals();
            LuaValue chunk = globals.load(script);
            chunk.call();
        }
        log.info("filterLuaScript success! msgId:" + msg.getId());
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
        this.filterConfigLuaScript = (FilterConfigLuaScript) filterConfig;
    }

    @Override
    public void destroy() {
    }
}
