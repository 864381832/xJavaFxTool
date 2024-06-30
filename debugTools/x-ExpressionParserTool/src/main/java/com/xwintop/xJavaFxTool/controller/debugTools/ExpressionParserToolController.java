package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.model.ExpressionParserToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.ExpressionParserToolService;
import com.xwintop.xJavaFxTool.view.debugTools.ExpressionParserToolView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
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
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ExpressionParserToolController
 * @Description: 表达式解析器工具类
 * @author: xufeng
 * @date: 2021/9/12 22:52
 */

@Getter
@Setter
@Slf4j
public class ExpressionParserToolController extends ExpressionParserToolView {
    private ExpressionParserToolService expressionParserToolService = new ExpressionParserToolService(this);
    private ObservableList<ExpressionParserToolTableBean> tableData = FXCollections.observableArrayList();
    private String[] quartzChoiceBoxStrings = new String[]{"简单表达式", "Cron表达式"};
    private String[] typeChoiceBoxStrings = new String[]{"SpringEl脚本", "SpringEl脚本文件",
            "Aviator脚本", "Aviator脚本文件", "Jexl脚本", "Jexl脚本文件", "Mvel脚本", "Mvel脚本文件",
            "BeanShell脚本", "BeanShell脚本文件", "Velocity脚本", "Velocity脚本文件", "FreeMarker脚本",
            "FreeMarker脚本文件", "StringTemplate脚本", "StringTemplate脚本文件", "QLExpress脚本", "QLExpress脚本文件"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        expressionParserToolService.loadingConfigure();
        orderTableColumn.setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, String>("order"));
        orderTableColumn.setCellFactory(TextFieldTableCell.<ExpressionParserToolTableBean>forTableColumn());
        orderTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ExpressionParserToolTableBean, String> t) -> {
            t.getRowValue().setOrder(t.getNewValue());
        });

        isEnabledTableColumn.setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, Boolean>("isEnabled"));
        isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));

        scriptTableColumn.setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, String>("script"));
        scriptTableColumn.setCellFactory(TextFieldTableCell.<ExpressionParserToolTableBean>forTableColumn());
        scriptTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ExpressionParserToolTableBean, String> t) -> {
            t.getRowValue().setScript(t.getNewValue());
        });

        viewScriptTableColumn.setCellFactory((col) -> {
            TableCell<ExpressionParserToolTableBean, Boolean> cell = new TableCell<ExpressionParserToolTableBean, Boolean>() {
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
                            ExpressionParserToolTableBean scriptEngineToolTableBean = tableData.get(this.getIndex());
                            expressionParserToolService.showScriptAction(scriptEngineToolTableBean);
                        });
                    }
                }
            };
            return cell;
        });

        typeTableColumn.setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, String>("type"));
        typeTableColumn.setCellFactory(
                new Callback<TableColumn<ExpressionParserToolTableBean, String>, TableCell<ExpressionParserToolTableBean, String>>() {
                    @Override
                    public TableCell<ExpressionParserToolTableBean, String> call(TableColumn<ExpressionParserToolTableBean, String> param) {
                        TableCell<ExpressionParserToolTableBean, String> cell = new TableCell<ExpressionParserToolTableBean, String>() {
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

        parameterTableColumn.setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, String>("parameter"));
        parameterTableColumn.setCellFactory(TextFieldTableCell.<ExpressionParserToolTableBean>forTableColumn());
        parameterTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ExpressionParserToolTableBean, String> t) -> {
            t.getRowValue().setParameter(t.getNewValue());
        });

        manualTableColumn.setCellFactory((col) -> {
            TableCell<ExpressionParserToolTableBean, Boolean> cell = new TableCell<ExpressionParserToolTableBean, Boolean>() {
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
                            ExpressionParserToolTableBean scriptEngineToolTableBean = tableData.get(this.getIndex());
                            expressionParserToolService.runAction(scriptEngineToolTableBean);
                        });
                    }
                }
            };
            return cell;
        });

        isRunAfterActivateTableColumn
                .setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, Boolean>("isRunAfterActivate"));
        isRunAfterActivateTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isRunAfterActivateTableColumn));

        runAfterActivateTableColumn
                .setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, String>("runAfterActivate"));
        runAfterActivateTableColumn.setCellFactory(TextFieldTableCell.<ExpressionParserToolTableBean>forTableColumn());
        runAfterActivateTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ExpressionParserToolTableBean, String> t) -> {
            t.getRowValue().setRunAfterActivate(t.getNewValue());
        });

        remarksTableColumn.setCellValueFactory(new PropertyValueFactory<ExpressionParserToolTableBean, String>("remarks"));
        remarksTableColumn.setCellFactory(TextFieldTableCell.<ExpressionParserToolTableBean>forTableColumn());
        remarksTableColumn.setOnEditCommit((TableColumn.CellEditEvent<ExpressionParserToolTableBean, String> t) -> {
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
        tableData.addListener((ListChangeListener.Change<? extends ExpressionParserToolTableBean> tableBean) -> {
            try {
                saveConfigure(null);
            } catch (Exception e) {
                log.error("保存配置失败", e);
            }
        });
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    ExpressionParserToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
                    ExpressionParserToolTableBean tableBean2 = new ExpressionParserToolTableBean(tableBean.getPropertys());
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
        String id = "x" + String.valueOf(System.currentTimeMillis()).substring(7);
        tableData.add(new ExpressionParserToolTableBean(id, isEnabledCheckBox.isSelected(),
                scriptTextField.getText(), typeChoiceBox.getValue(), parameterTextField.getText(), false, " ", " "));
    }

    @FXML
    private void saveConfigure(ActionEvent event) throws Exception {
        expressionParserToolService.saveConfigure();
    }

    @FXML
    private void otherSaveConfigureAction(ActionEvent event) throws Exception {
        expressionParserToolService.otherSaveConfigureAction();
    }

    @FXML
    private void loadingConfigureAction(ActionEvent event) {
        expressionParserToolService.loadingConfigureAction();
    }

    @FXML
    private void runQuartzAction(ActionEvent event) throws Exception {
        if ("定时运行".equals(runQuartzButton.getText())) {
            boolean isTrue = expressionParserToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(), intervalSpinner.getValue(),
                    repeatCountSpinner.getValue());
            if (isTrue) {
                runQuartzButton.setText("停止运行");
            }
        } else {
            boolean isTrue = expressionParserToolService.stopQuartzAction();
            if (isTrue) {
                runQuartzButton.setText("定时运行");
            }
        }
    }

    @FXML
    private void runAllAction(ActionEvent event) {
        expressionParserToolService.runAllAction();
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) throws Exception {
        expressionParserToolService.stopQuartzAction();
    }
}