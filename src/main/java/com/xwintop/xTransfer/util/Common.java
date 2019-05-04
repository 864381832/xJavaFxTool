package com.xwintop.xTransfer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.Date;

/**
 * @ClassName: Common
 * @Description: 工具类
 * @author: xufeng
 * @date: 2018/6/12 14:21
 */

@Slf4j
public class Common {

    /**
     * @param filePath         目录文件
     * @param isCreatePathFlag 不存在时是否自动创建
     * @Description: 检查是否存在目录
     */
    public static void checkIsHaveDir(File filePath, boolean isCreatePathFlag) throws Exception {
        if (!filePath.exists()) {
            if (isCreatePathFlag) {
                log.warn("configuration path:" + filePath.getPath() + " isn't exist.now create it.");
                if (filePath.mkdirs()) {
                    log.info("success create path:" + filePath.getPath());
                } else {
                    throw new Exception("can't create path:" + filePath.getPath());
                }
            } else {
                throw new Exception("configuration for path is error,path:" + filePath.getPath());
            }
        }
    }

    /**
     * 在路径的末端加入分隔符delimited
     * 如果已有delimited，则不添加
     * delimited用系统默认的目录分割符
     */
    public static String addSep(String path) {
//        return StringUtils.appendIfMissing(path, "/", "/", "\\");
        return StringUtils.appendIfMissing(path, File.separator, "/", "\\");
    }

    /**
     * @param rootPath
     * @Description 按天创建子目录
     */
    public static File dirByDay(File rootPath) {
        return dirByDateFormat(rootPath, "yyyyMMdd");
    }

    /**
     * @param rootPath
     * @Description 按小时创建子目录
     */
    public static File dirByHour(File rootPath) {
        return dirByDateFormat(rootPath, "yyyyMMddHH");
    }

    /**
     * @param rootPath
     * @Description 按天/小时创建子目录
     */
    public static File dirByDay_Hour(File rootPath) {
        rootPath = dirByDateFormat(rootPath, "yyyyMMdd");
        return dirByDateFormat(rootPath, "HH");
    }

    /**
     * @param rootPath
     * @Description 按分钟创建子目录
     */
    public static File dirByMinutes(File rootPath) {
        return dirByDateFormat(rootPath, "yyyyMMddHHmm");
    }

    /**
     * @param rootPath 主目录
     * @param pattern  时间格式
     * @return 格式子文件夹
     * @Description: 根据时间格式化创建子目录
     */
    public static File dirByDateFormat(File rootPath, String pattern) {
        String hour = DateFormatUtils.format(new Date(), pattern);
        File dirLevelTwo = new File(rootPath, hour);
        if (!dirLevelTwo.exists()) {
            dirLevelTwo.mkdir();
        }
        return dirLevelTwo;
    }

    public static String getPathByCheckFileName(String path) {
        return getPathByCheckFileName(new File(path)).getPath();
    }

    /**
     * @param file 原文件
     * @return 新的文件
     * @Description: 查找是否有重复文件；同一文件名，加后缀（.YYYYMMDDHHSSSS）
     */
    public static File getPathByCheckFileName(File file) {
        if (file.exists()) {
            file = new File(file.getPath() + DateFormatUtils.format(new Date(), ".yyyyMMddHHmmssss"));
        }
        return file;
    }
}
