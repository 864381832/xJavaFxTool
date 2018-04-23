package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.SocketToolController;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class SocketToolService {
    private SocketToolController socketToolController;

    //TCP使用DatagramSocket发送数据包
    private ServerSocket tcpServerSocket = null;
    private List<Socket> tcpServerSocketConnectList = new ArrayList<>();
    private Thread tcpServerSocketThread = null;
    //UDP使用DatagramSocket发送数据包
    private DatagramSocket udpServerSocket = null;
    //客户端TCP连接
    private Socket clientSocket = null;

    public SocketToolService(SocketToolController socketToolController) {
        this.socketToolController = socketToolController;
    }

    public void serverTcpListenAction() throws Exception { //启动Tcp服务
        if (tcpServerSocket == null) {
            String url = this.socketToolController.getServerTcpUrlComboBox().getValue();
            int port = Integer.parseInt(this.socketToolController.getServerTcpPortTextField().getText().trim());
            InetAddress serverAddr = InetAddress.getByName(url);
            tcpServerSocket = new ServerSocket(port, 1000, serverAddr);
            //死循环，一直运行服务器
            tcpServerSocketThread = new Thread(() -> {
                try {
                    log.info("启动服务器监听");
                    while (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
                        //调用accept（）方法侦听，等待客户端的连接以获取Socket实例
                        Socket socket = tcpServerSocket.accept();
                        InputStream is = socket.getInputStream();
                        writeServerLog("接收：" + IOUtils.toString(is, "utf-8"));
                        //获取输出流，响应客户端的请求
//                        OutputStream os = socket.getOutputStream();
//                        IOUtils.write("回发" + System.currentTimeMillis(), os, "utf-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            tcpServerSocketThread.start();
            writeServerLog("启动TCP服务器成功。");
            this.socketToolController.getServerTcpListenButton().setText("TCP停止");
        } else {
            tcpServerSocket.close();
            tcpServerSocket = null;
//            tcpServerSocketThread.stop();
            writeServerLog("停止TCP服务器成功。");
            this.socketToolController.getServerTcpListenButton().setText("TCP侦听");
        }
    }

    public void serverUdpListenAction() throws Exception {
    }

    public void serverDataSendAction(String data) {

    }

    public void serverClearLogAction() throws Exception {
        this.socketToolController.getServerLogTextArea().clear();
    }

    public void clientTcpConnectAction() throws Exception {
        if (clientSocket == null) {
//            new Thread(() -> {
            try {
                String url = this.socketToolController.getClientUrlComboBox().getValue();
                int port = Integer.parseInt(this.socketToolController.getClientPortTextField().getText().trim());
                //创建客户端Socket，指定服务器地址和端口
                clientSocket = new Socket(url, port);
                writeClientLog("TCP连接成功！！！");
                this.socketToolController.getClientTcpConnectButton().setText("TCP停止");
            } catch (Exception e) {
                writeClientLog(e.getMessage());
            }
//            }).start();
        } else {
            clientSocket.close();
            clientSocket = null;
            writeClientLog("TCP连接关闭！！！");
            this.socketToolController.getClientTcpConnectButton().setText("TCP连接");
        }

    }

    public void clientUdpConnectAction() throws Exception {
    }

    public void clientDataSendAction(String data) {
        try {
            //建立连接后，获取输出流，向服务器端发送信息
            OutputStream os = clientSocket.getOutputStream();
            IOUtils.write(data, os, "UTF-8");
            writeClientLog("往服务器发送:" + data);
//            clientSocket.shutdownOutput();//关闭输出流
        } catch (Exception e) {
            writeClientLog(e.getMessage());
        }
    }

    public void clientClearLogAction() throws Exception {
        this.socketToolController.getClientLogTextArea().clear();
    }

    private void writeServerLog(String logString) {
        log.info("Server:" + logString);
        this.socketToolController.getServerLogTextArea().appendText("\n" + logString);
    }

    private void writeClientLog(String logString) {
        log.info("Client:" + logString);
        this.socketToolController.getClientLogTextArea().appendText("\n" + logString);
    }
}