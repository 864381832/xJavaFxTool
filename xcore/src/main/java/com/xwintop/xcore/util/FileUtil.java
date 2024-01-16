package com.xwintop.xcore.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FileUtil {

    /**
     * 读取文件内容并返回为字符串
     *
     * @deprecated 使用 {@link FileUtils#readFileToString(File, Charset)}
     */
    public static String readText(File file, Charset charset) {
        try {
            return new String(Files.readAllBytes(file.toPath()), charset);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 读取文件内容，按默认编码解析，并返回为字符串
     *
     * @deprecated 使用 {@link FileUtils#readFileToString(File, Charset)}
     */
    public static String readText(File file) {
        return readText(file, Charset.defaultCharset());
    }

    /**
     * 将文件名拆分成名字部分和扩展名部分
     *
     * @deprecated 使用 {@link FilenameUtils#getExtension(java.lang.String)}
     */
    public static String[] getFileNames(File file) {
        String fileName = file.getName();
        return new String[]{
            StringUtils.substringBeforeLast(fileName, "."),
            StringUtils.substringAfterLast(fileName, ".")
        };
    }

    /**
     * @deprecated 使用 {@link FilenameUtils#getBaseName(java.lang.String)}
     */
    public static String getFileName(File file) {
        return getFileNames(file)[0];
    }

    /**
     * @deprecated 使用 {@link FilenameUtils#getExtension(java.lang.String)}
     */
    public static String getFileSuffixName(File file) {
        return getFileNames(file)[1];
    }

    /**
     * @deprecated 使用 {@link org.apache.commons.io.FilenameUtils}
     */
    public static String getFileSuffixNameAndDot(File file) {
        String suffixName = getFileNames(file)[1];
        if (!"".equals(suffixName)) {
            suffixName = "." + suffixName;
        }
        return suffixName;
    }

    public static String getRandomFileName(File file) {
        String[] fileNames = getFileNames(file);
        String fileName = fileNames[0] + ("" + System.currentTimeMillis()).substring(9);
        if (!"".equals(fileNames[1])) {
            fileName += ("." + fileNames[1]);
        }
        return fileName;
    }

    /**
     * 转换文件的大小以B,KB,M,G等计算
     *
     * @deprecated 使用 {@link FileUtils#byteCountToDisplaySize(java.math.BigInteger)}
     */
    public static String formatFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.000");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
