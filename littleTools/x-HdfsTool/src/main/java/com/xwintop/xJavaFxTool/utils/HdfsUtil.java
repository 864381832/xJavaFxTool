package com.xwintop.xJavaFxTool.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;

/**
 * @ClassName: HdfsUtil
 * @Description: 工具类
 * @author: xufeng
 * @date: 2018/6/12 14:21
 */

@Slf4j
@Data
public class HdfsUtil {

    private Configuration hadoopConf = new Configuration();
    private FileSystem fileSystem;

    public HdfsUtil() {

    }

    public HdfsUtil(Configuration configuration) throws IOException {
        this.hadoopConf = configuration;
        // 这个解决hdfs问题
        hadoopConf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        // 这个解决本地file问题
        hadoopConf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        fileSystem = FileSystem.get(hadoopConf);
    }

    public void close() throws Exception {
        fileSystem.close();
    }

    /**
     * @param filePath         目录文件
     * @param isCreatePathFlag 不存在时是否自动创建
     * @Description: 检查是否存在目录
     */
    public boolean checkIsHaveDir(String filePath, boolean isCreatePathFlag) throws Exception {
        if (!fileSystem.exists(new Path(filePath))) {
            if (isCreatePathFlag) {
                log.warn("configuration path:" + filePath + " isn't exist.now create it.");
                if (fileSystem.mkdirs(new Path(filePath))) {
                    log.info("success create path:" + filePath);
                } else {
                    throw new Exception("can't create path:" + filePath);
                }
            } else {
                throw new Exception("configuration for path is error,path:" + filePath);
            }
        }
        if (!fileSystem.isDirectory(new Path(filePath))) {
            return false;
        }
        return true;
    }

    /**
     * @param filePath         目录文件
     * @param isCreatePathFlag 不存在时是否自动创建
     * @Description: 检查是否存在目录
     */
    public boolean checkIsHaveDir(Path filePath, boolean isCreatePathFlag) throws Exception {
        if (!fileSystem.exists(filePath)) {
            if (isCreatePathFlag) {
                log.warn("configuration path:" + filePath + " isn't exist.now create it.");
                if (fileSystem.mkdirs(filePath)) {
                    log.info("success create path:" + filePath);
                } else {
                    throw new Exception("can't create path:" + filePath);
                }
            } else {
                throw new Exception("configuration for path is error,path:" + filePath);
            }
        }
        if (!fileSystem.isDirectory(filePath)) {
            return false;
        }
        return true;
    }

    /**
     * @param filePath 文件
     * @param
     * @Description: 检查是否存在目录
     */
    public static boolean checkIsHaveFile(String filePath, FileSystem fs) throws Exception {
        if (!fs.exists(new Path(filePath))) {
            return false;
        }
        return true;
    }

    public FileStatus[] getFileList(String path) throws Exception {
        FileStatus[] fs = fileSystem.listStatus(new Path(path));
        return fs;
    }

    public boolean exists(String path) throws Exception {
        return fileSystem.exists(new Path(path));
    }

    public boolean deleteFile(String path) throws Exception {
        return fileSystem.delete(new Path(path), true);
    }

    public byte[] getFile(String path) throws Exception {
        FSDataInputStream is = fileSystem.open(new Path(path));
        return IOUtils.toByteArray(is);
    }

    public void downloadFile(String path, String outPath) throws Exception {
        FSDataInputStream is = fileSystem.open(new Path(path));
        FileOutputStream out = new FileOutputStream(outPath);
        org.apache.hadoop.io.IOUtils.copyBytes(is, out, 4096, true);
    }

    public boolean mkdirs(String path) throws Exception {
        return fileSystem.mkdirs(new Path(path));
    }

    public void uploadFile(String path, File file) throws Exception {
        InputStream in = new FileInputStream(file);
        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(path), true);//后面的参数，是覆盖
        org.apache.hadoop.io.IOUtils.copyBytes(in, fsDataOutputStream, 4096, true);
    }

    //上传文件夹
    public void uploadDir(String path, File file) throws Exception {
        mkdirs(path);
        try (
                DirectoryStream<java.nio.file.Path> directoryStream = Files.newDirectoryStream(file.toPath())
        ) {
            for (java.nio.file.Path pathDir : directoryStream) {
                File fileDir = pathDir.toFile();
                if (fileDir.isDirectory()) {
                    uploadDir(path + "/" + fileDir.getName(), fileDir);
                } else {
                    InputStream in = new FileInputStream(fileDir);
                    FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(path + "/" + fileDir.getName()), true);//后面的参数，是覆盖
                    org.apache.hadoop.io.IOUtils.copyBytes(in, fsDataOutputStream, 4096, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean rename(String path, String path1) throws Exception {
        return fileSystem.rename(new Path(path), new Path(path1));
    }

}
