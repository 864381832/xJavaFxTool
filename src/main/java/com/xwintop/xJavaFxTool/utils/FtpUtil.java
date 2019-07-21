package com.xwintop.xJavaFxTool.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Locale;

@Data
@Slf4j
public class FtpUtil {
    private FTPClient ftp;
    private String defaultDirectory;
    private String host;
    private int port;
    private String user;
    private String passwd;
    private int timeout;
    private boolean passive;
    private boolean binary;
    private String encoding;
    private int socketTimeout;
    private boolean longConnection;
    private boolean isFtps = false;
    private Integer fileType; //文件传输方式（设置除二进制、ACSII方式之外的传输方式）
    private Integer bufferSize; //缓冲数据流缓冲区大小

    private boolean implicit = false;//The security mode. (True - Implicit Mode / False - Explicit Mode)（ftps独有）
    private String protocol = "TLS";//The secure socket protocol to be used, e.g. SSL/TLS.（ftps独有）
    private String prot = "P";//数据通道保护等级（ftps独有）
    private boolean checkServerValidity = false;//是否检测服务器证书有效性（ftps独有）

    public FtpUtil() {
    }

    public FtpUtil(String host, int port, String user, String passwd) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.passwd = passwd;
    }

    public FtpUtil(String host, int port, String user, String passwd, int timeout, boolean passive, boolean binary, String encoding, int socketTimeout, boolean longConnection) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.passwd = passwd;
        this.timeout = timeout;
        this.passive = passive;
        this.binary = binary;
        this.encoding = encoding;
        this.socketTimeout = socketTimeout;
        this.longConnection = longConnection;
    }

    public void checkAndConnect() throws Exception {
        if (ftp != null) {
            if (ftp.isConnected()) {
                try {
                    ftp.noop();
                } catch (IOException e) {
                    connect();
                }
                return;
            }
        }
        connect();
    }

    public void checkAndDisconnect() throws Exception {
        // do not disconnect if the connection mode is long connection.
        if (longConnection) {
            ftp.changeWorkingDirectory(defaultDirectory);
            return;
        }
        if (ftp != null) {
            ftp.disconnect();
        }
    }

    public void destroy() {
        if (ftp != null) {
            try {
                ftp.quit();
                log.info("quit ftp........");
            } catch (Exception e) {
                log.warn("quit ftp fail: " + e.getMessage());
            }
            try {
                ftp.disconnect();
                log.info("disconnect ftp........");
            } catch (Exception e) {
                log.warn("disconnect ftp fail:" + e.getMessage());
            }
            ftp = null;
        }
    }

    public void connect() throws Exception {
        if (ftp == null) {
            if (isFtps) {
                ftp = new FTPSClient(this.protocol, this.implicit) {
                    @Override
                    protected void _prepareDataSocket_(final Socket socket) throws IOException {
                        if (socket instanceof SSLSocket) {
                            // Control socket is SSL
                            final SSLSession session = ((SSLSocket) _socket_).getSession();
                            final SSLSessionContext context = session.getSessionContext();
                            //context.setSessionCacheSize(preferences.getInteger("ftp.ssl.session.cache.size"));
                            try {
                                final Field sessionHostPortCache = context.getClass().getDeclaredField("sessionHostPortCache");
                                sessionHostPortCache.setAccessible(true);
                                final Object cache = sessionHostPortCache.get(context);
                                final Method method = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                                method.setAccessible(true);
                                final String key = String.format("%s:%s", socket.getInetAddress().getHostName(), String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                                method.invoke(cache, key, session);
                            } catch (NoSuchFieldException e) {
                                // Not running in expected JRE
                                log.warn("No field sessionHostPortCache in SSLSessionContext", e);
                            } catch (Exception e) {
                                // Not running in expected JRE
                                log.warn(e.getMessage());
                            }
                        }
                    }
                };
                if (checkServerValidity) {
                    ((FTPSClient) ftp).setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
                }
            } else {
                ftp = new FTPClient();
            }
        }
        if (encoding != null && !"AUTO".equalsIgnoreCase(encoding)) {
            ftp.setControlEncoding(encoding);
        }
        if (socketTimeout > 0) {
            ftp.setDefaultTimeout(socketTimeout);
        }
        // connect to the ftp server and check the connection
        ftp.connect(host, port);
        ftp.setDataTimeout(timeout);
        checkAndThrow();
        // login the server
        if (!ftp.login(user, passwd)) {
            log.error("login error. user:" + user + "  passwd:" + passwd);
            throw new Exception("Server reply:" + ftp.getReplyString());
        } else {
            log.info("success login,current time is:" + new java.sql.Timestamp(System.currentTimeMillis()).toString());
        }
        if (isFtps) {
            ((FTPSClient) ftp).execPBSZ(0);
            ((FTPSClient) ftp).execPROT(prot);
        }
        // set the connect mode. The default is port.
        if (passive) {
            ftp.enterLocalPassiveMode();
            log.debug(ftp.getReplyString());
        }
        // set the translation mode. The default is ASCII.
        if (binary) {
            if (!ftp.setFileType(FTP.BINARY_FILE_TYPE)) {
                log.error("Failed to change file transfer type to binary.Use the default transfer type.");
            }
            log.debug(ftp.getReplyString());
        }
        if (fileType != null) {
            if (!ftp.setFileType(fileType)) {
                log.error("Failed to change file transfer type to binary.Use the default transfer type.");
            }
        }
        if (bufferSize != null) {
            ftp.setBufferSize(bufferSize);
        }
        checkAndThrow();
        defaultDirectory = ftp.printWorkingDirectory();
    }

    public void checkAndThrow() throws Exception {
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            throw new Exception("Server reply:" + ftp.getReplyString());
        }
    }

    public void makeDirs(String path) {
        String pp[] = path.split("/");
        String pathname = "";
        try {
            for (String p : pp) {
                log.debug("current path is:" + p);
                pathname = pathname + p + "/";
                if (!ftp.changeWorkingDirectory(pathname)) {
                    if (ftp.makeDirectory(pathname)) {
                        log.info("success create path:" + pathname);
                    } else {
                        log.info("failed create path:" + pathname);
                    }
                }
            }
            ftp.changeWorkingDirectory(defaultDirectory);
        } catch (Exception exc) {
            log.error("makeDirs error." + exc);
        }
    }

    public void changeStringDirectory(String tmp) throws Exception {
        this.checkAndConnect();
        if (!this.changeWorkingDirectory(tmp)) {
            this.makeDirs(tmp);
        }
        this.checkAndThrow();
    }

    /**
     * @param fileName 文件名
     * @Description: 下载文件
     */
    public byte[] downFile(String fileName) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (ftp.retrieveFile(fileName, os)) {
                return os.toByteArray();
            } else {
                throw new Exception("Reading file occurs error. " + ftp.getReplyString());
            }
        } finally {
            try {
                os.flush();
                os.close();
            } catch (Exception e) {
                log.error("Stream close error.", e);
            }
        }
    }

    /**
     * @param fileName
     * @param messageByte
     * @return
     * @Description: 上传文件
     */
    public boolean uploadFile(String fileName, byte[] messageByte) throws Exception {
        boolean isSuccess = false;
        ByteArrayInputStream bais = new ByteArrayInputStream(messageByte);
        try {
            if (ftp.storeFile(fileName, bais)) {
                isSuccess = true;
            }
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    log.error("Stream close error.", e);
                }
            }
        }
        return isSuccess;
    }

    public boolean changeWorkingDirectory() throws IOException {
        return ftp.changeWorkingDirectory(defaultDirectory);
    }

    public boolean changeWorkingDirectory(String pathname) throws IOException {
        return ftp.changeWorkingDirectory(pathname);
    }

    public boolean deleteFile(String fileName) throws IOException {
        return ftp.deleteFile(fileName);
    }

    public boolean rename(String from, String to) throws IOException {
        return ftp.rename(from, to);
    }

    public boolean removeDirectory(String fileName) throws IOException {
        return ftp.removeDirectory(fileName);
    }

}
