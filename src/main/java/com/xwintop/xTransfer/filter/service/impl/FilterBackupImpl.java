package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigBackup;
import com.xwintop.xTransfer.filter.enums.StrategyEnum;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.util.Common;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: FilterBackupImpl
 * @Description: 备份操作接口
 * @author: xufeng
 * @date: 2018/6/13 16:15
 */

@Service("filterBackup")
@Scope("prototype")
@Data
@Slf4j
public class FilterBackupImpl implements Filter {
    private FilterConfigBackup filterConfigBackup;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigBackup.getFileNameFilterRegex())) {
                String fileNameFilterRegexGroup = filterConfigBackup.getFileNameFilterRegexGroup();
                if (StringUtils.isEmpty(fileNameFilterRegexGroup)) {
                    fileNameFilterRegexGroup = "defaultRegexGroup";
                }
                if ("?!".equals(filterConfigBackup.getFileNameFilterRegex())) {
                    if (iMessage.checkFileNameFilterRegexGroup(fileNameFilterRegexGroup)) {
                        log.info("Filter:" + filterConfigBackup.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                } else {
                    if (!iMessage.getFileName().matches(filterConfigBackup.getFileNameFilterRegex())) {
                        log.info("Filter:" + filterConfigBackup.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                    iMessage.addFileNameFilterRegexGroup(fileNameFilterRegexGroup);
                }
            }
            doFilter(iMessage, params);
        }
    }

    public void doFilter(IMessage msg, Map params) throws Exception {
//        log.info("开始文件备份操作");
        String path = filterConfigBackup.getPath();
        String tmp = filterConfigBackup.getTmpPath();
        boolean hasTmpPath = StringUtils.isNotEmpty(tmp);
        String fileName = msg.getFileName();
        if (StringUtils.isNotBlank(filterConfigBackup.getFileName())) {
            fileName = ParseVariableCommon.parseVariable(filterConfigBackup.getFileName(), msg, params);
        }
        if (path == null || path.trim().length() <= 0) {
            throw new Exception("configuration for path is missing.MessageSender:" + filterConfigBackup.toString());
        }
        if (fileName == null || fileName.trim().length() <= 0) {
            throw new Exception("configuration for fileName is missing.MessageSender:" + filterConfigBackup.toString());
        }
        path = StringUtils.appendIfMissing(path, "/", "/", "\\");
        tmp = StringUtils.appendIfMissing(tmp, "/", "/", "\\");
        File filePath = new File(path);
        Common.checkIsHaveDir(filePath, filterConfigBackup.isCreatePathFlag());
        switch (StrategyEnum.valueOf(filterConfigBackup.getStrategy())) {
            case day:
                filePath = Common.dirByDay(filePath);
                break;
            case hour:
                filePath = Common.dirByHour(filePath);
                break;
            case day_hour:
                filePath = Common.dirByDay_Hour(filePath);
                break;
            case minutes:
                filePath = Common.dirByMinutes(filePath);
                break;
            default:
                break;
        }
        File tmpPath = null;
        File tmpFile;
        if (hasTmpPath) {
            tmpPath = new File(tmp);
            if (!tmpPath.exists()) {
                log.warn("backup临时目录不存在:" + tmp);
                tmpPath = filePath;
                hasTmpPath = false;
            }
        } else {
            tmpPath = filePath;
        }
        if (hasTmpPath) {
            tmpFile = new File(tmpPath, fileName);
        } else {
            tmpFile = new File(tmpPath, fileName + "." + msg.getId());
        }
        FileUtils.writeByteArrayToFile(tmpFile, msg.getMessage());
        File backupFile;
        if (filterConfigBackup.isOverload()) {
            backupFile = new File(filePath, fileName);
            FileUtils.deleteQuietly(backupFile);
        } else {
            //查找是否有重复文件；同一文件名，加后缀（.YYYYMMDDHHSSSS）
            backupFile = Common.getPathByCheckFileName(new File(filePath, fileName));
        }
        try {
            FileUtils.moveFile(tmpFile, backupFile);
        } catch (Exception e) {
            log.error("error when rename file " + tmpFile.getPath() + " to " + filePath.getPath());
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, filterConfigBackup.getPath());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("backup <" + msg.getId() + "> to " + filePath + " as " + fileName);
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigBackup = (FilterConfigBackup) filterConfig;
    }

    @Override
    public void destroy() {

    }
}
