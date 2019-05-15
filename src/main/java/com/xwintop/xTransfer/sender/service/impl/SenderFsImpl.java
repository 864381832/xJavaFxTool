package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigFs;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.util.Common;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * @ClassName: SenderFsImpl
 * @Description: Fs发送类
 * @author: xufeng
 * @date: 2018/6/13 16:06
 */

@Service("senderFs")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderFsImpl implements Sender {
    private SenderConfigFs senderConfigFs;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderFs,taskName:" + params.get(TaskQuartzJob.JOBID));
        // parse the variable of send properties
        String path = senderConfigFs.getPath();
        path = StringUtils.appendIfMissing(path, "/", "/", "\\");
        boolean hasTmpPath = senderConfigFs.isHasTmpPath();
        boolean overload = senderConfigFs.isOverload();
        String tmp = "";
        String postfixName = "";
        if (hasTmpPath) {
            tmp = senderConfigFs.getTmp();
            tmp = StringUtils.appendIfMissing(tmp, "/", "/", "\\");
        } else {
            postfixName = senderConfigFs.getPostfixName();
        }
        String fileName = msg.getFileName();
        if (StringUtils.isNotBlank(senderConfigFs.getFileName())) {
            fileName = ParseVariableCommon.parseVariable(senderConfigFs.getFileName(), msg, params);
        }
        if (path == null || path.trim().length() <= 0) {
            throw new Exception("configuration for path is missing.MessageSender:" + senderConfigFs.toString());
        }
        if (fileName == null || fileName.trim().length() <= 0) {
            throw new Exception("configuration for fileName is missing.MessageSender:" + senderConfigFs.toString());
        }
        File filePath = new File(path);
        Common.checkIsHaveDir(filePath, senderConfigFs.isCreatePathFlag());
        File tmpPath = null;
        // if tmp is not specified,do not use temporary directory
        if (hasTmpPath && StringUtils.isNotEmpty(tmp)) {
            tmpPath = new File(tmp);
            if (!tmpPath.exists()) {
                log.warn("senderFs临时目录不存在:" + tmp);
                tmpPath = filePath;
                hasTmpPath = false;
            }
        } else {
            tmpPath = filePath;
        }
        //for overload same filename in dest path.
        if (overload) {
            File tmpFile = new File(tmpPath, fileName);
            FileUtils.deleteQuietly(tmpFile);
            File chkFile = new File(filePath, fileName);
            FileUtils.deleteQuietly(chkFile);
        }

        File tmpFile = null;
        if (hasTmpPath && StringUtils.isNotEmpty(tmp)) {
            tmpFile = new File(tmpPath, fileName);
        } else {
            if (postfixName == null || postfixName.trim().equals("")) {
                postfixName = "." + msg.getId();
            }
            tmpFile = new File(tmpPath, fileName + postfixName);
        }
        FileUtils.writeByteArrayToFile(tmpFile, msg.getMessage());
        try {
            FileUtils.moveFile(tmpFile, new File(filePath, fileName));
        } catch (Exception e) {
            log.error("error when rename file " + tmpFile.getPath() + " to " + filePath.getPath());
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigFs.getPath());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("sent <" + msg.getId() + "> to " + filePath + " as " + fileName);
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigFs = (SenderConfigFs) senderConfig;
    }

    @Override
    public void destroy() throws Exception {

    }
}
