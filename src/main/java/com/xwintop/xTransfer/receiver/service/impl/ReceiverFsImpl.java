package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigFs;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.service.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * @ClassName: ReceiverFsImpl
 * @Description: Fs接收器
 * @author: xufeng
 * @date: 2018/6/13 16:11
 */

@Service("receiverFs")
@Scope("prototype")
@Slf4j
public class ReceiverFsImpl implements Receiver {
    private ReceiverConfigFs receiverConfigFs;
    private MessageHandler messageHandler;

    @Override
    public void receive(Map params) throws Exception {
//        log.info("开始执行receiverFs:" + receiverConfigFs.toString());
        receiveInternalByFiles(receiverConfigFs.getPathIn(), receiverConfigFs.getPathTmp(), params, 0);
//        receiveInternal(receiverConfigFs.getPathIn(), receiverConfigFs.getPathTmp(), receiverConfigFs.getMax(), receiverConfigFs.getMaxSize(), receiverConfigFs.getEncoding(), params, receiverConfigFs.isHasTmpPath(), receiverConfigFs.isDelReceiveFile(), receiverConfigFs.getFileNameRegex(), receiverConfigFs.isIncludeSubdirectory(), receiverConfigFs.getDelayTime(), receiverConfigFs.getMinSize(), receiverConfigFs.getBigFilePath(), 0);
//        log.info("完成执行re5TB6QAZ m,ceiveFs：" + receiverConfigFs.toString());
    }

