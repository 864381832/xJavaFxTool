package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.model.ScriptEngineToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.ScriptEngineToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.ScriptEngineToolView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * @ClassName: ScriptEngineToolController
 * @Description: 脚本引擎调试工具类
 * @author: xufeng
 * @date: 2018/1/28 23:01
 */

@Getter
@Setter
@Log4j
public class ScriptEngineToolController extends ScriptEngineToolView {
    private ScriptEngineToolService scriptEngineToolService = new ScriptEngineToolService(this);
    private ObservableList<ScriptEngineToolTableBean> tableData = FXCollections.observableArrayList();
    private String[] quartzChoiceBoxStrings = new String[] { "简单表达式", "Cron表达式" };
    private String[] typeChoiceBoxStrings = new String[] { "JavaScript脚本", "JavaScript脚本文件",
            "Groovy脚本", "Groovy脚本文件", "Python脚本", "Python脚本文件", "Lua脚本", "Lua脚本文件" };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        scriptEngineToolService.loadingConfigure();
        orderTableColumn.setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, String>("order"));
        orderTableColumn.setCellFactory(TextFieldTableCell.<ScriptEngineToolTableBean>forTableColumn());
        orderTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ScriptEngineToolTableBean, String> t) -> {
            t.getRowValue().setOrder(t.getNewValue());
        });

        isEnabledTableColumn.setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, Boolean>("isEnabled"));
        isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));

        scriptTableColumn.setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, String>("script"));
        scriptTableColumn.setCellFactory(TextFieldTableCell.<ScriptEngineToolTableBean>forTableColumn());
        scriptTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ScriptEngineToolTableBean, String> t) -> {
            t.getRowValue().setScript(t.getNewValue());
        });

        viewScriptTableColumn.setCellFactory((col) -> {
            TableCell<ScriptEngineToolTableBean, Boolean> cell = new TableCell<ScriptEngineToolTableBean, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        Button delBtn = new Button("查看");
                        this.setContentDisplay(ContentDisplay.CENTER);
                        this.setGraphic(delBtn);
                        delBtn.setOnMouseClicked((me) -> {
                            ScriptEngineToolTableBean scriptEngineToolTableBean = tableData.get(this.getIndex());
                            scriptEngineToolService.showScriptAction(scriptEngineToolTableBean);
                        });
                    }
                }
            };
            return cell;
        });

        typeTableColumn.setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, String>("type"));
        typeTableColumn.setCellFactory(
                new Callback<TableColumn<ScriptEngineToolTableBean, String>, TableCell<ScriptEngineToolTableBean, String>>() {
                    @Override
                    public TableCell<ScriptEngineToolTableBean, String> call(TableColumn<ScriptEngineToolTableBean, String> param) {
                        TableCell<ScriptEngineToolTableBean, String> cell = new TableCell<ScriptEngineToolTableBean, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                this.setText(null);
                                this.setGraphic(null);
                                if (!empty) {
                                    ChoiceBox<String> choiceBox = new ChoiceBox<String>();
                                    choiceBox.getItems().addAll(typeChoiceBoxStrings);
                                    choiceBox.setValue(tableData.get(this.getIndex()).getType());
                                    choiceBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
                                        tableData.get(this.getIndex()).setType(newVal);
                                    });
                                    this.setGraphic(choiceBox);
                                }
                            }
                        };
                        return cell;
                    }
                });

        parameterTableColumn.setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, String>("parameter"));
        parameterTableColumn.setCellFactory(TextFieldTableCell.<ScriptEngineToolTableBean>forTableColumn());
        parameterTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ScriptEngineToolTableBean, String> t) -> {
            t.getRowValue().setParameter(t.getNewValue());
        });

        manualTableColumn.setCellFactory((col) -> {
            TableCell<ScriptEngineToolTableBean, Boolean> cell = new TableCell<ScriptEngineToolTableBean, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        Button delBtn = new Button("执行");
                        this.setContentDisplay(ContentDisplay.CENTER);
                        this.setGraphic(delBtn);
                        delBtn.setOnMouseClicked((me) -> {
                            ScriptEngineToolTableBean scriptEngineToolTableBean = tableData.get(this.getIndex());
                            scriptEngineToolService.runAction(scriptEngineToolTableBean);
                        });
                    }
                }
            };
            return cell;
        });

        isRunAfterActivateTableColumn
                .setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, Boolean>("isRunAfterActivate"));
        isRunAfterActivateTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isRunAfterActivateTableColumn));

        runAfterActivateTableColumn
                .setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, String>("runAfterActivate"));
        runAfterActivateTableColumn.setCellFactory(TextFieldTableCell.<ScriptEngineToolTableBean>forTableColumn());
        runAfterActivateTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ScriptEngineToolTableBean, String> t) -> {
            t.getRowValue().setRunAfterActivate(t.getNewValue());
        });

        remarksTableColumn.setCellValueFactory(new PropertyValueFactory<ScriptEngineToolTableBean, String>("remarks"));
        remarksTableColumn.setCellFactory(TextFieldTableCell.<ScriptEngineToolTableBean>forTableColumn());
        remarksTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ScriptEngineToolTableBean, String> t) -> {
            t.getRowValue().setRemarks(t.getNewValue());
        });

        tableViewMain.setItems(tableData);

        quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
        quartzChoiceBox.setValue(quartzChoiceBoxStrings[0]);

        typeChoiceBox.getItems().addAll(typeChoiceBoxStrings);
        typeChoiceBox.setValue(typeChoiceBoxStrings[0]);
        JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 60, Integer.MAX_VALUE);
        JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);
    }

    private void initEvent() {
        tableData.addListener((ListChangeListener.Change<? extends ScriptEngineToolTableBean> tableBean) -> {
            try {
                saveConfigure(null);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    ScriptEngineToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
                    ScriptEngineToolTableBean tableBean2 = new ScriptEngineToolTableBean(tableBean.getPropertys());
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
        quartzChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (quartzChoiceBoxStrings[0].equals(newValue)) {
                    cronTextField.setVisible(false);
                    simpleScheduleAnchorPane.setVisible(true);
                } else if (quartzChoiceBoxStrings[1].equals(newValue)) {
                    cronTextField.setVisible(true);
                    simpleScheduleAnchorPane.setVisible(false);
                }
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void addItemAction(ActionEvent event) {
        String id = "x"+String.valueOf(System.currentTimeMillis()).substring(7);
        tableData.add(new ScriptEngineToolTableBean(id, isEnabledCheckBox.isSelected(),
                scriptTextField.getText(), typeChoiceBox.getValue(), parameterTextField.getText(), false, " ", " "));
    }

    @FXML
    private void saveConfigure(ActionEvent event) throws Exception {
        scriptEngineToolService.saveConfigure();
    }

    @FXML
    private void otherSaveConfigureAction(ActionEvent event) throws Exception {
        scriptEngineToolService.otherSaveConfigureAction();
    }

    @FXML
    private void loadingConfigureAction(ActionEvent event) {
        scriptEngineToolService.loadingConfigureAction();
    }

    @FXML
    private void runQuartzAction(ActionEvent event) throws Exception {
        if("定时运行".equals(runQuartzButton.getText())){
            boolean isTrue = scriptEngineToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(), intervalSpinner.getValue(),
                    repeatCountSpinner.getValue());
            if(isTrue){
                runQuartzButton.setText("停止运行");
            }
        }else{
            boolean isTrue = scriptEngineToolService.stopQuartzAction();
            if(isTrue){
                runQuartzButton.setText("定时运行");
            }
        }
    }

    @FXML
    private void runAllAction(ActionEvent event) {
        scriptEngineToolService.runAllAction();
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) throws Exception {
        scriptEngineToolService.stopQuartzAction();
    }
}