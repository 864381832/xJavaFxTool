package com.xwintop.xJavaFxTool.services.debugTools.socketTool;

import com.xwintop.xJavaFxTool.controller.debugTools.SocketToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslContextFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SocketToolService
 * @Description: Socket调试工具
 * @author: xufeng
 * @date: 2018/4/24 16:44
 */

@Getter
@Setter
@Slf4j
public class SocketToolService {
    private SocketToolController socketToolController;

    private IoAcceptor tcpAcceptor = null;//TCP服务端
    private IoAcceptor udpAcceptor = null;//UDP服务端
    private IoConnector connector = null;//TCP客户端连接

    public SocketToolService(SocketToolController socketToolController) {
        this.socketToolController = socketToolController;
    }

    public void serverTcpListenAction() throws Exception { //启动Tcp服务
        if (tcpAcceptor == null) {
            tcpAcceptor = new NioSocketAcceptor();
            //接着，如结构图示，在Acceptor和IoHandler之间将设置一系列的Fliter
            //包括记录过滤器和编解码过滤器。其中TextLineCodecFactory是mina自带的文本解编码器
            if (socketToolController.getServerIsUseSslCheckBox().isSelected()) {
                SslFilter sslFilter = new SslFilter(this.getSslContext(socketToolController.getServerKeyStorePathTextField().getText(), socketToolController.getServerKeyStorePasswordTextField().getText(), socketToolController.getServerTrustStoreTextField().getText(), socketToolController.getServerTrustStorePasswordTextField().getText()));
                // a.ssl过滤器，这个一定要第一个添加，否则数据不会进行加密
                tcpAcceptor.getFilterChain().addFirst("sslFilter", sslFilter);
            }
            tcpAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
            tcpAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
//        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new PrefixedStringCodecFactory(Charset.forName("UTF-8"))));
            //配置事务处理Handler，将请求转由TimeServerHandler处理。
            tcpAcceptor.setHandler(new SocketServerHandler(this));
            //配置Buffer的缓冲区大小
            tcpAcceptor.getSessionConfig().setReadBufferSize(2048);
            //设置等待时间，每隔IdleTime将调用一次handler.sessionIdle()方法
            tcpAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        }
        if (!tcpAcceptor.isActive()) {
            String url = this.socketToolController.getServerTcpUrlComboBox().getValue();
            int port = Integer.parseInt(this.socketToolController.getServerTcpPortTextField().getText().trim());
            //绑定端口
            tcpAcceptor.bind(new InetSocketAddress(InetAddress.getByName(url), port));
            writeServerLog("启动TCP服务器成功。");
            this.socketToolController.getServerTcpListenButton().setText("TCP停止");
        } else {
            tcpAcceptor.unbind();
            writeServerLog("停止TCP服务器成功。");
            this.socketToolController.getServerTcpListenButton().setText("TCP侦听");
            tcpAcceptor = null;
        }
    }

    public void serverUdpListenAction() throws Exception {
        if (udpAcceptor == null) {
            udpAcceptor = new NioDatagramAcceptor();
            //接着，如结构图示，在Acceptor和IoHandler之间将设置一系列的Fliter
            //包括记录过滤器和编解码过滤器。其中TextLineCodecFactory是mina自带的文本解编码器
            udpAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
            udpAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
//        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new PrefixedStringCodecFactory(Charset.forName("UTF-8"))));
            //配置事务处理Handler，将请求转由TimeServerHandler处理。
            udpAcceptor.setHandler(new SocketServerHandler(this));
            //配置Buffer的缓冲区大小
            udpAcceptor.getSessionConfig().setReadBufferSize(2048);
            //设置等待时间，每隔IdleTime将调用一次handler.sessionIdle()方法
            udpAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
            if (socketToolController.getServerIsUseSslCheckBox().isSelected()) {
                SslFilter sslFilter = new SslFilter(this.getSslContext(socketToolController.getServerKeyStorePathTextField().getText(), socketToolController.getServerKeyStorePasswordTextField().getText(), socketToolController.getServerTrustStoreTextField().getText(), socketToolController.getServerTrustStorePasswordTextField().getText()));
                // a.ssl过滤器，这个一定要第一个添加，否则数据不会进行加密
                udpAcceptor.getFilterChain().addFirst("sslFilter", sslFilter);
            }
        }
        if (!udpAcceptor.isActive()) {
            String url = this.socketToolController.getServerUdpUrlComboBox().getValue();
            int port = Integer.parseInt(this.socketToolController.getServerUdpPortTextField().getText().trim());
            //绑定端口
            udpAcceptor.bind(new InetSocketAddress(InetAddress.getByName(url), port));
            writeServerLog("启动UDP服务器成功。");
            this.socketToolController.getServerUdpListenButton().setText("UDP停止");
        } else {
            udpAcceptor.unbind();
            writeServerLog("停止UDP服务器成功。");
            this.socketToolController.getServerUdpListenButton().setText("UDP侦听");
            udpAcceptor = null;
        }
    }

