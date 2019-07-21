package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xJavaFxTool.utils.FtpUtil;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigFtp;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ReceiverFtpImpl
 * @Description: Ftp接收器实现类
 * @author: xufeng
 * @date: 2018/4/9 11:26
 */

@Service("receiverFtp")
@Scope("prototype")
@Slf4j
public class ReceiverFtpImpl implements Receiver {
    private ReceiverConfigFtp receiverConfigFtp;
    private MessageHandler messageHandler;

    private int receivedFileSum = 0;
    private volatile BlockingQueue<FtpUtil> ftpUtilPool = new LinkedBlockingQueue<>();
    private volatile BlockingQueue<FtpUtil> ftpUtilPoolRunning = new LinkedBlockingQueue<>();
    private volatile int pollSize = 0;

    @Override
    public void receive(Map params) throws Exception {
        this.checkInit(params);
//        FtpUtil ftpUtil = ftpUtilPool.take();
        FtpUtil ftpUtil = ftpUtilPool.poll(10, TimeUnit.SECONDS);
        if (ftpUtil == null) {
            log.warn("ftpUtilPool is null:" + receiverConfigFtp.getId());
            return;
        }
        ftpUtilPoolRunning.put(ftpUtil);
        ftpUtil.checkAndConnect();
        receiveInternal(params, ftpUtil);
        ftpUtil.checkAndDisconnect();
        ftpUtilPoolRunning.remove(ftpUtil);
        ftpUtilPool.put(ftpUtil);
    }

    private void receiveInternal(Map params, FtpUtil ftpUtil) throws Exception {
        for (ReceiverConfigFtp.ReceiverConfigFtpRow receiverConfigFs : receiverConfigFtp.getReceiverConfigFsList()) {
            receiveFile(receiverConfigFs, params, ftpUtil);
        }
    }

    private void receiveFile(ReceiverConfigFtp.ReceiverConfigFtpRow receiverConfigFs, Map params, FtpUtil ftpUtil) throws Exception {
        boolean delReceiveFile = receiverConfigFs.isDelReceiveFile();
        String fileNameRegex = receiverConfigFs.getFileNameRegex();
        String remotePath = receiverConfigFs.getRemotePath();
        remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
        int max = receiverConfigFs.getMax();
        long maxSize = receiverConfigFs.getMaxSize();
        String bigFilePath = receiverConfigFs.getBigFilePath();
        bigFilePath = StringUtils.appendIfMissing(bigFilePath, "/", "/", "\\");
        String encoding = receiverConfigFs.getEncoding();
        String tmpPath = receiverConfigFtp.getTmpPath();
        tmpPath = StringUtils.appendIfMissing(tmpPath, "/", "/", "\\");
        boolean hasTmpPath = receiverConfigFtp.isHasTmpPath();
        String postfixName = null;
        boolean includeSubdirectory = receiverConfigFs.isIncludeSubdirectory();
        long delayTime = receiverConfigFs.getDelayTime();
        long minSize = receiverConfigFs.getMinSize();
        receiveAllFile(remotePath, tmpPath, hasTmpPath, fileNameRegex, encoding, delReceiveFile, max, maxSize, bigFilePath, postfixName, includeSubdirectory, delayTime, minSize, params, ftpUtil, 0);
    }

