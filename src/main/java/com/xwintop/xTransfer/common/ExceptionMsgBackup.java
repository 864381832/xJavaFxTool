package com.xwintop.xTransfer.common;

import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.util.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @ClassName: ExceptionMsgBackup
 * @Description: 发生异常时备份消息
 * @author: xufeng
 * @date: 2019/3/21 13:22
 */

@Slf4j
public class ExceptionMsgBackup {
    private static final String backupPath = "./exceptionMsgBackup/";

    public static void msgBackup(String configId, IContext iContext) {
        try {
            for (IMessage msg : iContext.getMessages()) {
                String path = backupPath + configId + File.separatorChar;
                String fileName = msg.getFileName();
                File filePath = new File(path);
                Common.checkIsHaveDir(filePath, true);
                filePath = Common.dirByDay(filePath);
                File backupFile = new File(filePath, fileName);
                FileUtils.writeByteArrayToFile(backupFile, msg.getMessage());
                log.info("发生异常备份：" + backupFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("异常备份失败：", e);
        }
    }
}