    private void receiveInternalByFiles(String pathIn, String pathTmp, Map params, int receivedFileSum) throws Exception {
        pathIn = StringUtils.appendIfMissing(pathIn, "/", "/", "\\");
        pathTmp = StringUtils.appendIfMissing(pathTmp, "/", "/", "\\");
        File dirIn = new File(pathIn);
        if (!dirIn.exists() || !dirIn.isDirectory()) {
            log.error("path: " + pathIn + "not exist or not a dirctory!");
            return;
        }
        File dirOpt = null;
        if (receiverConfigFs.isHasTmpPath()) {
            dirOpt = new File(pathTmp);
            if (!dirOpt.exists() || !dirOpt.isDirectory()) {
                log.error("path: " + pathTmp + "not exist or not a dirctory!");
                return;
            }
        } else {
            //when has no tmp directory then do
            dirOpt = dirIn;
        }
        Iterator<Path> pathIterator = null;
        AutoCloseable autoCloseable = null;
        if (receiverConfigFs.getLastModifiedSort() == null || receiverConfigFs.getLastModifiedSort() == 0) {
            DirectoryStream<Path> stream = Files.newDirectoryStream(dirIn.toPath());
            autoCloseable = stream;
            pathIterator = stream.iterator();
        } else {
            Stream<Path> stream = Files.list(dirIn.toPath());
            stream = stream.sorted((o1, o2) -> {
                if (receiverConfigFs.getLastModifiedSort() == -1) {
                    return (int) (o1.toFile().lastModified() - o2.toFile().lastModified());
                } else if (receiverConfigFs.getLastModifiedSort() == 1) {
                    return (int) (o2.toFile().lastModified() - o1.toFile().lastModified());
                }
                return 0;
            });
            autoCloseable = stream;
            pathIterator = stream.iterator();
        }
        List<File> filePaths = new ArrayList<>();
        try {
            while (pathIterator.hasNext()) {
                if (receiverConfigFs.getMax() >= 0 && receiverConfigFs.getMax() <= receivedFileSum) {
                    break;
                }
                Path curPath = pathIterator.next();
                File curFile = curPath.toFile();
                if (curFile.isFile()) {
                    String curFileName = curFile.getName();
                    if (!receiverConfigFs.isHasTmpPath() && curFileName.endsWith(".TMP_FILE")) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(receiverConfigFs.getFileNameRegex())) {
                        if (!curFileName.matches(receiverConfigFs.getFileNameRegex())) {
                            continue;
                        }
                    }
                    boolean readSuccessFlag = false;
                    if (receiverConfigFs.getMaxSize() != -1 && curFile.length() > receiverConfigFs.getMaxSize()) {
                        log.warn(curFile.getAbsolutePath() + ": size is exceed the limit [" + receiverConfigFs.getMaxSize() + "]. fileSize:" + curFile.length());
                        String bigFilePath = receiverConfigFs.getBigFilePath();
                        if (bigFilePath != null && bigFilePath.length() > 0) {
                            try {
                                FileUtils.moveFileToDirectory(curFile, new File(bigFilePath), true);
                                log.info("move big file to:" + bigFilePath + "/" + curFile.getName());
                            } catch (Exception e) {
                                log.error("move big file " + curFile.getName() + " error,", e);
                            }
                        }
                        continue;
                    }
                    if (receiverConfigFs.getDelayTime() > 0) {
                        if (System.currentTimeMillis() - curFile.lastModified() < receiverConfigFs.getDelayTime()) {
                            log.info("File is more new than currentTime-" + receiverConfigFs.getDelayTime());
                            continue;
                        }
                    }
                    if (receiverConfigFs.getMinSize() != -1) {
                        if (curFile.length() < receiverConfigFs.getMinSize()) {
                            log.info("File's Size is too small then skip it,file name is:" + curFile.getName() + " fileSize:" + curFile.length());
                            continue;
                        }
                    }
                    IMessage msg = new DefaultMessage();
                    String uuid = msg.getId();
                    log.info("begin receiving file:" + curFileName + "size=" + curFile.length() + ",id:" + uuid);
                    File optFile = null;
                    String tmFileName = curFile.getName() + "." + uuid + ".TMP_FILE";
                    optFile = new File(dirOpt, tmFileName);
                    if (!curFile.exists()) {
                        log.warn("curFile is not exist! file:" + curFile.getAbsolutePath());
                        continue;
                    }
                    if (!curFile.renameTo(optFile)) {
                        log.warn("can not move file:" + curFileName);
                        continue;
                    }
                    if (!optFile.exists()) {
                        log.warn("optFile is not exist! file:" + optFile.getAbsolutePath());
                        continue;
                    }
                    try {
                        try {
                            msg.setRawData(Files.readAllBytes(optFile.toPath()));
                        } catch (NoSuchFileException n) {
                            log.warn("read optFile is not exist! file:" + optFile.getAbsolutePath());
                            continue;
                        }
                        msg.setFileName(curFileName);
                        msg.setEncoding(receiverConfigFs.getEncoding());
                        msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
                        msg.setProperty(LOGKEYS.CHANNEL_IN, pathIn);
                        msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
                        msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigFs.getId());
                        if (MapUtils.isNotEmpty(receiverConfigFs.getArgs())) {
                            msg.getProperties().putAll(receiverConfigFs.getArgs());
                        }
                        IContext ctx = new DefaultContext();
                        ctx.setMessage(msg);

                        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, uuid, null);
                        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
                        msgLogInfo.put(LOGKEYS.CHANNEL_IN, pathIn);
                        msgLogInfo.put(LOGKEYS.MSG_TAG, curFileName);
                        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
                        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigFs.getId());
                        MsgLogger.info(msgLogInfo.toMap());

                        messageHandler.handle(ctx);
                        readSuccessFlag = true;
                    } catch (Exception e) {
                        log.error("ReceiverFs 读取文件失败：", e);
                    }
                    if (!readSuccessFlag) {
                        if (optFile.exists()) {
                            if (optFile.renameTo(curFile)) {
                                log.info("success recovery:move file to receive path,file name is:" + curFile.getAbsolutePath());
                            } else {
                                log.warn("recovery failed:can't move tmp file to receive path,file name is:" + curFile.getAbsolutePath());
                            }
                        } else {
                            log.error("tmp file is lost:" + optFile.getAbsolutePath());
                        }
                        continue;
                    }
                    if (receiverConfigFs.isDelReceiveFile()) {
                        optFile.delete();
                    } else {
                        //must move tmp file to dist directory
                        optFile.renameTo(curFile);
                    }
                    log.info("received file:" + curFileName);
                    receivedFileSum++;
                } else {
                    if (receiverConfigFs.isIncludeSubdirectory() && curFile.isDirectory() && receivedFileSum < receiverConfigFs.getMax()) {
                        filePaths.add(curFile);
                    }
                }
            }
        } finally {
            if (autoCloseable != null) {
                autoCloseable.close();
            }
        }
        //is directory then do
        if (receiverConfigFs.isIncludeSubdirectory() && receivedFileSum < receiverConfigFs.getMax()) {
            for (Iterator<File> it = filePaths.iterator(); it.hasNext(); ) {
                String curPathIn = it.next().getAbsolutePath();
                receiveInternalByFiles(curPathIn, pathTmp, params, receivedFileSum);
            }
        }
    }

    private void receiveInternal(String pathIn, String pathTmp, int max, long maxSize,
                                 String encoding, Map params, boolean hasTmpPath,
                                 boolean delReceiveFile, final String fileNameRegex, boolean includeSubdirectory,
                                 long delayTime, long minSize, String bigFilePath, int receivedFileSum) {
        pathIn = StringUtils.appendIfMissing(pathIn, "/", "/", "\\");
        pathTmp = StringUtils.appendIfMissing(pathTmp, "/", "/", "\\");
        File dirIn = new File(pathIn);
        if (!dirIn.exists() || !dirIn.isDirectory()) {
            log.error("path: " + pathIn + "not exist or not a dirctory!");
            return;
        }
        File dirOpt = null;
        if (hasTmpPath) {
            dirOpt = new File(pathTmp);
            if (!dirOpt.exists() || !dirOpt.isDirectory()) {
                log.error("path: " + pathTmp + "not exist or not a dirctory!");
                return;
            }
        } else {
            //when has no tmp directory then do
            dirOpt = dirIn;
        }
        //add filter
        File[] files = null;
        if (StringUtils.isNotBlank(fileNameRegex)) {
            files = dirIn.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (!hasTmpPath && file.getName().endsWith(".TMP_FILE")) {
                        return false;
                    }
                    if (file.getName().matches(fileNameRegex)) {
                        return true;
                    }
                    return false;
                }
            });
        } else {
            files = dirIn.listFiles(file -> {
                if (!hasTmpPath && file.getName().endsWith(".TMP_FILE")) {
                    return false;
                }
                return true;
            });
        }
        if (receiverConfigFs.getLastModifiedSort() != null) {
            if (receiverConfigFs.getLastModifiedSort() == -1) {
                ((LastModifiedFileComparator) LastModifiedFileComparator.LASTMODIFIED_COMPARATOR).sort(files);
            } else if (receiverConfigFs.getLastModifiedSort() == 1) {
                if (files != null) {
                    Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                }
            }
        }
        List<File> filePaths = new ArrayList<>();
        for (int i = 0; files != null && i < files.length && max > receivedFileSum; i++) {
            File curFile = files[i];
            if (curFile.isFile()) {
                boolean readSuccessFlag = false;
                if (maxSize != -1 && curFile.length() > maxSize) {
                    log.warn(curFile.getAbsolutePath() + ": size is exceed the limit [" + maxSize + "].");
                    if (bigFilePath != null && bigFilePath.length() > 0) {
                        try {
                            FileUtils.moveFileToDirectory(curFile, new File(bigFilePath), true);
                            log.info("move big file to:" + bigFilePath + "/" + curFile.getName());
                        } catch (Exception e) {
                            log.error("move big file " + curFile.getName() + " error,", e);
                        }
                    }
                    continue;
                }
                if (delayTime > 0) {
                    if (System.currentTimeMillis() - curFile.lastModified() < delayTime) {
                        log.warn("File is more new than currentTime-" + delayTime);
                        continue;
                    }
                }
                if (minSize != -1) {
                    if (curFile.length() < minSize) {
                        log.warn("File's Size is too small then skip it,file name is:" + curFile.getName());
                        continue;
                    }
                }
                IMessage msg = new DefaultMessage();
                String uuid = msg.getId();
                String curFileName = curFile.getName();
                log.info("begin receiving file:" + curFileName + "size=" + curFile.length() + ",id:" + uuid);
                File optFile = null;
//                if (hasTmpPath) {
//                    optFile = new File(dirOpt, uuid);
//                } else {
                String tmFileName = curFile.getName() + "." + uuid + ".TMP_FILE";
                optFile = new File(dirOpt, tmFileName);
//                }
                if (!curFile.exists()) {
                    log.warn("curFile is not exist! file:" + curFile.getAbsolutePath());
                    continue;
                }
                if (!curFile.renameTo(optFile)) {
                    log.warn("can not move file:" + curFileName);
                    continue;
                }
                if (!optFile.exists()) {
                    log.warn("optFile is not exist! file:" + optFile.getAbsolutePath());
                    continue;
                }
                try {
                    msg.setRawData(FileUtils.readFileToByteArray(optFile));
                    msg.setFileName(curFileName);
                    msg.setEncoding(encoding);
                    msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
                    msg.setProperty(LOGKEYS.CHANNEL_IN, pathIn);
                    msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
                    msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigFs.getId());
                    if (MapUtils.isNotEmpty(receiverConfigFs.getArgs())) {
                        msg.getProperties().putAll(receiverConfigFs.getArgs());
                    }
                    IContext ctx = new DefaultContext();
                    ctx.setMessage(msg);

                    Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, uuid, null);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN, pathIn);
                    msgLogInfo.put(LOGKEYS.MSG_TAG, curFileName);
                    msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                    msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                    msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                    msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
                    msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigFs.getId());

                    MsgLogger.info(msgLogInfo.toMap());

                    messageHandler.handle(ctx);