    private void receiveAllFile(String remotePath, String tmpPath, boolean hasTmpPath,
                                String fileNameRegex, String encoding, boolean delReceiveFile, int max,
                                long maxSize, String bigFilePath, String postfixName, boolean includeSubdirectory,
                                long delayTime, long minSize, Map params, FtpUtil ftpUtil, int receivedFileSum)
            throws Exception {
        // change the working directory
        ftpUtil.changeWorkingDirectory();
        if (!ftpUtil.changeWorkingDirectory(remotePath)) {
            log.error(ftpUtil.getFtp().getReplyString() + "Error when change working directory to " + remotePath);
            return;
        }
        // receive file
        FTPFile[] fileList = ftpUtil.getFtp().listFiles();
//        log.info("receiverFtp:" + receiverConfigFtp.getHost() + ":" + receiverConfigFtp.getPort() + " path:" + remotePath + " fileList.length:" + (fileList != null ? fileList.length : 0));
        Msg msgLogInfo = new Msg("EVENT.MSG.FILECOUNT", UuidUtil.get32UUID(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigFtp.getHost() + ":" + receiverConfigFtp.getPort() + ":" + remotePath);
//        msgLogInfo.put(LOGKEYS.MSG_TAG, remotePath);
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, fileList != null ? fileList.length : 0);
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FTP);
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigFtp.getId());
        MsgLogger.info(msgLogInfo.toMap());

        if (fileList == null) {
            return;
        }
        List<FTPFile> filePaths = new ArrayList<>();
        for (int i = 0; i < fileList.length && (max == -1 || receivedFileSum < max); i++) {
            FTPFile file = fileList[i];
            if (".".equals(file.getName()) || "..".equals(file.getName())) {
                continue;
            }
            //add filename filter
            if (StringUtils.isNotBlank(fileNameRegex)) {
                if (!file.getName().matches(fileNameRegex)) {
                    continue;
                }
            }
            if (file.isFile()) {
                if (maxSize != -1 && file.getSize() > maxSize) {
                    log.warn(file.getName() + ": size is exceed the limit [" + maxSize + "]. fileSize:" + file.getSize());
                    if (StringUtils.isNotEmpty(bigFilePath)) {
                        try {
                            ftpUtil.rename(file.getName(), bigFilePath + file.getName());
                            log.info("move big file to:" + bigFilePath + "/" + file.getName());
                        } catch (Exception e) {
                            log.error("move big file " + file.getName() + " error,", e);
                        }
                    }
                    continue;
                }
                if (delayTime > 0) {
                    if (System.currentTimeMillis() - file.getTimestamp().getTimeInMillis() < delayTime) {
                        log.warn("File is more new than currentTime-" + delayTime + "fileName:" + file.getName());
                        continue;
                    }
                }
                if (minSize != -1) {
                    if (file.getSize() < minSize) {
                        log.warn("File's Size is too small then skip it,file name is:" + file.getName());
                        continue;
                    }
                }
                boolean removeFlag = false;
                String fileName = file.getName();
                IMessage msg = new DefaultMessage();
                // use the tmpPath to be a temporary directory
                // or read the file directly is there is no tmpPath.
                if (hasTmpPath) {
                    if (tmpPath != null && !"".equals(tmpPath.trim())) {
                        if (ftpUtil.rename(file.getName(), tmpPath + fileName)) {
                            fileName = tmpPath + fileName;
                            removeFlag = true;
                            log.debug("use " + fileName + " as temp file.");
                        } else {
                            log.warn("rename file to temp directory error :" + ftpUtil.getFtp().getReplyString() + "Read file directly.");
                            continue;
                        }
                    }
                } else {
                    //when has no tmp directory then do
                    if (StringUtils.isBlank(postfixName)) {
                        postfixName = msg.getId();
                    }
                    fileName = fileName + "." + postfixName;
                    if (ftpUtil.rename(file.getName(), fileName)) {
                        removeFlag = true;
                        log.debug("use " + fileName + " as temp file.");
                    } else {
                        log.warn("rename file to temp directory error :" + ftpUtil.getFtp().getReplyString() + "Read file directly.");
                        continue;
                    }
                }
                try {
                    try {
                        byte[] messageByte = ftpUtil.downFile(fileName);
                        msg.setRawData(messageByte);
                    } catch (Exception e) {
                        if (e.getMessage().contains("No such file or directory")) {
                            log.warn("文件不存在：", fileName);
                            continue;
                        } else {
                            throw e;
                        }
                    }
                    if (encoding != null && !"AUTO".equals(encoding)) {
                        msg.setEncoding(encoding);
                    }
                    msg.setFileName(file.getName());
                    onMessage(msg, params);
                    try {
                        // delete the remote file.
                        if (delReceiveFile) {
                            if (!ftpUtil.deleteFile(fileName)) {
                                String replyString = ftpUtil.getFtp().getReplyString();
                                if (replyString.contains("No such file or directory")) {
                                    log.warn("deleteFile文件不存在：", fileName);
                                } else {
                                    throw new Exception("Deleting file occurs error. " + ftpUtil.getFtp().getReplyString());
                                }
                            }
                        } else {
                            //execute move file to dist directory
                            if (hasTmpPath && tmpPath != null && !"".equals(tmpPath.trim())) {
                                ftpUtil.rename(tmpPath + file.getName(), file.getName());
                            } else {
                                ftpUtil.rename(fileName, file.getName());
                            }
                        }
                    } catch (Exception e) {
                        removeFlag = false;
                        throw e;
                    }
                } catch (Exception e) {
                    log.error("receiverFtp异常:", e);
                    // rename the temp file to the remote path when error
                    if (removeFlag) {
                        if (hasTmpPath && tmpPath != null && !"".equals(tmpPath.trim())) {
                            ftpUtil.rename(tmpPath + file.getName(), file.getName());
                        } else {
                            ftpUtil.rename(fileName, file.getName());
                        }
                    }
                    continue;
                }
                receivedFileSum++;
            } else {
                if (includeSubdirectory && (max == -1 || receivedFileSum < max)) {
                    filePaths.add(file);
                }
            }
        }
        //when is directory then do
        if (includeSubdirectory && (max == -1 || receivedFileSum < max)) {
            for (Iterator<FTPFile> it = filePaths.iterator(); it.hasNext(); ) {
                String curRemotePath = remotePath + it.next().getName() + "/";
                receiveAllFile(curRemotePath, tmpPath, hasTmpPath, fileNameRegex, encoding, delReceiveFile, max, maxSize, bigFilePath, postfixName, includeSubdirectory, delayTime, minSize, params, ftpUtil, receivedFileSum);
            }
        }
    }

    private void onMessage(IMessage msg, Map params) throws Exception {
        IContext ctx = new DefaultContext();
        ctx.setMessage(msg);

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigFtp.getHost() + ":" + receiverConfigFtp.getPort());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FTP);
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigFtp.getId());
        MsgLogger.info(msgLogInfo.toMap());

        messageHandler.handle(ctx);
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigFtp = (ReceiverConfigFtp) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        try {
            pollSize = 0;
            for (FtpUtil ftpUtil = ftpUtilPool.poll(); ftpUtil != null; ftpUtil = ftpUtilPool.poll()) {
                ftpUtil.destroy();
            }
            for (FtpUtil ftpUtil = ftpUtilPoolRunning.poll(); ftpUtil != null; ftpUtil = ftpUtilPoolRunning.poll()) {
                ftpUtil.destroy();
            }
        } catch (Exception e) {
            log.error("receiverFtp destroy fail", e);
        }
    }

    private synchronized void checkInit(Map params) throws Exception {
        if (pollSize < receiverConfigFtp.getConnectionPoolSize() && ftpUtilPool.size() <= 0) {
            FtpUtil ftpUtil = new FtpUtil(receiverConfigFtp.getHost(), receiverConfigFtp.getPort(), receiverConfigFtp.getUser(), receiverConfigFtp.getPassword(), receiverConfigFtp.getTimeout(), receiverConfigFtp.isPassive(), receiverConfigFtp.isBinary(), receiverConfigFtp.getServersEncoding(), receiverConfigFtp.getSocketTimeout(), receiverConfigFtp.isLongConnection());
            ftpUtil.setFileType(receiverConfigFtp.getFileType());
            ftpUtil.setBufferSize(receiverConfigFtp.getBufferSize());
            if (receiverConfigFtp.getConnectionType() == 0) {
                ftpUtil.setFtps(false);
            } else if (receiverConfigFtp.getConnectionType() == 1) {
                ftpUtil.setFtps(true);
                ftpUtil.setImplicit(true);
            } else if (receiverConfigFtp.getConnectionType() == 2) {
                ftpUtil.setFtps(true);
                ftpUtil.setImplicit(false);
                ftpUtil.setProtocol("SSL");
            } else if (receiverConfigFtp.getConnectionType() == 3) {
                ftpUtil.setFtps(true);
                ftpUtil.setImplicit(false);
                ftpUtil.setProtocol("TLS");
            }
            ftpUtil.setProt(receiverConfigFtp.getProt());
            ftpUtil.setCheckServerValidity(receiverConfigFtp.isCheckServerValidity());
            ftpUtilPool.put(ftpUtil);
            pollSize++;
        }
    }

}
