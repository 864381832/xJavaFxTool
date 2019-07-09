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
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    private volatile BlockingQueue<ChannelSftp> channelPool = new LinkedBlockingQueue<>();
    private volatile BlockingQueue<ChannelSftp> channelPoolRunning = new LinkedBlockingQueue<>();
    private volatile int pollSize = 0;

    @Override
    public void receive(Map params) throws Exception {
        ChannelSftp channel;
        if (receiverConfigSftp.isLongConnection()) {
            synchronized (this) {
                if (pollSize < receiverConfigSftp.getConnectionPoolSize() && channelPool.size() <= 0) {
                    channelPool.put(createChannelSftp());
                    pollSize++;
                }
            }
//            channel = channelPool.take();
            channel = channelPool.poll(10, TimeUnit.SECONDS);
            if (channel == null) {
                log.warn("channelPool is null:" + receiverConfigSftp.getId());
                return;
            }
            if (!channel.isConnected()) {
                channel.getSession().disconnect();
                channel = createChannelSftp();
            }
        } else {
            channel = createChannelSftp();
        }
        channelPoolRunning.put(channel);
        receiveInternal(params, channel);
    }

    private void receiveInternal(Map params, ChannelSftp channel) throws Exception {
        for (ReceiverConfigSftp.ReceiverConfigSftpRow receiverConfigFs : receiverConfigSftp.getReceiverConfigFsList()) {
            receiveFile(receiverConfigFs, params, channel);
        }
        channelPoolRunning.remove(channel);
        if (receiverConfigSftp.isLongConnection()) {
            channelPool.put(channel);
        } else {
            channel.getSession().disconnect();
            channel.disconnect();
        }
    }

    private void receiveFile(ReceiverConfigSftp.ReceiverConfigSftpRow receiverConfigFs, Map params, ChannelSftp channel) throws Exception {
        boolean delReceiveFile = receiverConfigFs.isDelReceiveFile();
        String fileNameRegex = receiverConfigFs.getFileNameRegex();
        String remotePath = receiverConfigFs.getRemotePath();
        int max = receiverConfigFs.getMax();
        long maxSize = receiverConfigFs.getMaxSize();
        String bigFilePath = receiverConfigFs.getBigFilePath();
        bigFilePath = StringUtils.appendIfMissing(bigFilePath, "/", "/", "\\");
        String encoding = receiverConfigFs.getEncoding();
        String tmpPath = receiverConfigSftp.getTmpPath();
        boolean hasTmpPath = receiverConfigSftp.isHasTmpPath();
        String postfixName = receiverConfigFs.getFileNameRegex();
        boolean includeSubdirectory = receiverConfigFs.isIncludeSubdirectory();
        long delayTime = receiverConfigFs.getDelayTime();
        long minSize = receiverConfigFs.getMinSize();
        remotePath = StringUtils.appendIfMissing(remotePath, "/", "/", "\\");
        tmpPath = StringUtils.appendIfMissing(tmpPath, "/", "/", "\\");
        receiveAllFile(remotePath, tmpPath, hasTmpPath, fileNameRegex, encoding, delReceiveFile, max, maxSize, bigFilePath, postfixName, includeSubdirectory, delayTime, minSize, params, channel, 0);
    }

    private void receiveAllFile(String remotePath, String tmpPath, boolean hasTmpPath,
                                String fileNameRegex, String encoding, boolean delReceiveFile, int max,
                                long maxSize, String bigFilePath, String postfixName, boolean includeSubdirectory,
                                long delayTime, long minSize, Map params, ChannelSftp channel, int receivedFileSum)
            throws Exception {
        Vector<LsEntry> fileList = channel.ls(remotePath);
        if (fileList == null) {
            log.warn("fileList is null,path:" + remotePath);
            return;
        }
        // receive file
//        log.info("receiverSftp:" + receiverConfigSftp.getHost() + ":" + receiverConfigSftp.getPort() + " path:" + remotePath + " fileList.length:" + fileList.size());
        Msg msgLogInfo = new Msg("EVENT.MSG.FILECOUNT", UuidUtil.get32UUID(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_SFTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigSftp.getHost() + ":" + receiverConfigSftp.getPort() + ":" + remotePath);
//        msgLogInfo.put(LOGKEYS.MSG_TAG, remotePath);
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, fileList.size());
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_SFTP);
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigSftp.getId());
        MsgLogger.info(msgLogInfo.toMap());

        List<LsEntry> filePaths = new ArrayList<>();
        for (int i = 0; i < fileList.size() && (max == -1 || receivedFileSum < max); i++) {
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
                String filePathAndName = remotePath + file.getFilename();
                channel = this.checkAndConnect(channel);
                if (maxSize != -1 && file.getAttrs().getSize() > maxSize) {
                    log.warn(file.getFilename() + ": size is exceed the limit [" + maxSize + "]. fileSize:" + file.getAttrs().getSize());
                    if (StringUtils.isNotEmpty(bigFilePath)) {
                        try {
                            channel.rename(remotePath + file.getFilename(), bigFilePath + file.getFilename());
                            log.info("move big file to:" + bigFilePath + file.getFilename());
                        } catch (Exception e) {
                            log.error("move big file " + file.getFilename() + " error,", e);
                        }
                    }
                    continue;
                }
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
                IMessage msg = new DefaultMessage();
                // use the tmpPath to be a temporary directory or read the file directly is there is no tmpPath.
                if (hasTmpPath && StringUtils.isNotBlank(tmpPath)) {
                    try {
                        channel.rename(filePathAndName, tmpPath + file.getFilename());
                        filePathAndName = tmpPath + file.getFilename();
                        removeFlag = true;
                        log.debug("use " + filePathAndName + " as temp file.");
                    } catch (SftpException s) {
                        log.warn("rename file " + file.getFilename() + " to temp directory error :" + s.getMessage());
                        continue;
                    }
                }
                try {
                    channel = this.checkAndConnect(channel);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        channel.get(filePathAndName, outputStream);
                    } catch (SftpException s) {
                        if (s.getMessage().contains("No such file")) {
                            log.warn("文件不存在：" + filePathAndName);
                            continue;
                        } else {
                            throw s;
                        }
                    }
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
                    log.error("receiverSftp接收失败：", e);
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
                if (includeSubdirectory && (max == -1 || receivedFileSum < max)) {
                    filePaths.add(file);
                }
            }
        }
        //when is directory then do
        if (includeSubdirectory && (max == -1 || receivedFileSum < max)) {
            for (Iterator<LsEntry> it = filePaths.iterator(); it.hasNext(); ) {
                String curRemotePath = remotePath + "/" + it.next().getFilename();
                receiveAllFile(curRemotePath, tmpPath, hasTmpPath, fileNameRegex, encoding, delReceiveFile, max, maxSize, bigFilePath, postfixName, includeSubdirectory, delayTime, minSize, params, channel, receivedFileSum);
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
        pollSize = 0;
        for (ChannelSftp channel = channelPool.poll(); channel != null; channel = channelPool.poll()) {
            try {
                channel.getSession().disconnect();
            } catch (Exception e) {
                log.warn("Session disconnect fail");
            }
            try {
                channel.disconnect();
            } catch (Exception e) {
                log.warn("channel.disconnect fail");
            }
        }
        for (ChannelSftp channel = channelPoolRunning.poll(); channel != null; channel = channelPoolRunning.poll()) {
            try {
                channel.getSession().disconnect();
            } catch (Exception e) {
                log.warn("Session disconnect fail");
            }
            try {
                channel.disconnect();
            } catch (Exception e) {
                log.warn("channel.disconnect fail");
            }
        }
    }

    private ChannelSftp createChannelSftp() throws Exception {
        JSch jSch = new JSch(); //创建JSch对象
        Session session = jSch.getSession(receiverConfigSftp.getUsername(), receiverConfigSftp.getHost(), receiverConfigSftp.getPort());//根据用户名，主机ip和端口获取一个Session对象
        session.setPassword(receiverConfigSftp.getPassword());//设置密码
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);//为Session对象设置properties
        session.setTimeout(receiverConfigSftp.getTimeout());//设置超时
        session.connect();//通过Session建立连接
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }

    private ChannelSftp checkAndConnect(ChannelSftp channel) throws Exception {
        if (!channel.isConnected()) {
            channelPoolRunning.remove(channel);
            try {
                channel.getSession().disconnect();
            } catch (Exception e) {
                log.warn("Session disconnect fail", e);
            }
            channel = this.createChannelSftp();
            channelPoolRunning.put(channel);
        }
        return channel;
    }

}
