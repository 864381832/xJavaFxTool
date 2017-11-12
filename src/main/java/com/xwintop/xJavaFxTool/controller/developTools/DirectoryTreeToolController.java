package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.DirectoryTreeToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.developTools.DirectoryTreeToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @ClassName: DirectoryTreeToolController
 * @Description: 文件列表生成器工具
 * @author: xufeng
 * @date: 2017年11月13日 下午4:34:11
 */
@Getter
@Setter
@Log4j
public class DirectoryTreeToolController extends DirectoryTreeToolView {
    private DirectoryTreeToolService directoryTreeToolService = new DirectoryTreeToolService(this);
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
    private String[] filtrationTypeChoiceBoxStrings = new String[] { "包含条件","开始包含条件", "结束包含条件", "不包含条件"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setSpinnerValueFactory(showDirDepthSpinner, 0, Integer.MAX_VALUE,1);
        filtrationTypeChoiceBox.getItems().addAll(filtrationTypeChoiceBoxStrings);
        filtrationTypeChoiceBox.getSelectionModel().select(0);
        JavaFxViewUtil.setTableColumnMapValueFactory(filtrationConditionTableColumn, "filtrationCondition");
        filtrationTypeTableColumn.setCellValueFactory(new MapValueFactory("filtrationType"));
        filtrationTypeTableColumn.setCellFactory(
                new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>() {
                    @Override
                    public TableCell<Map<String, String>, String> call(
                            TableColumn<Map<String, String>, String> param) {
                        TableCell<Map<String, String>, String> cell = new TableCell<Map<String, String>, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                this.setText(null);
                                this.setGraphic(null);
                                if (!empty) {
                                    ChoiceBox<String> choiceBox = new ChoiceBox<String>();
                                    choiceBox.getItems().addAll(filtrationTypeChoiceBoxStrings);
                                    choiceBox.setValue(tableData.get(this.getIndex()).get("filtrationType"));
                                    choiceBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
                                        tableData.get(this.getIndex()).put("filtrationType",newVal);
                                    });
                                    this.setGraphic(choiceBox);
                                }
                            }
                        };
                        return cell;
                    }
                });

        tableViewMain.setItems(tableData);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(projectPathTextField, FileChooserUtil.FileType.FOLDER);
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    Map<String, String> tableBean = tableViewMain.getSelectionModel().getSelectedItem();
                    Map<String, String> tableBean2 = new HashMap<String, String>(tableBean);
                    tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(), tableBean2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    tableData.clear();
                });
                tableViewMain.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
        projectPathTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                directoryTreeToolService.buildDirectoryTreeString();
            }
        });
    }

    private void initService() {
        reloadAction(null);
    }

    @FXML
    private void projectPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            projectPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void filtrationAddAction(ActionEvent event) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("filtrationType",filtrationTypeChoiceBox.getValue());
        hashMap.put("filtrationCondition",filtrationConditionTextField.getText());
        tableData.add(hashMap);
    }

    @FXML
    private void reloadAction(ActionEvent event) {
        directoryTreeToolService.buildDirectoryTreeString();
    }
}