package com.xwintop.xTransfer.messaging;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @ClassName: IMessage
 * @Description: 传输消息封装
 * @author: xufeng
 * @date: 2018/6/13 16:13
 */

public interface IMessage extends Cloneable, Serializable {
    String getId();

    void setId(String var1);

    String getEncoding();

    void setEncoding(String var1);

    String getMessageByString() throws UnsupportedEncodingException;

    byte[] getMessage();

    byte[] getMessage(String var1) throws UnsupportedEncodingException;

    void setMessage(byte[] var1);

    long getCreateTimestamp();

    void setCreateTimestamp(long var1);

    void setFileName(String fileName);

    String getFileName();

    Properties getProperties();

    Object getProperty(String key);

    void setProperty(String key, Object value);

    byte[] getRawData();

    byte[] getRawData(String var1) throws UnsupportedEncodingException;

    void setRawData(byte[] var1);

    String getRawDataByString() throws UnsupportedEncodingException;

    String getVersion();

    void setVersion(String var1);

    Object clone() throws CloneNotSupportedException;

    void addFileNameFilterRegexGroup(String fileNameFilterRegexGroup);

    boolean checkFileNameFilterRegexGroup(String fileNameFilterRegexGroup);
}
