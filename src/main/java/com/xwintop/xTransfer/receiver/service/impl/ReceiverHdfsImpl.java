package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigHdfs;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceiverFsImpl
 * @Description: Fs接收器
 * @author: xufeng
 * @date: 2018/6/13 16:11
 */

@Service("receiverFs")
@Scope("prototype")
@Slf4j
public class ReceiverHdfsImpl implements Receiver {
    private ReceiverConfigHdfs receiverConfigHdfs;
    private MessageHandler messageHandler;

    @Override
    public void receive(Map params) throws Exception {
        receiveInternalByFiles(receiverConfigHdfs.getPathIn(), params, 0);
    }

    private void receiveInternalByFiles(String pathIn, Map params, int receivedFileSum) throws Exception {
        pathIn = StringUtils.appendIfMissing(pathIn, "/", "/", "\\");
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", receiverConfigHdfs.getHdfsUrl());
        FileSystem hdfs = FileSystem.get(conf);
        Path pathin = new Path(pathIn);
        if (!hdfs.exists(pathin) || !hdfs.isDirectory(pathin)) {
            log.error("path: " + pathIn + "not exist or not a dirctory!");
            return;
        }
        RemoteIterator<FileStatus> pathIterator = hdfs.listStatusIterator(pathin);
        List<FileStatus> filePaths = new ArrayList<>();
        try {
            while (pathIterator.hasNext()) {
                if (receiverConfigHdfs.getMax() >= 0 && receiverConfigHdfs.getMax() <= receivedFileSum) {
                    break;
                }
                FileStatus curPath = pathIterator.next();
                Path curFile = curPath.getPath();
                if (curPath.isDirectory()) {
                    String curFileName = curFile.getName();
                    if (StringUtils.isNotBlank(receiverConfigHdfs.getFileNameRegex())) {
                        if (!curFileName.matches(receiverConfigHdfs.getFileNameRegex())) {
                            continue;
                        }
                    }
                    if (receiverConfigHdfs.getMaxSize() != -1 && curPath.getLen() > receiverConfigHdfs.getMaxSize()) {
                        log.warn(curFile.toString() + ": size is exceed the limit [" + receiverConfigHdfs.getMaxSize() + "]. fileSize:" + curPath.getLen());
                        continue;
                    }
                    if (receiverConfigHdfs.getDelayTime() > 0) {
                        if (System.currentTimeMillis() - curPath.getModificationTime() < receiverConfigHdfs.getDelayTime()) {
                            log.info("File is more new than currentTime-" + receiverConfigHdfs.getDelayTime());
                            continue;
                        }
                    }
                    if (receiverConfigHdfs.getMinSize() != -1) {
                        if (curPath.getLen() < receiverConfigHdfs.getMinSize()) {
                            log.info("File's Size is too small then skip it,file name is:" + curFile.getName() + " fileSize:" + curPath.getLen());
                            continue;
                        }
                    }
                    IMessage msg = new DefaultMessage();
                    String uuid = msg.getId();
                    log.info("begin receiving file:" + curFileName + "size=" + curPath.getLen() + ",id:" + uuid);
                    if (!hdfs.exists(curFile)) {
                        log.warn("curFile is not exist! file:" + curFile.toString());
                        continue;
                    }
                    try {
                        try {
                            FSDataInputStream is = hdfs.open(curFile);
                            ByteArrayOutputStream fout = new ByteArrayOutputStream();
                            byte[] b = new byte[2048];
                            int len = 0;
                            while ((len = is.read(b)) != -1) {
                                fout.write(b, 0, len);
                            }
                            msg.setRawData(fout.toByteArray());
                        } catch (NoSuchFileException n) {
                            log.warn("read optFile is not exist! file:" + curFile.toString());
                            continue;
                        }
                        msg.setFileName(curFileName);
                        msg.setEncoding(receiverConfigHdfs.getEncoding());
                        msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_HDFS);
                        msg.setProperty(LOGKEYS.CHANNEL_IN, pathIn);
                        msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
                        msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigHdfs.getId());
                        if (MapUtils.isNotEmpty(receiverConfigHdfs.getArgs())) {
                            msg.getProperties().putAll(receiverConfigHdfs.getArgs());
                        }
                        IContext ctx = new DefaultContext();
                        ctx.setMessage(msg);

                        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, uuid, null);
                        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_HDFS);
                        msgLogInfo.put(LOGKEYS.CHANNEL_IN, pathIn);
                        msgLogInfo.put(LOGKEYS.MSG_TAG, curFileName);
                        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_HDFS);
                        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigHdfs.getId());
                        MsgLogger.info(msgLogInfo.toMap());

                        messageHandler.handle(ctx);
                    } catch (Exception e) {
                        log.error("ReceiverFs 读取文件失败：", e);
                    }
                    if (receiverConfigHdfs.isDelReceiveFile()) {
                        hdfs.deleteOnExit(curFile);
                    }
                    log.info("received file:" + curFileName);
                    receivedFileSum++;
                } else {
                    if (receiverConfigHdfs.isIncludeSubdirectory() && curPath.isDirectory() && receivedFileSum < receiverConfigHdfs.getMax()) {
                        filePaths.add(curPath);
                    }
                }
            }
        } finally {
            if (hdfs != null) {
                hdfs.close();
            }
        }
        //is directory then do
        if (receiverConfigHdfs.isIncludeSubdirectory() && receivedFileSum < receiverConfigHdfs.getMax()) {
            for (Iterator<FileStatus> it = filePaths.iterator(); it.hasNext(); ) {
                String curPathIn = it.next().getPath().toUri().getPath();
                receiveInternalByFiles(curPathIn, params, receivedFileSum);
            }
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigHdfs = (ReceiverConfigHdfs) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {

    }

}
