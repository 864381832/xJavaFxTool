package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigSftp;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
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

    private Session session = null;
    private ChannelSftp channel = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        if (session == null) {
            JSch jSch = new JSch(); //创建JSch对象
            session = jSch.getSession(senderConfigSftp.getUsername(), senderConfigSftp.getHost(), senderConfigSftp.getPort());//根据用户名，主机ip和端口获取一个Session对象
            session.setPassword(senderConfigSftp.getPassword());//设置密码
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);//为Session对象设置properties
            session.setTimeout(senderConfigSftp.getTimeout());//设置超时
            session.connect();//通过Session建立连接
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        }
//        log.debug("SenderSftp:" + msg.getTo() + "  " + msg.getMessageByString());
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
            changeStringDirectory(path);
        }
        String tmp = "";
        String postfixName = "";
        if (hasTmpPath) {
            tmp = StringUtils.appendIfMissing(senderConfigSftp.getTmpPath(), "/", "/", "\\");
            tmp = ParseVariableCommon.parseVariable(tmp, msg, params);
            if (StringUtils.isBlank(tmp)) {
                throw new Exception("configuration for tmp is missing.FtpSender:" + senderConfigSftp.toString());
            }
            if (createPathFlag) {
                changeStringDirectory(tmp);
            }
        }
        if (StringUtils.isBlank(tmp) && StringUtils.isBlank(postfixName)) {
            postfixName = "." + msg.getId();
        }
        // overload same filename in dest path
        if (overload) {
            if (StringUtils.isNotBlank(tmp)) {
                channel.rm(tmp + fileName);
            }
            channel.rm(path + fileName);
        }
        String tmpFileName = fileName + postfixName;
        if (tmp == null || tmp.trim().equals("")) {
            tmp = path;
            tmpFileName = fileName;
        }
        log.debug("tmp:" + tmp + " path:" + path + " fileName:" + fileName + " tmpFileName:" + tmpFileName);
        byte[] msgByte = msg.getMessage();
        if (senderConfigSftp.getEncoding() != null && !"AUTO".equalsIgnoreCase(senderConfigSftp.getEncoding())) {
            msgByte = msg.getMessage(senderConfigSftp.getEncoding());
        }
        if (!hasTmpPath) {
            channel.put(new ByteArrayInputStream(msgByte), path + fileName);
            log.debug("direct to destniation,fileName is:" + fileName);
        } else {
            channel.put(new ByteArrayInputStream(msgByte), tmp + tmpFileName);
            channel.rename(tmp + tmpFileName, path + fileName);
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
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigSftp = (SenderConfigSftp) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (channel != null) {
            channel.quit();
            channel.disconnect();
            channel = null;
        }
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public void changeStringDirectory(String path) throws SftpException {
        if (channel.ls(path) == null) {
            channel.mkdir(path);
        }
    }
}
