package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolController;
import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataTableController;
import com.xwintop.xcore.util.RedisUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class RedisToolService {

    private RedisToolController redisToolController;
    private Map<String, RedisUtil> jedisMap = new HashMap<String, RedisUtil>();
    private Map<String, Tab> dataTableTabMap = new HashMap<String, Tab>();

    public RedisToolService(RedisToolController redisToolController) {
        this.redisToolController = redisToolController;
    }

    public void addServerMenuItem() {
        MenuItem menu_addServer = new MenuItem("添加服务器");
        menu_addServer.setOnAction(event1 -> {
            int row = 0;
            GridPane page1Grid = new GridPane();
            page1Grid.setVgap(10);
            page1Grid.setHgap(10);

            TextField txName = createTextField(null, page1Grid, "Name:", row++);
            TextField txHost = createTextField("localhost", page1Grid, "Host:", row++);
            TextField txPort = createTextField("6379", page1Grid, "Port:", row++);
            TextField txPassword = createTextField(null, page1Grid, "Password:", row++);

            Alert alert = new Alert(Alert.AlertType.NONE, null, new ButtonType("取消", ButtonBar.ButtonData.NO),
                    new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("添加服务器");
            alert.setGraphic(page1Grid);
            alert.setWidth(200);
            Optional<ButtonType> _buttonType = alert.showAndWait();
            // 根据点击结果返回
            if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                if (StringUtils.isEmpty(txName.getText()) || StringUtils.isEmpty(txHost.getText())
                        || StringUtils.isEmpty(txPort.getText()))
                {
                    TooltipUtil.showToast("请输入服务器信息");
                    return;
                }
                addServiceAddress(txName.getText(), txHost.getText(), Integer.parseInt(txPort.getText()),
                        txPassword.getText());
            }
        });
        MenuItem menu_Refresh = new MenuItem("刷新");
        menu_Refresh.setOnAction(event1 -> {
            reloadServiceAddress();
        });
        redisToolController.getRedisServiceTreeView().setContextMenu(new ContextMenu(menu_addServer, menu_Refresh));
    }

    public void addServiceAddress(String name, String host, int port, String password) {
        try {
            RedisUtil redisUtil = new RedisUtil(name, host, port, password);
            // Jedis jedis = new Jedis("localhost", 6379);
            jedisMap.put(name, redisUtil);
            TreeItem<String> treeItem = new TreeItem<String>(name);
            redisToolController.getRedisServiceTreeView().getRoot().getChildren().add(treeItem);
            reloadServiceAddress();
        } catch (Exception e) {
            log.error(e.getMessage());
            TooltipUtil.showToast(e.getMessage());
        }
    }

    public void reloadServiceAddress() {
        redisToolController.getRedisServiceTreeView().getRoot().getChildren().forEach((TreeItem<String> treeItem) -> {
            treeItem.getChildren().clear();
            RedisUtil redisUtil = jedisMap.get(treeItem.getValue());
            int dbAmount = redisUtil.getDbAmount();
            for (int i = 0; i < dbAmount; i++) {
                redisUtil.setId(i);
                TreeItem<String> treeItem2 = new TreeItem<String>("db" + i + "(" + redisUtil.getDbSize() + ")");
                treeItem.getChildren().add(treeItem2);
            }
        });
    }

    public void addDataServiceTabPane(String redisName, int redisId) {
        String tabName = redisName + "-db" + redisId;
        Tab tab1 = dataTableTabMap.get(tabName);
        if (tab1 != null) {
            redisToolController.getDataServiceTabPane().getSelectionModel().select(tab1);
            return;
        }
        final Tab tab = new Tab(tabName);
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                List<Tab> tabList = new ArrayList<Tab>();
                redisToolController.getDataViewTabPane().getTabs().forEach((Tab tab2)->{
                    if(tab2.getText().startsWith(tabName)){
                        tabList.add(tab2);
                    }
                });
                redisToolController.getDataViewTabPane().getTabs().removeAll(tabList);
                dataTableTabMap.remove(tab.getText());
            }
        });
        FXMLLoader fXMLLoader = RedisToolDataTableController.getFXMLLoader();
        try {
            tab.setContent(fXMLLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RedisToolDataTableController redisToolDataTableController = fXMLLoader.getController();
        RedisUtil redisUtil = jedisMap.get(redisName).clone();
        redisUtil.setId(redisId);
        redisToolDataTableController.setData(redisToolController, redisUtil);
        redisToolDataTableController.setTabName(tabName);
        redisToolController.getDataServiceTabPane().getTabs().add(tab);
        redisToolController.getDataServiceTabPane().getSelectionModel().select(tab);
        dataTableTabMap.put(tabName, tab);
    }

    private TextField createTextField(String textValue, GridPane gridPane, String label, int row) {
        TextField textField = new TextField(textValue);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        gridPane.add(new Label(label), 0, row);
        gridPane.add(textField, 1, row);
        return textField;
    }

}
