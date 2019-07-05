package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfigHdfs;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: SenderHdfsImpl
 * @Description: Hdfs发送类
 * @author: xufeng
 * @date: 2019/7/5 17:26
 */


@Service("senderHdfs")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderHdfsImpl implements Sender {
    private SenderConfigHdfs senderConfigHdfs;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderFs,taskName:" + params.get(TaskQuartzJob.JOBID));
        String path = senderConfigHdfs.getPath();
        path = StringUtils.appendIfMissing(path, "/", "/", "\\");
        String fileName = msg.getFileName();
        if (StringUtils.isNotBlank(senderConfigHdfs.getFileName())) {
            fileName = ParseVariableCommon.parseVariable(senderConfigHdfs.getFileName(), msg, params);
        }
        if (path == null || path.trim().length() <= 0) {
            throw new Exception("configuration for path is missing.MessageSender:" + senderConfigHdfs.toString());
        }
        if (fileName == null || fileName.trim().length() <= 0) {
            throw new Exception("configuration for fileName is missing.MessageSender:" + senderConfigHdfs.toString());
        }
        Path filePath = new Path(path);
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", senderConfigHdfs.getHdfsUrl());
        FileSystem hdfs = FileSystem.get(conf);
        try {
            if (!hdfs.exists(filePath)) {
                if (senderConfigHdfs.isCreatePathFlag()) {
                    if (hdfs.mkdirs(filePath)) {
                        log.info("success create path:" + path);
                    } else {
                        throw new Exception("can't create path:" + path);
                    }
                } else {
                    throw new Exception("configuration for path is error,path:" + path);
                }
            }
            if (senderConfigHdfs.isOverload()) {
                hdfs.deleteOnExit(new Path(path, fileName));
            }
            FSDataOutputStream out = hdfs.create(new Path(path, fileName));
            out.write(msg.getMessage());
            out.flush();
            out.close();
        } finally {
            hdfs.close();
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_HDFS);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigHdfs.getPath());
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
        this.senderConfigHdfs = (SenderConfigHdfs) senderConfig;
    }

    @Override
    public void destroy() throws Exception {

    }
}
