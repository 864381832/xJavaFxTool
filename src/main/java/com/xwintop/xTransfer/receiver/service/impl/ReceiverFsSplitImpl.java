package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.filter.bean.FilterConfigBackup;
import com.xwintop.xTransfer.filter.enums.StrategyEnum;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigFsSplit;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.util.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceiverFsSplitImpl
 * @Description: Fs接收器大文件拆分操作配置接收器
 * @author: xufeng
 * @date: 2018/9/4 17:20
 */

@Service("receiverFsSplit")
@Scope("prototype")
@Slf4j
public class ReceiverFsSplitImpl implements Receiver {
    private static String SPILT = ".SPLIT_";  //拆分报文后缀
    private ReceiverConfigFsSplit receiverConfigFsSplit;
    private MessageHandler messageHandler;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("开始执行receiverFs:" + receiverConfigFsSplit.toString());
        receiveInternal(receiverConfigFsSplit.getPathIn(), receiverConfigFsSplit.getPathTmp(), receiverConfigFsSplit.getMax(), receiverConfigFsSplit.getEncoding(), params, receiverConfigFsSplit.isHasTmpPath(), receiverConfigFsSplit.isDelReceiveFile(), receiverConfigFsSplit.getFileNameRegex(), receiverConfigFsSplit.isIncludeSubdirectory(), receiverConfigFsSplit.getDelayTime(), 0, receiverConfigFsSplit.getDefaultRow());
        log.debug("完成执行receiveFs：" + receiverConfigFsSplit.toString());
    }

    private void receiveInternal(String pathIn, String pathTmp, int max, String encoding, Map params, boolean hasTmpPath,
                                 boolean delReceiveFile, final String fileNameRegex, boolean includeSubdirectory, long delayTime, int receivedFileSum, int defaultRow) throws Exception{
        pathIn = StringUtils.appendIfMissing(pathIn, "/", "/", "\\");
        pathTmp = StringUtils.appendIfMissing(pathTmp, "/", "/", "\\");
        File dirIn = new File(pathIn);//源文件
        if (!dirIn.exists() || !dirIn.isDirectory()) {
            log.error("path: " + pathIn + "not exist or not a dirctory!");
            return;
        }
        File dirOpt = null;//备份文件
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
        File[] files = null;//目录下的文件
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
//                if (!hasTmpPath && ".TMP_FILE.".equals(file.getName().substring(file.getName().length() - 42, file.getName().length() - 32))) {
                if (!hasTmpPath && file.getName().endsWith(".TMP_FILE")) {
                    return false;
                }
                return true;
            });
        }
        List<File> filePaths = new ArrayList<>();
        for (int i = 0; files != null && i < files.length && max > receivedFileSum; i++) {
            File curFile = files[i];
            if (curFile.isFile()) {
                boolean readSuccessFlag = false;
                if (delayTime > 0) {
                    if (System.currentTimeMillis() - curFile.lastModified() < delayTime) {
                        log.warn("File is more new than currentTime-" + delayTime);
                        continue;
                    }
                }
                IMessage msg = new DefaultMessage();
                String uuid = msg.getId();
                String curFileName = curFile.getName();
                log.info("begin receiving file:" + curFileName + "size=" + curFile.length() + ",id:" + uuid);
                String tmFileName = curFile.getName() + "." + uuid + ".TMP_FILE";
                File optFile = new File(dirOpt, tmFileName);//临时文件
                if (!curFile.renameTo(optFile)) {
                    log.warn("can not move file:" + curFileName);
                    continue;
                }
                //备份文件
                List<FilterConfigBackup> list = receiverConfigFsSplit.getFilterConfigBackupList();
                for (FilterConfigBackup filterConfigBackup : list) {
                    doBackupFile(filterConfigBackup, optFile, curFileName);
                }
                try {
                    StringBuffer sb = new StringBuffer();
                    int index = 0;
                    int batch = 0;
                    LineIterator it = FileUtils.lineIterator(optFile, encoding);
                    try {
                        while (it.hasNext()) {
                            if (index > 0) {
                                sb.append("\n");
                            }
                            index++;
                            sb.append(it.nextLine());
                            if (index == defaultRow) {
                                receiveSplit(sb, curFileName + SPILT + batch, encoding, pathIn, params); //分行读取报文（大报文）
                                batch++;
                                index = 0;
                                sb.setLength(0);
                            }
                        }
                        if (sb.length() > 0) {
                            receiveSplit(sb, curFileName + SPILT + batch, encoding, pathIn, params); //分行读取报文（大报文）
                        }
                    } finally {
                        LineIterator.closeQuietly(it);
                    }

//                    log.info("读取文件：" + msg.getRawDataByString());
                    readSuccessFlag = true;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
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
                receiveInternal(curPathIn, pathTmp, max, encoding, params, hasTmpPath, delReceiveFile, fileNameRegex, includeSubdirectory, delayTime, receivedFileSum, defaultRow);
            }
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigFsSplit = (ReceiverConfigFsSplit) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {

    }

    /**
     * @Description 发送拆分后文件至Message中
     * @param stringBuffer 文件拆分后内容
     * @param curFileName 文件拆分后名
     * @param encoding 编码
     * @param pathIn 文件路径
     * @param params 附加参数
     */
    public void receiveSplit(StringBuffer stringBuffer, String curFileName, String encoding, String pathIn, Map params) throws Exception {
        IMessage msg = new DefaultMessage();
        if (StringUtils.isNoneBlank(stringBuffer)) {
            byte[] data = stringBuffer.toString().getBytes(encoding);
            msg.setRawData(data);
            msg.setFileName(curFileName);
            msg.setEncoding(encoding);
            msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
            msg.setProperty(LOGKEYS.CHANNEL_IN, pathIn);
            msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
            msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigFsSplit.getId());
            IContext ctx = new DefaultContext();
            ctx.setMessage(msg);

            Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
            msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_FS);
            msgLogInfo.put(LOGKEYS.CHANNEL_IN, pathIn);
            msgLogInfo.put(LOGKEYS.MSG_TAG, curFileName);
            msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
            msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
            msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
            msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_FS);
            msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigFsSplit.getId());

            MsgLogger.info(msgLogInfo.toMap());

            messageHandler.handle(ctx);
        }
    }

    /**
     * @Description: 备份文件操作
     * @param filterConfigBackup 大报文备份配置（使用了备份操作的配置）
     * @param optFile 原文件
     * @param curFileName 原文件名
     */
    public void doBackupFile(FilterConfigBackup filterConfigBackup, File optFile, String curFileName) throws Exception {
        //        log.info("开始文件备份操作");
        String path = filterConfigBackup.getPath();//备份目录
        String tmp = filterConfigBackup.getTmpPath();//备份缓存目录
        String fileName = curFileName;
        if (path == null || path.trim().length() <= 0) {
            throw new Exception("configuration for path is missing.MessageSender:" + filterConfigBackup.toString());
        }
        if (fileName == null || fileName.trim().length() <= 0) {
            throw new Exception("configuration for fileName is missing.MessageSender:" + filterConfigBackup.toString());
        }
        path = StringUtils.appendIfMissing(path, "/", "/", "\\");
        tmp = StringUtils.appendIfMissing(tmp, "/", "/", "\\");
        File filePath = new File(path);
        Common.checkIsHaveDir(filePath, filterConfigBackup.isCreatePathFlag());//检查是否存在目录
        switch (StrategyEnum.valueOf(filterConfigBackup.getStrategy())) {//文件目录策略（direct、day、hour）
            case day:
                filePath = Common.dirByDay(filePath);
                break;
            case hour:
                filePath = Common.dirByHour(filePath);
                break;
            case minutes:
                filePath = Common.dirByMinutes(filePath);
                break;
        }
        File tmpFile;
        if (StringUtils.isBlank(tmp)) {
            tmpFile = Common.getPathByCheckFileName(new File(filePath, fileName));//查找是否有重复文件；同一文件名，加后缀（.YYYYMMDDHHSSSS）
        } else {
            File tmpPath = new File(tmp);
            if (!tmpPath.exists()) {
                tmpPath = filePath;
            }
            tmpFile = new File(tmpPath, fileName);
        }
        FileUtils.copyFile(optFile,tmpFile);
        if (StringUtils.isNotBlank(tmp)) {
            //查找是否有重复文件；同一文件名，加后缀（.YYYYMMDDHHSSSS）
            File backupFile = Common.getPathByCheckFileName(new File(filePath, fileName));
            if (!tmpFile.renameTo(backupFile)) {
                log.error("error when rename file " + tmpFile.getPath() + " to " + filePath.getPath());
            }
        }
        log.info("sent <" + optFile.getPath() + "> to " + filePath + " as " + fileName);
    }
}