//                    log.info("读取文件：" + msg.getRawDataByString());
                    readSuccessFlag = true;
                } catch (Exception e) {
                    log.error("ReceiverFs 读取文件失败：", e);
                } finally {
                }
                if (!readSuccessFlag) {
                    if (optFile.exists()) {
                        if (optFile.renameTo(curFile)) {
                            log.info("success recovery:move file to receive path,file name is:" + curFile.getAbsolutePath());
                        } else {
                            log.warn("recovery failed:can't move tmp file to receive path,file name is:" + curFile.getAbsolutePath());
                        }
                    } else {
                        log.error("tmp file is lost:" + optFile.getAbsolutePath());
                    }
                    continue;
                }
                if (delReceiveFile) {
                    optFile.delete();
                } else {
                    //must move tmp file to dist directory
                    optFile.renameTo(curFile);
                }
                log.info("received file:" + curFileName);
                receivedFileSum++;
            } else {
                if (includeSubdirectory && curFile.isDirectory() && receivedFileSum < max) {
                    filePaths.add(curFile);
                }
            }
        }
        //is directory then do
        if (includeSubdirectory && receivedFileSum < max) {
            for (Iterator<File> it = filePaths.iterator(); it.hasNext(); ) {
                String curPathIn = it.next().getAbsolutePath();
                receiveInternal(curPathIn, pathTmp, max, maxSize, encoding, params, hasTmpPath, delReceiveFile, fileNameRegex, includeSubdirectory, delayTime, minSize, bigFilePath, receivedFileSum);
            }
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigFs = (ReceiverConfigFs) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {

    }

}
