package com.xwintop.xJavaFxPlugIn.services.developTools;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xwintop.xJavaFxPlugIn.controller.developTools.ScanPortToolController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.*;
import java.util.*;

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

    //开始扫描端口
    public void scanAction() throws Exception {
        List<String> portsList = new ArrayList<>();
        for (Node child : scanPortToolController.getCommonPortFlowPane().getChildren()) {
            CheckBox checkBox = (CheckBox) child;
            if (checkBox.isSelected()) {
                addPort(checkBox.getText(), portsList);
            }
        }
        String diyPortString = scanPortToolController.getDiyPortTextField().getText();
        if (StringUtils.isNotEmpty(diyPortString)) {
            addPort(diyPortString, portsList);
        }
        String[] hosts = scanPortToolController.getHostTextField().getText().split(",");
        hosts = ArrayUtils.removeElements(hosts, scanPortToolController.getIpFilterTextField().getText().split(","));
        List<String> portFilters = Arrays.asList(scanPortToolController.getPortFilterTextField().getText().split(","));
        portsList.removeAll(portFilters);
//        log.info(portsList.toString());
        scanPortToolController.getConnectStatusTableData().clear();
        for (String host : hosts) {
            InetAddress address = InetAddress.getByName(host); // 如果输入的是主机名，尝试获取ip地址
            for (String port : portsList) {
                ThreadUtil.execute(() -> {
                    Map<String, String> dataRow = new HashMap<String, String>();
                    dataRow.put("ip", host);
                    dataRow.put("port", port);
                    try {
                        Socket socket = new Socket();// 定义套接字
                        SocketAddress socketAddress = new InetSocketAddress(address, Integer.valueOf(port));// 传递ip和端口
                        socket.connect(socketAddress, 1000);
                        // 对目标主机的指定端口进行连接，超时后连接失败
                        socket.close();
                        // 关闭端口
                        dataRow.put("status", "开放");
                        // 在文本区域里更新消息
                    } catch (Exception e) {
                        dataRow.put("status", "关闭");
                    }
                    Platform.runLater(() -> {
                        scanPortToolController.getConnectStatusTableData().add(dataRow);
                    });
                });
            }
        }
    }

    //添加端口号
    private void addPort(String portString, List<String> portsList) {
        String[] ports = portString.split(",");
        for (String port : ports) {
            if (port.contains(":")) {
                portsList.add(port.split(":")[1]);
            } else if (port.contains("-")) {
                Integer startPort = Integer.valueOf(port.split("-")[0]);
                Integer entPort = Integer.valueOf(port.split("-")[1]);
                for (int i = startPort; i <= entPort; i++) {
                    portsList.add(Integer.toString(i));
                }
            } else {
                portsList.add(port);
            }
        }
    }

    //解析域名对应ip
    public void parseDomainAction() {
        String[] hosts = scanPortToolController.getHostTextField().getText().split(",");
        List<String> domainIps = new ArrayList<>();
        for (String host : hosts) {
            try {
                InetAddress address = InetAddress.getByName(host);
                domainIps.add(address.getHostAddress());
            } catch (UnknownHostException e) {
                log.error("解析域名失败！", e);
            }
        }
        scanPortToolController.getDomainIpTextField().setText(String.join(",", domainIps));
    }

    //获取外网对应ip
    public void getNatIpAddressAction() {
        String jsonString = HttpUtil.get("https://ip.cn/api/index?ip=&type=0");
        JSONObject jsonObject = JSON.parseObject(jsonString);
        scanPortToolController.getNatIpTextField().setText(jsonObject.getString("ip"));
        scanPortToolController.getNatIpAddressTextField().setText(jsonObject.getString("address"));
    }
}
