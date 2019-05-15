package com.xwintop.xJavaFxTool.services.developTools;

import cn.hutool.core.thread.ThreadUtil;
import com.xwintop.xJavaFxTool.controller.developTools.ScanPortToolController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ScanPortToolService
 * @Description: 端口扫描工具
 * @author: xufeng
 * @date: 2019/5/15 17:34
 */

@Getter
@Setter
@Slf4j
public class ScanPortToolService {
    private ScanPortToolController scanPortToolController;

    public ScanPortToolService(ScanPortToolController scanPortToolController) {
        this.scanPortToolController = scanPortToolController;
    }

    public void scanAction() throws Exception {
        List<Integer> portsList = new ArrayList<>();
        for (Node child : scanPortToolController.getCommonPortFlowPane().getChildren()) {
            CheckBox checkBox = (CheckBox) child;
            if (checkBox.isSelected()) {
                String[] ports = checkBox.getText().split(",");
                for (String port : ports) {
                    if (port.contains(":")) {
                        portsList.add(Integer.valueOf(port.split(":")[1]));
                    } else {
                        portsList.add(Integer.valueOf(port));
                    }
                }
            }
        }
        String diyPortString = scanPortToolController.getDiyPortTextField().getText();
        if (StringUtils.isNotEmpty(diyPortString)) {
            for (String port : diyPortString.split(",")) {
                portsList.add(Integer.valueOf(port));
            }
        }
        log.info(portsList.toString());
        String host = scanPortToolController.getHostTextField().getText();
        InetAddress address = InetAddress.getByName(host); // 如果输入的是主机名，尝试获取ip地址
        for (Integer integer : portsList) {
            ThreadUtil.execute(() -> {
                try {
                    Socket socket = new Socket();// 定义套接字
                    SocketAddress socketAddress = new InetSocketAddress(address, integer);// 传递ip和端口
                    socket.connect(socketAddress, 1000);
                    // 对目标主机的指定端口进行连接，超时后连接失败
                    socket.close();
                    // 关闭端口
                    log.info("端口 " + integer + " ：开放");
                    Platform.runLater(() -> {
                        scanPortToolController.getConnectLogTextArea().appendText("端口 " + integer + " ：开放\n");
                    });
                    // 在文本区域里更新消息
                } catch (Exception e) {
                    log.info("端口 " + integer + " ：关闭");
                    Platform.runLater(() -> {
                        // 产生异常表示端口关闭
                        scanPortToolController.getConnectLogTextArea().appendText("端口 " + integer + " ：关闭\n");
                    });
                }
            });
        }
    }
}