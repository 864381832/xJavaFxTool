package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.services.debugTools.socketTool.SocketToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.SocketToolView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: SocketToolController
 * @Description: Socket调试工具
 * @author: xufeng
 * @date: 2018/4/24 16:45
 */

@Getter
@Setter
@Slf4j
public class SocketToolController extends SocketToolView {
    private SocketToolService socketToolService = new SocketToolService(this);
    private ObservableList<Map<String, String>> serverConnectTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initView();
            initEvent();
            initService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() throws Exception {
        serverTcpUrlComboBox.getItems().add("127.0.0.1");
        serverUdpUrlComboBox.getItems().add("127.0.0.1");
        clientUrlComboBox.getItems().add("127.0.0.1");
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        NetworkIF[] networkIFs = hal.getNetworkIFs();
        for (NetworkIF networkIF : networkIFs) {
            if (ArrayUtils.getLength(networkIF.getIPv4addr()) > 0) {
                String address = networkIF.getIPv4addr()[0];
                serverTcpUrlComboBox.getItems().add(address);
                serverUdpUrlComboBox.getItems().add(address);
                clientUrlComboBox.getItems().add(address);
            }
        }

        serverTcpUrlComboBox.getSelectionModel().select(0);
        serverUdpUrlComboBox.getSelectionModel().select(0);
        clientUrlComboBox.getSelectionModel().select(0);
        JavaFxViewUtil.setTableColumnMapValueFactory(serverConnectTableColumn, "connect", false);
        serverConnectTableView.setItems(serverConnectTableData);
        serverConnectTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initEvent() {
        serverDataSend1Button.setOnMouseClicked(event -> {
            socketToolService.serverDataSendAction(serverDataSend1TextField.getText());
        });
        serverDataSend2Button.setOnMouseClicked(event -> {
            socketToolService.serverDataSendAction(serverDataSend2TextField.getText());
        });
        serverDataSend3Button.setOnMouseClicked(event -> {
            socketToolService.serverDataSendAction(serverDataSend3TextField.getText());
        });
        clientDataSend1Button.setOnMouseClicked(event -> {
            socketToolService.clientDataSendAction(clientDataSend1TextField.getText());
        });
        clientDataSend2Button.setOnMouseClicked(event -> {
            socketToolService.clientDataSendAction(clientDataSend2TextField.getText());
        });
        clientDataSend3Button.setOnMouseClicked(event -> {
            socketToolService.clientDataSendAction(clientDataSend3TextField.getText());
        });
        serverConnectTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && !serverConnectTableData.isEmpty()) {
                MenuItem menu_Remove = new MenuItem("断开选中连接");
                menu_Remove.setOnAction(event1 -> {
                    if (socketToolService.getTcpAcceptor() != null) {
                        socketToolService.getTcpAcceptor().getManagedSessions().forEach((aLong, ioSession) -> {
                            serverConnectTableView.getSelectionModel().getSelectedItems().forEach(stringStringMap -> {
                                if (stringStringMap.get("connect").contains(ioSession.getRemoteAddress().toString())) {
                                    ioSession.closeNow();
                                }
                            });
                        });
                    }
                    if (socketToolService.getUdpAcceptor() != null) {
                        socketToolService.getUdpAcceptor().getManagedSessions().forEach((aLong, ioSession) -> {
                            serverConnectTableView.getSelectionModel().getSelectedItems().forEach(stringStringMap -> {
                                if (stringStringMap.get("connect").contains(ioSession.getRemoteAddress().toString())) {
                                    ioSession.closeNow();
                                }
                            });
                        });
                    }
                    serverConnectTableData.removeAll(serverConnectTableView.getSelectionModel().getSelectedItems());
                });
                MenuItem menu_RemoveAll = new MenuItem("断开所有连接");
                menu_RemoveAll.setOnAction(event1 -> {
                    if (socketToolService.getTcpAcceptor() != null) {
                        socketToolService.getTcpAcceptor().getManagedSessions().forEach((aLong, ioSession) -> {
                            ioSession.closeNow();
                        });
                    }
                    if (socketToolService.getUdpAcceptor() != null) {
                        socketToolService.getUdpAcceptor().getManagedSessions().forEach((aLong, ioSession) -> {
                            ioSession.closeNow();
                        });
                    }
                    serverConnectTableData.clear();
                });
                serverConnectTableView.setContextMenu(new ContextMenu(menu_Remove, menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void serverTcpListenAction(ActionEvent event) throws Exception {
        socketToolService.serverTcpListenAction();
    }

    @FXML
    private void serverUdpListenAction(ActionEvent event) throws Exception {
        socketToolService.serverUdpListenAction();
    }

    @FXML
    private void serverClearLogAction(ActionEvent event) throws Exception {
        serverLogTextArea.clear();
    }

    @FXML
    private void clientTcpConnectAction(ActionEvent event) throws Exception {
        socketToolService.clientTcpConnectAction();
    }

    @FXML
    private void clientUdpConnectAction(ActionEvent event) throws Exception {
        socketToolService.clientUdpConnectAction();
    }

    @FXML
    private void clientClearLogAction(ActionEvent event) throws Exception {
        clientLogTextArea.clear();
    }
}