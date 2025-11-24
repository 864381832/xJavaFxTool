package com.xwintop.xJavaFxTool.controller.littleTools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xwintop.xJavaFxTool.services.littleTools.FileBuildToolService;
import com.xwintop.xJavaFxTool.view.littleTools.FileBuildToolView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * @ClassName: FileBuildToolController
 * @Description: 文件生成工具
 * @author: xufeng
 * @date: 2020/4/18 0018 18:05
 */

@Getter
@Setter
@Slf4j
public class FileBuildToolController extends FileBuildToolView {
    private FileBuildToolService fileBuildToolService = new FileBuildToolService(this);
    private ObservableList<Map<String, String>> fieldTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        fieldTableView.setItems(fieldTableData);
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(fieldTableView);

    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(outputFilePathTextField, FileChooserUtil.FileType.FOLDER);
        fieldTableView.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuAdd = new MenuItem("添加行");
                menuAdd.setOnAction((event1) -> {
                    fieldTableView.getItems().add(new HashMap());
                });
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction((event1) -> {
                    Map<String, String> map = (Map) fieldTableView.getSelectionModel().getSelectedItem();
                    Map<String, String> map2 = new HashMap(map);
                    fieldTableView.getItems().add(fieldTableView.getSelectionModel().getSelectedIndex(), map2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction((event1) -> {
                    fieldTableView.getItems().remove(fieldTableView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction((event1) -> {
                    fieldTableView.getItems().clear();
                });
                ContextMenu contextMenu = new ContextMenu(new MenuItem[]{menuAdd, menu_Copy, menu_Remove, menu_RemoveAll});
                MenuItem menuAddTableColumn = new MenuItem("添加列");
                menuAddTableColumn.setOnAction((event1) -> {
                    String tableColumnName = AlertUtil.showInputAlertDefaultValue("请输入列名", "");
                    if (StringUtils.isNotEmpty(tableColumnName)) {
                        TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(tableColumnName);
                        JavaFxViewUtil.setTableColumnMapValueFactory(tableColumn, tableColumnName);
                        fieldTableView.getColumns().add(tableColumn);
                    }
                });
                contextMenu.getItems().add(menuAddTableColumn);
                MenuItem menuSaveTemplateAndConfig = new MenuItem("保存模版及配置");
                menuSaveTemplateAndConfig.setOnAction((event1) -> {
                    try {
                        fileBuildToolService.saveTemplateAndConfig();
                    } catch (Exception e) {
                        log.error("保存配置失败：", e);
                        TooltipUtil.showToast("保存配置失败：" + e.getMessage());
                    }
                });
                contextMenu.getItems().add(menuSaveTemplateAndConfig);
                MenuItem menuAddTemplateAndConfig = new MenuItem("加载模版及配置");
                menuAddTemplateAndConfig.setOnAction((event1) -> {
                    try {
                        fileBuildToolService.addTemplateAndConfig();
                    } catch (Exception e) {
                        log.error("保存配置失败：", e);
                        TooltipUtil.showToast("保存配置失败：" + e.getMessage());
                    }
                });
                contextMenu.getItems().add(menuAddTemplateAndConfig);
                MenuItem menuAddConfigJsonFile = new MenuItem("加载Json配置文件");
                menuAddConfigJsonFile.setOnAction((event1) -> {
                    File file = FileChooserUtil.chooseFile();
                    if (file != null) {
                        try {
                            JSONArray jsonArray = JSON.parseArray(FileUtils.readFileToString(file, "utf-8"));
                            if (jsonArray == null || jsonArray.isEmpty()) {
                                return;
                            }
                            fieldTableView.getColumns().clear();
                            fieldTableView.getItems().clear();
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            for (String key : jsonObject.keySet()) {
                                TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(key);
                                JavaFxViewUtil.setTableColumnMapValueFactory(tableColumn, key);
                                fieldTableView.getColumns().add(tableColumn);
                            }
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Map<String, String> dataRow = new HashMap<String, String>();
                                for (String key : jsonObject1.keySet()) {
                                    dataRow.put(key, jsonObject1.getString(key));
                                }
                                fieldTableView.getItems().add(dataRow);
                            }
                        } catch (Exception e) {
                            log.error("加载配置文件失败：", e);
                        }
                    }
                });
                contextMenu.getItems().add(menuAddConfigJsonFile);
                MenuItem menuAddConfigCsvFile = new MenuItem("加载csv配置文件");
                menuAddConfigCsvFile.setOnAction((event1) -> {
                    File file = FileChooserUtil.chooseFile();
                    if (file != null) {
                        try {
                            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withAllowDuplicateHeaderNames(false);
                            CSVParser parser = csvFileFormat.parse(new InputStreamReader(new ByteArrayInputStream(FileUtils.readFileToByteArray(file)), "utf-8"));
                            fieldTableView.getColumns().clear();
                            fieldTableView.getItems().clear();
                            List<String> headerNames = new ArrayList<>();
                            for (CSVRecord strings : parser) {
                                if (fieldTableView.getColumns().isEmpty()) {
                                    for (int i = 0; i < strings.size(); i++) {
                                        String key = strings.get(i);
                                        headerNames.add(key);
                                        TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(key);
                                        JavaFxViewUtil.setTableColumnMapValueFactory(tableColumn, key);
                                        fieldTableView.getColumns().add(tableColumn);
                                    }
                                } else {
                                    Map<String, String> dataRow = new HashMap<String, String>();
                                    for (int i = 0; i < headerNames.size(); i++) {
                                        dataRow.put(headerNames.get(i), strings.get(i));
                                    }
                                    fieldTableView.getItems().add(dataRow);
                                }
                            }
                        } catch (Exception e) {
                            log.error("加载配置文件失败：", e);
                        }
                    }
                });
                contextMenu.getItems().add(menuAddConfigCsvFile);
                fieldTableView.setContextMenu(contextMenu);
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void buildMoreFileAction(ActionEvent event) {
        fileBuildToolService.buildFileAction(true);
    }

    @FXML
    private void buildOneFileAction(ActionEvent event) {
        fileBuildToolService.buildFileAction(false);
    }

    @FXML
    private void outputFilePathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            outputFilePathTextField.setText(file.getPath());
        }
    }
}
