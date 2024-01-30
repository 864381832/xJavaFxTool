package com.xwintop.xJavaFxTool.services.debugTools.socketTool;

import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

@Getter
@Setter
@Slf4j
public class SocketClientHandler extends IoHandlerAdapter {
    private SocketToolService socketToolService;

    public SocketClientHandler(SocketToolService socketToolService) {
        this.socketToolService = socketToolService;
    }

    @Override
    public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
        arg1.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession arg0, Object message) throws Exception {
        log.info("client接受信息:" + message.toString());
        socketToolService.writeClientLog("接收服务器信息:" + message.toString());
    }

    @Override
    public void messageSent(IoSession arg0, Object message) throws Exception {
        log.info("client发送信息" + message.toString());
        socketToolService.writeClientLog("往服务器发送:" + message.toString());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("client与:" + session.getRemoteAddress().toString() + "断开连接");
        socketToolService.setConnector(null);
        socketToolService.writeClientLog(session.getRemoteAddress().toString() + "断开连接");
        Platform.runLater(()->{
            socketToolService.getSocketToolController().getClientTcpConnectButton().setText("TCP连接");
            socketToolService.getSocketToolController().getClientUdpConnectButton().setText("UDP连接");
        });
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.info("client与:" + session.getRemoteAddress().toString() + "建立连接");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.info("IDLE " + session.getIdleCount(status));
    }

    @Override
    public void sessionOpened(IoSession arg0) throws Exception {
        log.info("打开连接");
    }

}
