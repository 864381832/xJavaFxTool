package com.xwintop.xJavaFxTool.services.debugTools.redisTool;

import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataTableController;
import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDataViewController;
import com.xwintop.xJavaFxTool.controller.debugTools.redisTool.RedisToolDialogController;
import com.xwintop.xJavaFxTool.model.RedisToolDataTableBean;
import com.xwintop.xcore.util.RedisUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class RedisToolDataTableService {

    private RedisToolDataTableController redisToolDataTableController;
    private Map<String, Tab> dataViewTabMap = new HashMap<String, Tab>();

    private RedisToolDialogController redisToolDialogController;

    public RedisToolDataTableService(RedisToolDataTableController redisToolDataTableController) {
        this.redisToolDataTableController = redisToolDataTableController;
    }

    public void addTableViewContextMenu() {
        Menu menuAdd = new Menu("添加");
        MenuItem menuAddString = new MenuItem("String");
        menuAddString.setOnAction(event1 -> {
            addString();
            reloadTableData();
        });
        MenuItem menuAddList = new MenuItem("List");
        menuAddList.setOnAction(event1 -> {
            redisToolDialogController = RedisToolDialogController.getRedisToolDialogController("List", true);
            redisToolDialogController.setRedisToolDataTableService(this);
        });
        MenuItem menuAddSet = new MenuItem("Set");
        menuAddSet.setOnAction(event1 -> {
            redisToolDialogController = RedisToolDialogController.getRedisToolDialogController("Set", true);
            redisToolDialogController.setRedisToolDataTableService(this);
        });
        MenuItem menuAddSortedSet = new MenuItem("Sorted Set");
        menuAddSortedSet.setOnAction(event1 -> {
            redisToolDialogController = RedisToolDialogController.getRedisToolDialogController("Sorted Set", false);
            redisToolDialogController.setRedisToolDataTableService(this);
        });
        MenuItem menuAddHash = new MenuItem("Hash");
        menuAddHash.setOnAction(event1 -> {
            redisToolDialogController = RedisToolDialogController.getRedisToolDialogController("Hash", false);
            redisToolDialogController.setRedisToolDataTableService(this);
        });
        menuAdd.getItems().addAll(menuAddString, menuAddList, menuAddSet, menuAddSortedSet, menuAddHash);
        MenuItem menuReload = new MenuItem("刷新");
        menuReload.setOnAction(event1 -> {
            reloadTableData();
        });
        MenuItem menu_Remove = new MenuItem("删除选中行");
        menu_Remove.setOnAction(event1 -> {
            RedisToolDataTableBean redisToolDataTableBean = redisToolDataTableController.getDataTableView()
                    .getSelectionModel().getSelectedItem();
            if (redisToolDataTableBean != null) {
                redisToolDataTableController.getRedisUtil().del(redisToolDataTableBean.getName());
                redisToolDataTableController.getTableData().remove(redisToolDataTableBean);
                Tab tab = dataViewTabMap.get(redisToolDataTableBean.getName());
                if (tab != null) {
                    redisToolDataTableController.getRedisToolController().getDataViewTabPane().getTabs().remove(tab);
                    dataViewTabMap.remove(redisToolDataTableBean.getName());
                }
            }
        });
        MenuItem menu_RemoveAll = new MenuItem("删除所有");
        menu_RemoveAll.setOnAction(event1 -> {
            redisToolDataTableController.getRedisUtil().delAll();
            dataViewTabMap.forEach((String k, Tab tab) -> {
                redisToolDataTableController.getRedisToolController().getDataViewTabPane().getTabs().remove(tab);
            });
            dataViewTabMap.clear();
            redisToolDataTableController.getTableData().clear();
        });
        redisToolDataTableController.getDataTableView()
                .setContextMenu(new ContextMenu(menuAdd, menuReload, menu_Remove, menu_RemoveAll));
    }

    private void addString() {
        String[] valueStrings = AlertUtil.showInputAlert("请输入值", "key", "value", "过期时间（单位：秒，-1为永久）");
        if (valueStrings != null) {
            redisToolDataTableController.getRedisUtil().setString(valueStrings[0], valueStrings[1]);
            if (StringUtils.isNoneEmpty(valueStrings[2])) {
                redisToolDataTableController.getRedisUtil().setDeadLine(valueStrings[0],
                        Integer.parseInt(valueStrings[2]));
            }
        }
    }

    public void addTableDataByDialog() {
        switch (redisToolDialogController.getTitle()) {
            case "List":
                addList();
                break;
            case "Set":
                addSet();
                break;
            case "Sorted Set":
                addSortedSet();
                break;
            case "Hash":
                addHash();
                break;
        }
        reloadTableData();
    }

    private void addList() {
        String key = redisToolDialogController.getKeyTextField().getText();
        List<String> list = new ArrayList<String>();
        redisToolDialogController.getDialogTableData().forEach((map) -> {
            list.add(map.get("value"));
        });
        redisToolDataTableController.getRedisUtil().addList(key,list,false,true);
        redisToolDataTableController.getRedisUtil().setDeadLine(key,Integer.parseInt(redisToolDialogController.getOverdueTimeTextField().getText()));
    }

    private void addSet() {
        String key = redisToolDialogController.getKeyTextField().getText();
        Set<String> list = new HashSet<String>();
        redisToolDialogController.getDialogTableData().forEach((map) -> {
            list.add(map.get("value"));
        });
        redisToolDataTableController.getRedisUtil().addSet(key,list);
        redisToolDataTableController.getRedisUtil().setDeadLine(key,Integer.parseInt(redisToolDialogController.getOverdueTimeTextField().getText()));
    }

    private void addSortedSet() {
        String key = redisToolDialogController.getKeyTextField().getText();
        Map<String,Double> list = new HashMap<String,Double>();
        redisToolDialogController.getDialogTableData().forEach((map) -> {
            list.put(map.get("key"),Double.valueOf(map.get("value")));
        });
        redisToolDataTableController.getRedisUtil().addZSet(key,list);
        redisToolDataTableController.getRedisUtil().setDeadLine(key,Integer.parseInt(redisToolDialogController.getOverdueTimeTextField().getText()));
    }

    private void addHash() {
        String key = redisToolDialogController.getKeyTextField().getText();
        Map<String,String> list = new HashMap<String,String>();
        redisToolDialogController.getDialogTableData().forEach((map) -> {
            list.put(map.get("key"),map.get("value"));
        });
        redisToolDataTableController.getRedisUtil().addHash(key,list);
        redisToolDataTableController.getRedisUtil().setDeadLine(key,Integer.parseInt(redisToolDialogController.getOverdueTimeTextField().getText()));
    }

    public void addRedisToolDataViewTab(String key) {
        Tab tab1 = dataViewTabMap.get(key);
        if (tab1 != null) {
            redisToolDataTableController.getRedisToolController().getDataViewTabPane().getSelectionModel().select(tab1);
            return;
        }
        final Tab tab = new Tab(key);
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                dataViewTabMap.remove(tab.getText());
            }
        });
        FXMLLoader fXMLLoader = RedisToolDataViewController.getFXMLLoader();
        try {
            tab.setContent(fXMLLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RedisToolDataViewController redisToolDataViewController = fXMLLoader.getController();
        redisToolDataViewController.setData(redisToolDataTableController.getRedisUtil(), key);
        redisToolDataTableController.getRedisToolController().getDataViewTabPane().getTabs().add(tab);
        redisToolDataTableController.getRedisToolController().getDataViewTabPane().getSelectionModel().select(tab);
        dataViewTabMap.put(key, tab);
    }

    public void reloadTableData() {
        redisToolDataTableController.getTableData().clear();
        RedisUtil redisUtil = redisToolDataTableController.getRedisUtil();
        Set<String> nodekeys = redisUtil.getKeys();
        for (String key : nodekeys) {
            // System.out.println(key);
            String type = redisUtil.getValueType(key);
            String size = redisUtil.getSize(key).toString();
            Long timeLong = redisUtil.getDeadline(key);
            String time = timeLong == -1 ? "永久" : timeLong.toString();
            RedisToolDataTableBean redisToolDataTableBean = new RedisToolDataTableBean(key, type, size, time);
            redisToolDataTableController.getTableData().add(redisToolDataTableBean);
        }
    }

}
