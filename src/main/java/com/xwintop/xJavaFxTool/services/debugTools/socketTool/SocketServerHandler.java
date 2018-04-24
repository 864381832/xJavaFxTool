package com.xwintop.xJavaFxTool.services.debugTools.socketTool;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @ClassName: SocketServerHandler
 * @Description: Socket服务Handler
 * @author: xufeng
 * @date: 2018/4/24 15:02
 */
@Getter
@Setter
@Slf4j
public class SocketServerHandler extends IoHandlerAdapter {
    private SocketToolService socketToolService;

    public SocketServerHandler(SocketToolService socketToolService) {
        this.socketToolService = socketToolService;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String str = message.toString();
        log.info("接受到的消息:" + str);
        this.socketToolService.writeServerLog("接受到的消息:" + str);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.info("发送信息:" + message.toString());
        this.socketToolService.writeServerLog("向 "+session.getRemoteAddress().toString()+" 发送数据："+message.toString());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        List<Map<String, String>> serverConnectTableData = socketToolService.getSocketToolController().getServerConnectTableData();
        for (int i = 0; i < serverConnectTableData.size(); i++) {
            if(serverConnectTableData.get(i).get("connect").contains(session.getRemoteAddress().toString())){
                serverConnectTableData.remove(i);
                break;
            }
        }
        log.info("IP:" + session.getRemoteAddress().toString() + "断开连接");
        socketToolService.writeServerLog("断开连接："+session.getRemoteAddress().toString());
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("connect", session.getRemoteAddress().toString());
        socketToolService.getSocketToolController().getServerConnectTableData().add(stringStringMap);
//        log.info("添加连接: " + session.getRemoteAddress().toString());
        socketToolService.writeServerLog("添加连接："+session.getRemoteAddress().toString());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
//        log.info("IDLE: " + session.getRemoteAddress().toString() + " : " + session.getIdleCount(status));
    }

    @Override
    public void sessionOpened(IoSession arg0) throws Exception {
        log.info("sessionOpened IP:" + arg0.getRemoteAddress().toString());
    }
}