package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigSftp;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @ClassName: ReceiverSftpImpl
 * @Description: Sftp接收器实现类
 * @author: xufeng
 * @date: 2019/2/14 10:55
 */

@Service("receiverSftp")
@Scope("prototype")
@Slf4j
public class ReceiverSftpImpl implements Receiver {
    private ReceiverConfigSftp receiverConfigSftp;
    private MessageHandler messageHandler;

    private int receivedFileSum = 0;

    private Session session = null;
    private ChannelSftp channel = null;

    @Override
    public void receive(Map params) throws Exception {
        if (session == null) {
            JSch jSch = new JSch(); //创建JSch对象
            session = jSch.getSession(receiverConfigSftp.getUsername(), receiverConfigSftp.getHost(), receiverConfigSftp.getPort());//根据用户名，主机ip和端口获取一个Session对象
            session.setPassword(receiverConfigSftp.getPassword());//设置密码
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);//为Session对象设置properties
            session.setTimeout(receiverConfigSftp.getTimeout());//设置超时
            session.connect();//通过Session建立连接
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        }
        receiveInternal(params);
    }

    private void receiveInternal(Map params) throws Exception {
        for (ReceiverConfigSftp.ReceiverConfigSftpRow receiverConfigFs : receiverConfigSftp.getReceiverConfigFsList()) {
            receiveFile(receiverConfigFs, params);
        }
    }

    private void receiveFile(ReceiverConfigSftp.ReceiverConfigSftpRow receiverConfigFs, Map params) throws Exception {
        boolean delReceiveFile = receiverConfigFs.isDelReceiveFile();
        String fileNameRegex = receiverConfigFs.getFileNameRegex();
        String remotePath = receiverConfigFs.getRemotePath();
        int max = receiverConfigFs.getMax();
        long maxSize = receiverConfigFs.getMaxSize();
        String encoding = receiverConfigFs.getEncoding();
        String tmpPath = receiverConfigSftp.getTmpPath();
        boolean hasTmpPath = receiverConfigSftp.isHasTmpPath();
        String postfixName = receiverConfigFs.getFileNameRegex();
        boolean includeSubdirectory = receiverConfigFs.isIncludeSubdirectory();
        long delayTime = receiverConfigFs.getDelayTime();
        long minSize = receiverConfigFs.getMinSize();
        receivedFileSum = 0;
        remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
        tmpPath = StringUtils.appendIfMissing(tmpPath, "/", "/", "\\");
        receiveAllFile(remotePath, tmpPath, hasTmpPath, fileNameRegex, encoding, delReceiveFile, max, maxSize, postfixName, includeSubdirectory, delayTime, minSize, params);
    }

    private void receiveAllFile(String remotePath, String tmpPath, boolean hasTmpPath,
                                String fileNameRegex, String encoding, boolean delReceiveFile, int max,
                                long maxSize, String postfixName, boolean includeSubdirectory,
                                long delayTime, long minSize, Map params)
            throws Exception {
        Vector<LsEntry> fileList = channel.ls(remotePath);
        if (fileList == null) {
            log.error("Error when change working directory to " + remotePath);
            return;
        }
        // receive file
        log.debug("fileList.length:" + fileList.size());
        List<LsEntry> filePaths = new ArrayList<>();
        for (int i = 0; i < fileList.size() && max - receivedFileSum > 0; i++) {
            LsEntry file = fileList.get(i);
            //add filename filter
            if (StringUtils.isNotBlank(fileNameRegex)) {
                if (!file.getFilename().matches(fileNameRegex)) {
                    continue;
                }
            }
            if (".".equals(file.getFilename()) || "..".equals(file.getFilename())) {
                continue;
            }
            if (!file.getAttrs().isDir()) {
                if (delayTime > 0) {
                    if (System.currentTimeMillis() - file.getAttrs().getMTime() < delayTime) {
                        log.warn("File is more new than currentTime-" + delayTime);
                        continue;
                    }
                }
                if (minSize != -1) {
                    if (file.getAttrs().getSize() < minSize) {
                        log.warn("File's Size is too small then skip it,file name is:" + file.getFilename());
                        continue;
                    }
                }
                boolean removeFlag = false;
                String filePathAndName = remotePath + file.getFilename();
                IMessage msg = new DefaultMessage();
                // use the tmpPath to be a temporary directory
                // or read the file directly is there is no tmpPath.
                if (hasTmpPath && StringUtils.isNotBlank(tmpPath)) {
                    try {
                        channel.rename(filePathAndName, tmpPath + file.getFilename());
                        filePathAndName = tmpPath + file.getFilename();
                        removeFlag = true;
                        log.debug("use " + filePathAndName + " as temp file.");
                    } catch (SftpException s) {
                        log.error("rename file to temp directory error :", s);
                    }
                }
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    channel.get(filePathAndName, outputStream);
                    byte[] messageByte = outputStream.toByteArray();
                    if (messageByte != null && messageByte.length > 0) {
                        msg.setRawData(messageByte);
                        if (encoding != null && !"AUTO".equals(encoding)) {
                            msg.setEncoding(encoding);
                        }
                        msg.setFileName(file.getFilename());
                        onMessage(msg, params);
                        try {
                            // delete the remote file.
                            if (delReceiveFile) {
                                channel.rm(filePathAndName);
                            } else {
                                //execute move file to dist directory
                                if (hasTmpPath && StringUtils.isNotBlank(tmpPath)) {
                                    channel.rename(filePathAndName, remotePath + file.getFilename());
                                } else {
                                    channel.rename(filePathAndName, remotePath + file.getFilename());
                                }
                            }
                        } catch (Exception e) {
                            removeFlag = false;
                            throw e;
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    // rename the temp file to the remote path when error
                    if (removeFlag) {
                        if (hasTmpPath && StringUtils.isNotBlank(tmpPath)) {
                            channel.rename(filePathAndName, remotePath + file.getFilename());
                        } else {
                            channel.rename(filePathAndName, remotePath + file.getFilename());
                        }
                    }
                    continue;
                }
                receivedFileSum++;
            } else {
                if (includeSubdirectory && receivedFileSum < max) {
                    filePaths.add(file);
                }
            }
        }
        //when is directory then do
        if (includeSubdirectory && receivedFileSum < max) {
            for (Iterator<LsEntry> it = filePaths.iterator(); it.hasNext(); ) {
                String curRemotePath = remotePath + "/" + it.next().getFilename();
                receiveAllFile(curRemotePath, tmpPath, hasTmpPath, fileNameRegex, encoding, delReceiveFile, max, maxSize, postfixName, includeSubdirectory, delayTime, minSize, params);
            }
        }
    }

    private void onMessage(IMessage msg, Map params) throws Exception {
        IContext ctx = new DefaultContext();
        ctx.setMessage(msg);

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_SFTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigSftp.getHost() + ":" + receiverConfigSftp.getPort());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_SFTP);
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigSftp.getId());
        MsgLogger.info(msgLogInfo.toMap());

        messageHandler.handle(ctx);
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigSftp = (ReceiverConfigSftp) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        if (channel != null) {
            channel.quit();
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

}
