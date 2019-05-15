package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xJavaFxTool.utils.FtpUtil;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigFtp;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName: SenderFtpImpl
 * @Description: Ftp发送类
 * @author: xufeng
 * @date: 2018/6/13 16:06
 */
@Service("senderFtp")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderFtpImpl implements Sender {
    private SenderConfigFtp senderConfigFtp;
    private BlockingQueue<FtpUtil> ftpUtilPool = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        if (ftpUtilPool == null) {
            int connectionPoolSize = senderConfigFtp.getConnectionPoolSize() > 0 ? senderConfigFtp.getConnectionPoolSize() : 10;
            ftpUtilPool = new ArrayBlockingQueue<>(connectionPoolSize);
            for (int i = 0; i < connectionPoolSize; i++) {
                FtpUtil ftpUtil = new FtpUtil(senderConfigFtp.getHost(), senderConfigFtp.getPort(), senderConfigFtp.getUser(), senderConfigFtp.getPassword(), senderConfigFtp.getTimeout(), senderConfigFtp.isPassive(), senderConfigFtp.isBinary(), senderConfigFtp.getServersEncoding(), senderConfigFtp.getSocketTimeout(), senderConfigFtp.isLongConnection());
                ftpUtil.setFileType(senderConfigFtp.getFileType());
                ftpUtil.setBufferSize(senderConfigFtp.getBufferSize());
                if (senderConfigFtp.getConnectionType() == 0) {
                    ftpUtil.setFtps(false);
                } else if (senderConfigFtp.getConnectionType() == 1) {
                    ftpUtil.setFtps(true);
                    ftpUtil.setImplicit(true);
                } else if (senderConfigFtp.getConnectionType() == 2) {
                    ftpUtil.setFtps(true);
                    ftpUtil.setImplicit(false);
                    ftpUtil.setProtocol("SSL");
                } else if (senderConfigFtp.getConnectionType() == 3) {
                    ftpUtil.setFtps(true);
                    ftpUtil.setImplicit(false);
                    ftpUtil.setProtocol("TLS");
                }
                ftpUtil.setProt(senderConfigFtp.getProt());
                ftpUtil.setCheckServerValidity(senderConfigFtp.isCheckServerValidity());
                ftpUtilPool.put(ftpUtil);
            }
        }
        FtpUtil ftpUtil = ftpUtilPool.take();
        String path = StringUtils.appendIfMissing(senderConfigFtp.getPath(), "/", "/", "\\");
        path = ParseVariableCommon.parseVariable(path, msg, params);
        boolean createPathFlag = senderConfigFtp.isCreatePathFlag();
        boolean hasTmpPath = senderConfigFtp.isHasTmpPath();
        boolean overload = senderConfigFtp.isOverload();
        if (createPathFlag) {
            ftpUtil.changeStringDirectory(path);
        }
        String tmp = "";
        String postfixName = "";
        if (hasTmpPath) {
            tmp = StringUtils.appendIfMissing(senderConfigFtp.getTmpPath(), "/", "/", "\\");
            tmp = ParseVariableCommon.parseVariable(tmp, msg, params);
            if (StringUtils.isBlank(tmp)) {
                throw new Exception("configuration for tmp is missing.FtpSender:" + senderConfigFtp.toString());
            }
            if (createPathFlag) {
                ftpUtil.changeStringDirectory(tmp);
            }
        } else {
            postfixName = senderConfigFtp.getPostfixName();
            if (StringUtils.isEmpty(postfixName)) {
                postfixName = "." + msg.getId();
            }
        }
        String fileName = msg.getFileName();
        if (StringUtils.isNotBlank(senderConfigFtp.getFileName())) {
            fileName = ParseVariableCommon.parseVariable(senderConfigFtp.getFileName(), msg, params);
        }
        if (StringUtils.isBlank(fileName)) {
            throw new Exception("configuration for fileName is missing.FtpSender:" + senderConfigFtp.toString());
        }
        // connect to ftp server
        ftpUtil.checkAndConnect();
        if (!ftpUtil.getFtp().allocate(ArrayUtils.getLength(msg.getMessage()))) {
            throw new Exception(ftpUtil.getFtp().getReplyString());
        }
        // overload same filename in dest path
        if (overload) {
            if (tmp != null && !tmp.trim().equals("")) {
                ftpUtil.deleteFile(tmp + fileName);
            }
            ftpUtil.deleteFile(path + fileName);
        }
        String tmpFileName = fileName;
        log.debug("tmp:" + tmp + " path:" + path + " fileName:" + fileName + " tmpFileName:" + tmpFileName);
        byte[] msgByte = msg.getMessage();
        if (senderConfigFtp.getEncoding() != null && !"AUTO".equalsIgnoreCase(senderConfigFtp.getEncoding())) {
            msgByte = msg.getMessage(senderConfigFtp.getEncoding());
        }
        if (hasTmpPath) {
            if (!ftpUtil.uploadFile(tmp + tmpFileName, msgByte)) {
                throw new Exception(ftpUtil.getFtp().getReplyString() + "Error when send file to " + path + fileName + " directly.");
            } else {
                log.debug("Send file to tmp file " + tmp + fileName + " .");
                if (!ftpUtil.rename(tmp + tmpFileName, path + fileName)) {
                    throw new Exception(ftpUtil.getFtp().getReplyString() + "Error when rename tmp file " + tmp + tmpFileName + " to " + path + fileName + " .");
                }
                log.debug("Rename tmp file " + tmp + tmpFileName + " to " + path + fileName + " .");
            }
        } else {
            if (!ftpUtil.uploadFile(path + fileName, msgByte)) {
                throw new Exception(ftpUtil.getFtp().getReplyString() + "Error when send file to " + path + fileName + " directly.");
            }
            ftpUtil.checkAndThrow();
            log.info("direct to destniation,fileName is:" + fileName);
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_FTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigFtp.getPath());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("sent <" + msg.getId() + "> to " + path + " as " + fileName);

        ftpUtil.checkAndDisconnect();
        ftpUtilPool.put(ftpUtil);
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigFtp = (SenderConfigFtp) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (ftpUtilPool != null) {
            while (ftpUtilPool.size() > 0) {
                ftpUtilPool.take().destroy();
            }
            ftpUtilPool = null;
        }
    }
}