    public void serverDataSendAction(String data) {
        List<Map<String, String>> selectedItems = this.socketToolController.getServerConnectTableView().getSelectionModel().getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) {
            writeServerLog("未选择客户端连接。");
            return;
        }
        selectedItems.forEach(stringStringMap -> {
            String connect = stringStringMap.get("connect");
            if (this.tcpAcceptor != null) {
                this.tcpAcceptor.getManagedSessions().forEach((aLong, ioSession) -> {
                    if (connect.contains(ioSession.getRemoteAddress().toString())) {
                        ioSession.write(data);
                    }
                });
            }
            if (this.udpAcceptor != null) {
                this.udpAcceptor.getManagedSessions().forEach((aLong, ioSession) -> {
                    if (connect.contains(ioSession.getRemoteAddress().toString())) {
                        ioSession.write(data);
                    }
                });
            }
        });
    }

    public void clientTcpConnectAction() throws Exception {
        if (connector == null) {
            connector = new NioSocketConnector();
            if (socketToolController.getClientIsUseSslCheckBox().isSelected()) {
                SslFilter sslFilter = new SslFilter(this.getSslContext(socketToolController.getClientKeyStorePathTextField().getText(), socketToolController.getClientKeyStorePasswordTextField().getText(), socketToolController.getClientTrustStoreTextField().getText(), socketToolController.getClientTrustStorePasswordTextField().getText()));
//                sslFilter.setUseClientMode(true);
                // a.ssl过滤器，这个一定要第一个添加，否则数据不会进行加密
                connector.getFilterChain().addFirst("sslFilter", sslFilter);
            }
            connector.getFilterChain().addLast("logger", new LoggingFilter());
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
            connector.setHandler(new SocketClientHandler(this));
            String url = this.socketToolController.getClientUrlComboBox().getValue();
            int port = Integer.parseInt(this.socketToolController.getClientPortTextField().getText().trim());
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(url, port));
            try {
                connectFuture.awaitUninterruptibly();
                IoSession session = connectFuture.getSession();
                //等待建立连接
                writeClientLog("TCP连接成功！！！");
                this.socketToolController.getClientTcpConnectButton().setText("TCP停止");
            } catch (Exception e) {
                connector = null;
                writeClientLog("TCP连接失败：" + e.getMessage());
            }
        } else {
            connector.dispose();
            connector = null;
            writeClientLog("TCP连接关闭！！！");
            this.socketToolController.getClientTcpConnectButton().setText("TCP连接");
        }
    }

    public void clientUdpConnectAction() throws Exception {
        if (connector == null) {
            connector = new NioDatagramConnector();
            if (socketToolController.getClientIsUseSslCheckBox().isSelected()) {
                SslFilter sslFilter = new SslFilter(this.getSslContext(socketToolController.getClientKeyStorePathTextField().getText(), socketToolController.getClientKeyStorePasswordTextField().getText(), socketToolController.getClientTrustStoreTextField().getText(), socketToolController.getClientTrustStorePasswordTextField().getText()));
//                sslFilter.setUseClientMode(true);
                // a.ssl过滤器，这个一定要第一个添加，否则数据不会进行加密
                connector.getFilterChain().addFirst("sslFilter", sslFilter);
            }
            connector.getFilterChain().addLast("logger", new LoggingFilter());
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
            connector.setHandler(new SocketClientHandler(this));
            String url = this.socketToolController.getClientUrlComboBox().getValue();
            int port = Integer.parseInt(this.socketToolController.getClientPortTextField().getText().trim());
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(url, port));
            try {
                connectFuture.awaitUninterruptibly();
                IoSession session = connectFuture.getSession();
                //等待建立连接
                writeClientLog("UDP连接成功！！！");
                this.socketToolController.getClientUdpConnectButton().setText("UDP停止");
            } catch (Exception e) {
                connector = null;
                writeClientLog("UDP连接失败：" + e.getMessage());
            }
        } else {
            connector.dispose();
            connector = null;
            writeClientLog("UDP连接关闭！！！");
            this.socketToolController.getClientUdpConnectButton().setText("UDP连接");
        }
    }

    public void clientDataSendAction(String data) {
        try {
            connector.getManagedSessions().values().iterator().next().write(data);
        } catch (Exception e) {
            writeClientLog("发送失败：" + e.getMessage());
        }
    }

    public void writeServerLog(String logString) {
        log.info("Server:" + logString);
        this.socketToolController.getServerLogTextArea().appendText("\n" + logString);
    }

    public void writeClientLog(String logString) {
        log.info("Client:" + logString);
        this.socketToolController.getClientLogTextArea().appendText("\n" + logString);
    }

    public static SSLContext getSslContext(String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword) throws Exception {
        SSLContext sslContext = null;
        /*
         * 提供keystore的存放目录，读取keystore的文件内容
         */
        File keyStoreFile = new File(keyStorePath);
        /*
         * 提供truststore的存放目录，读取truststore的文件内容
         */
        File trustStoreFile = new File(trustStorePath);

        if (keyStoreFile.exists() && trustStoreFile.exists()) {
            final KeyStoreFactory keyStoreFactory = new KeyStoreFactory();
            keyStoreFactory.setDataFile(keyStoreFile);
            /*
             * 这个是当初我们使用keytool创建keystore和truststore文件的密码,也是上次让你们一定要记住密码的原因了
             */
            keyStoreFactory.setPassword(keyStorePassword);
            final KeyStoreFactory trustStoreFactory = new KeyStoreFactory();
            trustStoreFactory.setDataFile(trustStoreFile);
            trustStoreFactory.setPassword(trustStorePassword);

            final SslContextFactory sslContextFactory = new SslContextFactory();
            final KeyStore keyStore = keyStoreFactory.newInstance();
            sslContextFactory.setKeyManagerFactoryKeyStore(keyStore);

            final KeyStore trustStore = trustStoreFactory.newInstance();
            sslContextFactory.setTrustManagerFactoryKeyStore(trustStore);
            sslContextFactory.setKeyManagerFactoryKeyStorePassword("123456");
            sslContext = sslContextFactory.newInstance();
        } else {
            log.error("Keystore or Truststore file does not exist");
            throw new Exception("Keystore or Truststore file does not exist");
        }
        return sslContext;
    }
}