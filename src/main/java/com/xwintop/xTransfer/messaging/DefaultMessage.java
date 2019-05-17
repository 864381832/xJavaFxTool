package com.xwintop.xTransfer.messaging;

import com.xwintop.xcore.util.UuidUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: DefaultMessage
 * @Description: 传输消息封装默认实现类
 * @author: xufeng
 * @date: 2018/6/13 16:13
 */

public class DefaultMessage implements IMessage, Serializable {
    private static final long serialVersionUID = -3515372818719874220L;
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String FILE_NAME = "fileName";

    private String id = null;
    private String encoding = null;
    private byte[] message = null;
    private byte[] rawData = null;
    private long createTimestamp = 0L;
    private Properties properties = null;
    private String version = null;

    private Map<String, Object> fileNameFilterRegexGroupMap;

    public DefaultMessage() {
        this.init();
    }

    public DefaultMessage(byte[] rawData, String encoding) {
        this.init();
        this.rawData = rawData;
        this.encoding = encoding;
    }

    private void init() {
        if (this.encoding == null) {
            this.encoding = DEFAULT_ENCODING;
        }
        this.properties = new Properties();
        this.createTimestamp = System.currentTimeMillis();
        this.id = UuidUtil.get32UUID();
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getMessageByString() throws UnsupportedEncodingException {
        return this.message == null ? new String(this.rawData, this.encoding) : new String(this.message, this.encoding);
    }

    @Override
    public byte[] getMessage() {
        return this.message == null ? this.rawData : this.message;
    }

    @Override
    public void setMessage(byte[] data) {
        this.message = data;
    }

    @Override
    public long getCreateTimestamp() {
        return this.createTimestamp;
    }

    @Override
    public void setCreateTimestamp(long ts) {
        this.createTimestamp = ts;
    }

    @Override
    public void setFileName(String fileName) {
        this.properties.setProperty(FILE_NAME, fileName);
    }

    @Override
    public String getFileName() {
        return this.properties.getProperty(FILE_NAME);
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    @Override
    public byte[] getRawData() {
        return this.rawData;
    }

    @Override
    public void setRawData(byte[] data) {
        this.rawData = data;
    }

    @Override
    public String getRawDataByString() throws UnsupportedEncodingException {
        return this.rawData == null ? null : new String(this.rawData, this.encoding);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultMessage c = new DefaultMessage();
        c.setEncoding(this.getEncoding());
        c.setFileName(this.getFileName());
        Properties p = c.getProperties();
        Iterator i = this.getProperties().keySet().iterator();

        while (i.hasNext()) {
            String key = (String) i.next();
            p.put(key, this.getProperties().get(key));
        }

        byte[] m = this.getMessage();
        if (m != null) {
            c.setMessage((byte[]) ((byte[]) m.clone()));
        }

        byte[] rd = this.getRawData();
        if (rd != null) {
            c.setRawData((byte[]) ((byte[]) rd.clone()));
        }

        return c;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public byte[] getMessage(String encoding) throws UnsupportedEncodingException {
        if (encoding != null && !encoding.equals(this.encoding)) {
            String str = this.getMessageByString();
            return str != null ? str.getBytes(encoding) : null;
        } else {
            return this.getMessage();
        }
    }

    @Override
    public byte[] getRawData(String encoding) throws UnsupportedEncodingException {
        if (encoding != null && !encoding.equals(this.encoding)) {
            String str = this.getRawDataByString();
            return str != null ? str.getBytes(encoding) : null;
        } else {
            return this.getRawData();
        }
    }

    @Override
    public boolean checkFileNameFilterRegexGroup(String fileNameFilterRegexGroup) {
        if (this.fileNameFilterRegexGroupMap == null) {
            return false;
        }
        return this.fileNameFilterRegexGroupMap.get(fileNameFilterRegexGroup) != null;
    }

    @Override
    public void addFileNameFilterRegexGroup(String fileNameFilterRegexGroup) {
        if (this.fileNameFilterRegexGroupMap == null) {
            this.fileNameFilterRegexGroupMap = new HashMap<>();
        }
        this.fileNameFilterRegexGroupMap.put(fileNameFilterRegexGroup, true);
    }
}
