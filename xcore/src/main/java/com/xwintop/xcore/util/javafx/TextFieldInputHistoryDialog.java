package com.xwintop.xcore.util.javafx;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @ClassName: TextFieldInputHistoryDialog
 * @Description: 输入框历史编辑工具
 * @author: xufeng
 * @date: 2020/4/23 17:23
 */


@Getter
@Setter
@Slf4j
public class TextFieldInputHistoryDialog {
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();

    private String saveFilePath = null;

    private String[] tableColumns = null;

    private ContextMenu contextMenu = new ContextMenu();

    public TextFieldInputHistoryDialog(String saveFilePath, String... tableColumns) {
        this.saveFilePath = saveFilePath;
        this.tableColumns = tableColumns;
        this.loadingConfigure();
    }

    public void addConfig(String... values) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < this.tableColumns.length; i++) {
            map.put(this.tableColumns[i], values[i]);
        }
        map.put("name", values[0]);
        map.put("explain", "");
        for (Map<String, String> tableDatum : this.tableData) {
            boolean isEquals = true;
            for (int i = 0; i < this.tableColumns.length; i++) {
                if (!values[i].equals(tableDatum.get(this.tableColumns[i]))) {
                    isEquals = false;
                }
            }
            if (isEquals) {
                return;
            }
        }
        this.tableData.add(map);
        saveConfigure();
    }

    public void loadingConfigure() {
        try {
            if (StringUtils.isNotEmpty(saveFilePath)) {
                File CONFIG_FILE = new File(saveFilePath);
                if (!CONFIG_FILE.exists()) {
                    FileUtils.touch(CONFIG_FILE);
                }
                List<Map<String, String>> list = JSON.parseObject(FileUtils.readFileToString(CONFIG_FILE, "UTF-8"), List.class);
                if (list != null) {
                    this.tableData.addAll(list);
                }
            }
        } catch (Exception e) {
            log.error("加载配置失败：", e);
        }
    }

    public void saveConfigure() {
        if (StringUtils.isNotEmpty(saveFilePath)) {
            try {
                File CONFIG_FILE = new File(saveFilePath);
                FileUtils.writeStringToFile(CONFIG_FILE, JSON.toJSONString(this.tableData, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue), "UTF-8");
                TooltipUtil.showToast("保存配置成功,保存在：" + CONFIG_FILE.getPath());
            } catch (Exception e) {
                log.error("保存配置失败", e);
            }
        } else {
            TooltipUtil.showToast("未添加保存路径！");
        }
    }

    public void setOnMouseClicked(TextField hostText, Consumer<Map<String, String>> consumer, Function<Map<String, String>, String> menuItemName) {
        hostText.setOnMouseClicked(event -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
            contextMenu.getItems().clear();
            if (this.tableData != null) {
                for (Map<String, String> map : this.tableData) {
                    MenuItem menu_tab = null;
                    if (menuItemName == null) {
                        menu_tab = new MenuItem(map.get("name"));
                    } else {
                        menu_tab = new MenuItem(menuItemName.apply(map));
                    }
                    menu_tab.setOnAction(event1 -> {
                        if (consumer == null) {
                            hostText.setText(map.get("name"));
                        } else {
                            consumer.accept(map);
                        }
                    });
                    contextMenu.getItems().add(menu_tab);
                }
            }
            MenuItem menu_tab = new MenuItem("编辑历史输入");
            menu_tab.setOnAction(event1 -> {
                try {
                    TextFieldInputHistoryDialog.this.openEditWindow();
                } catch (Exception e) {
                    log.error("加载历史输入编辑界面失败", e);
                }
            });
            contextMenu.getItems().add(menu_tab);
            contextMenu.show(hostText, null, 0, hostText.getHeight());
        });
    }

    //设置输入框监听搜索显示
    public void setTextPropertyListener(TextField hostText, Consumer<Map<String, String>> consumer, Function<Map<String, String>, String> menuItemName) {
        hostText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
            contextMenu.getItems().clear();
            if (this.tableData != null) {
                for (Map<String, String> map : this.tableData) {
                    String menuItemNameStr = null;
                    if (menuItemName == null) {
                        menuItemNameStr = map.get("name");
                    } else {
                        menuItemNameStr = menuItemName.apply(map);
                    }
                    if (!StringUtils.containsIgnoreCase(menuItemNameStr, newValue)) {
                        continue;
                    }
                    MenuItem menu_tab = new MenuItem(menuItemNameStr);
                    menu_tab.setOnAction(event1 -> {
                        if (consumer == null) {
                            hostText.setText(map.get("name"));
                        } else {
                            consumer.accept(map);
                        }
                    });
                    contextMenu.getItems().add(menu_tab);
                }
            }
            MenuItem menu_tab = new MenuItem("编辑历史输入");
            menu_tab.setOnAction(event1 -> {
                try {
                    TextFieldInputHistoryDialog.this.openEditWindow();
                } catch (Exception e) {
                    log.error("加载历史输入编辑界面失败", e);
                }
            });
            contextMenu.getItems().add(menu_tab);
            contextMenu.show(hostText, null, 0, hostText.getHeight());
        });
    }

    public void setOnMouseClickedAndTextPropertyListener(TextField hostText, Consumer<Map<String, String>> consumer, Function<Map<String, String>, String> menuItemName) {
        this.setOnMouseClicked(hostText, consumer, menuItemName);
        this.setTextPropertyListener(hostText, consumer, menuItemName);
    }

    public void openEditWindow() {
        TableView<Map<String, String>> tableView = new TableView();
        tableView.setEditable(true);
        TableColumn nameTableColumn = new TableColumn("名称");
        JavaFxViewUtil.setTableColumnMapValueFactory(nameTableColumn, "name");
        tableView.getColumns().add(nameTableColumn);
        for (String tableColumnName : this.tableColumns) {
            TableColumn tableColumn = new TableColumn(tableColumnName);
            JavaFxViewUtil.setTableColumnMapValueFactory(tableColumn, tableColumnName);
            tableView.getColumns().add(tableColumn);
        }
        TableColumn explainTableColumn = new TableColumn("说明");
        JavaFxViewUtil.setTableColumnMapValueFactory(explainTableColumn, "explain");
        tableView.getColumns().add(explainTableColumn);

        tableView.setItems(tableData);
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(tableView);
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuAdd = new MenuItem("添加行");
                menuAdd.setOnAction(event1 -> {
                    tableView.getItems().add(new HashMap<>());
                });
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    Map<String, String> map = tableView.getSelectionModel().getSelectedItem();
                    Map<String, String> map2 = new HashMap<>(map);
                    tableView.getItems().add(tableView.getSelectionModel().getSelectedIndex(), map2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    tableView.getItems().clear();
                });
                MenuItem menuSave = new MenuItem("保存配置");
                menuSave.setOnAction(event1 -> {
                    try {
                        TextFieldInputHistoryDialog.this.saveConfigure();
                    } catch (Exception e) {
                        log.error("保存配置失败", e);
                    }
                });
                tableView.setContextMenu(new ContextMenu(menuAdd, menu_Copy, menu_Remove, menu_RemoveAll, menuSave));
            }
        });
        JavaFxViewUtil.openNewWindow("编辑历史输入", tableView);
    }
}
