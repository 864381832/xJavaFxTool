package com.xwintop.xTransfer.sender.service.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfigSftp;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SenderSftpImpl
 * @Description: Sftp发送类
 * @author: xufeng
 * @date: 2019/2/13 15:36
 */

@Service("senderSftp")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderSftpImpl implements Sender {
    private SenderConfigSftp senderConfigSftp;

    private volatile BlockingQueue<ChannelSftp> channelPool = new LinkedBlockingQueue<>();
    private volatile BlockingQueue<ChannelSftp> channelPoolRunning = new LinkedBlockingQueue<>();
    private volatile int pollSize = 0;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        ChannelSftp channel;
        if (senderConfigSftp.isLongConnection()) {
            synchronized (this) {
                if (pollSize < senderConfigSftp.getConnectionPoolSize() && channelPool.size() <= 0) {
                    channelPool.put(createChannelSftp());
                    pollSize++;
                }
            }
//            channel = channelPool.take();
            channel = channelPool.poll(10, TimeUnit.SECONDS);
            if (channel == null) {
                log.warn("channelPool is null:" + senderConfigSftp.getId());
                return false;
            }
            if (!channel.isConnected()) {
                channel.getSession().disconnect();
                channel = createChannelSftp();
            }
        } else {
            channel = createChannelSftp();
        }
        channelPoolRunning.put(channel);
        // parse the variable of send properties
        String path = StringUtils.appendIfMissing(senderConfigSftp.getPath(), "/", "/", "\\");
        path = ParseVariableCommon.parseVariable(path, msg, params);
        boolean createPathFlag = senderConfigSftp.isCreatePathFlag();
        boolean hasTmpPath = senderConfigSftp.isHasTmpPath();
        boolean overload = senderConfigSftp.isOverload();
        String fileName = msg.getFileName();
        if (StringUtils.isNotBlank(senderConfigSftp.getFileName())) {
            fileName = ParseVariableCommon.parseVariable(senderConfigSftp.getFileName(), msg, params);
        }
        if (StringUtils.isBlank(path)) {
            throw new Exception("configuration for path is missing.MessageSender:" + senderConfigSftp.toString());
        }
        if (StringUtils.isBlank(fileName)) {
            throw new Exception("configuration for fileName is missing.MessageSender:" + senderConfigSftp.toString());
        }
        if (createPathFlag) {
            changeStringDirectory(channel, path);
        }
        String tmp = "";
        if (hasTmpPath) {
            tmp = StringUtils.appendIfMissing(senderConfigSftp.getTmpPath(), "/", "/", "\\");
            tmp = ParseVariableCommon.parseVariable(tmp, msg, params);
            if (StringUtils.isBlank(tmp)) {
                throw new Exception("configuration for tmp is missing.FtpSender:" + senderConfigSftp.toString());
            }
            if (createPathFlag) {
                changeStringDirectory(channel, tmp);
            }
        }
        // overload same filename in dest path
        if (overload) {
            try {
                if (StringUtils.isNotBlank(tmp)) {
                    channel.rm(tmp + fileName);
                }
            } catch (Exception e) {
                if (!e.getMessage().contains("No such file")) {
                    log.warn("rm fail" + tmp + fileName, e);
                }
            }
            try {
                channel.rm(path + fileName);
            } catch (Exception e) {
                if (!e.getMessage().contains("No such file")) {
                    log.warn("rm fail" + path + fileName, e);
                }
            }
        }
        String tmpFileName = fileName + "." + msg.getId();
        log.debug("tmp:" + tmp + " path:" + path + " fileName:" + fileName + " tmpFileName:" + tmpFileName);
        byte[] msgByte = null;
        if (senderConfigSftp.getEncoding() != null && !"AUTO".equalsIgnoreCase(senderConfigSftp.getEncoding())) {
            msgByte = msg.getMessage(senderConfigSftp.getEncoding());
        } else {
            msgByte = msg.getMessage();
        }
        if (hasTmpPath) {
            channel.put(new ByteArrayInputStream(msgByte), tmp + tmpFileName);
            channel.rename(tmp + tmpFileName, path + fileName);
        } else {
            channel.put(new ByteArrayInputStream(msgByte), path + fileName);
            log.debug("direct to destniation,fileName is:" + fileName);
        }
        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_SFTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigSftp.getPath());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("sent <" + msg.getId() + "> to " + path + " as " + fileName);
        channelPoolRunning.remove(channel);
        if (senderConfigSftp.isLongConnection()) {
            channelPool.put(channel);
        } else {
            channel.getSession().disconnect();
        }
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigSftp = (SenderConfigSftp) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
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
        Session session = jSch.getSession(senderConfigSftp.getUsername(), senderConfigSftp.getHost(), senderConfigSftp.getPort());//根据用户名，主机ip和端口获取一个Session对象
        session.setPassword(senderConfigSftp.getPassword());//设置密码
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);//为Session对象设置properties
        session.setTimeout(senderConfigSftp.getTimeout());//设置超时
        session.connect();//通过Session建立连接
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }

    public void changeStringDirectory(ChannelSftp channel, String path) throws SftpException {
        if (channel.ls(path) == null) {
            channel.mkdir(path);
        }
    }
}
