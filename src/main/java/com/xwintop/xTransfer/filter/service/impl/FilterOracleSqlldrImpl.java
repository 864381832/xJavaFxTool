package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigOracleSqlldr;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.util.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName: FilterOracleSqlldrImpl
 * @Description: OracleSqlldr入库实现类
 * @author: xufeng
 * @date: 2019/2/14 15:46
 */

@Service("filterOracleSqlldr")
@Scope("prototype")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FilterOracleSqlldrImpl implements Filter {
    private FilterConfigOracleSqlldr filterConfigOracleSqlldr;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigOracleSqlldr.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(filterConfigOracleSqlldr.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigOracleSqlldr.getId() + "跳过fileName：" + iMessage.getFileName());
                    continue;
                }
            }
            doFilter(iMessage, params);
        }
    }

    public void doFilter(IMessage msg, Map params) throws Exception {
        String tmpFilePath = Common.addSep(filterConfigOracleSqlldr.getTmpFilePath());
        String badPath = Common.addSep(filterConfigOracleSqlldr.getBadPath());
        String logPath = Common.addSep(filterConfigOracleSqlldr.getLogPath());
        Common.checkIsHaveDir(new File(tmpFilePath), true);
        Common.checkIsHaveDir(new File(badPath), true);
        Common.checkIsHaveDir(new File(logPath), true);
        FileUtils.writeByteArrayToFile(new File(tmpFilePath + msg.getFileName()), msg.getMessage());
        String fileTimer = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String exeComm = "sqlldr userid=" + filterConfigOracleSqlldr.getUsername() + "/" + filterConfigOracleSqlldr.getPassword() + "@" + filterConfigOracleSqlldr.getDbServiceName()
                + " control=" + tmpFilePath + msg.getFileName()
                + " bad=" + badPath + msg.getFileName() + fileTimer
                + " log=" + logPath + msg.getFileName() + fileTimer;
        log.debug("exeComm:" + exeComm);
        try {
            Runtime rt = Runtime.getRuntime();
            Process myProcess = null;

            myProcess = rt.exec(exeComm);

            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(myProcess.getErrorStream()));

            int j;
            char[] chars = new char[512];
            String strResult1 = "";
            while ((j = bufferedReader1.read(chars, 0, 512)) != -1) {
                strResult1 = new String(chars, 0, j);
                log.debug("strResult1:" + strResult1);
            }

            String strResult2 = "";

            while ((j = bufferedReader2.read(chars, 0, 512)) != -1) {
                strResult2 = new String(chars, 0, j);
                log.debug("doSqlLoader,strResult2:" + strResult2);
            }
            myProcess.waitFor();
            log.debug("success ! log is " + filterConfigOracleSqlldr.getLogPath() + msg.getFileName() + fileTimer);
        } catch (Exception e) {
            log.warn("SqlLoader throw error---" + e.toString());
        } finally {
            FileUtils.deleteQuietly(new File(tmpFilePath + msg.getFileName()));
        }

//        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
//        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
//        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
//        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, "MSGTODB");
//        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, msg.getFileName());
//        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
//        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
//        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
//        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
//        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
//        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
//        MsgLogger.info(msgLogInfo.toMap());
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigOracleSqlldr = (FilterConfigOracleSqlldr) filterConfig;
    }

    @Override
    public void destroy() {
    }
}
