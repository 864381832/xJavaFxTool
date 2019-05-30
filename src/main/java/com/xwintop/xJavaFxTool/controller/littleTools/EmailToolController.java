package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.model.EmailToolTableBean;
import com.xwintop.xJavaFxTool.services.littleTools.EmailToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.EmailToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: EmailToolController
 * @Description: 邮件发送工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:04
 */
@Getter
@Setter
@Slf4j
public class EmailToolController extends EmailToolView {
    private EmailToolService emailToolService = new EmailToolService(this);
    private ObservableList<EmailToolTableBean> tableData = FXCollections.observableArrayList();
    private ObservableList<Map<String, String>> attachPathTableData = FXCollections.observableArrayList();
    private String[] quartzChoiceBoxStrings = new String[]{"简单表达式", "Cron表达式"};
    private String[] hostNameComboBoxStrings = new String[]{"smtp.163.com", "smtp.gmail.com", "smtp.aliyun.com", "smtp.exmail.qq.com", "smtp.qq.com", "smtp.139.com", "smtp.189.cn"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        emailToolService.loadingConfigure();
        hostNameComboBox.getItems().addAll(hostNameComboBoxStrings);
        hostNameComboBox.getSelectionModel().select(0);

        orderTableColumn.setCellValueFactory(new PropertyValueFactory<EmailToolTableBean, Integer>("order"));

        isEnabledTableColumn
                .setCellValueFactory(new PropertyValueFactory<EmailToolTableBean, Boolean>("isEnabled"));
        isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));

        toEmailTableColumn.setCellValueFactory(new PropertyValueFactory<EmailToolTableBean, String>("toEmail"));
        toEmailTableColumn.setCellFactory(TextFieldTableCell.<EmailToolTableBean>forTableColumn());
        toEmailTableColumn.setOnEditCommit((CellEditEvent<EmailToolTableBean, String> t) -> {
            t.getRowValue().setToEmail(t.getNewValue());
        });

        toEmailNameTableColumn
                .setCellValueFactory(new PropertyValueFactory<EmailToolTableBean, String>("toEmailName"));
        toEmailNameTableColumn.setCellFactory(TextFieldTableCell.<EmailToolTableBean>forTableColumn());
        toEmailNameTableColumn.setOnEditCommit((CellEditEvent<EmailToolTableBean, String> t) -> {
            t.getRowValue().setToEmailName(t.getNewValue());
        });

        manualTableColumn.setCellFactory((col) -> {
            TableCell<EmailToolTableBean, Boolean> cell = new TableCell<EmailToolTableBean, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        Button delBtn = new Button("运行");
                        this.setContentDisplay(ContentDisplay.CENTER);
                        this.setGraphic(delBtn);
                        delBtn.setOnMouseClicked((me) -> {
                            EmailToolTableBean emailToolTableBean = tableData.get(this.getIndex());
                            emailToolService.runAction(emailToolTableBean);
                        });
                    }
                }
            };
            return cell;
        });

        sendStatusTableColumn.setCellValueFactory(new PropertyValueFactory<EmailToolTableBean, String>("sendStatus"));
        sendStatusTableColumn.setCellFactory(TextFieldTableCell.<EmailToolTableBean>forTableColumn());
        sendStatusTableColumn.setOnEditCommit((CellEditEvent<EmailToolTableBean, String> t) -> {
            t.getRowValue().setSendStatus(t.getNewValue());
        });

        tableViewMain.setItems(tableData);

        JavaFxViewUtil.setTableColumnMapValueFactory(attachPathTableColumn, "attachPath");
        JavaFxViewUtil.setTableColumnMapValueFactory(attachNameTableColumn, "attachName");
        JavaFxViewUtil.setTableColumnMapValueFactory(attachDescriptionTableColumn, "attachDescription");
        attachPathTableView.setItems(attachPathTableData);

        quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
        quartzChoiceBox.getSelectionModel().select(0);

        JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 60, Integer.MAX_VALUE);
        JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(attachPathTextField, FileChooserUtil.FileType.FILE);
        tableData.addListener((ListChangeListener.Change<? extends EmailToolTableBean> tableBean) -> {
            try {
                for (int i = 0; i < tableData.size(); i++) {
                    tableData.get(i).setOrder(i + 1);
                }
                saveConfigure(null);
            } catch (Exception e) {
                log.error("保存配置失败", e);
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
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    EmailToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
                    EmailToolTableBean tableBean2 = new EmailToolTableBean(tableBean.getPropertys());
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
        attachPathTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    Map<String, String> tableBean = attachPathTableView.getSelectionModel().getSelectedItem();
                    Map<String, String> tableBean2 = new HashMap<>(tableBean);
                    attachPathTableData.add(attachPathTableView.getSelectionModel().getSelectedIndex(), tableBean2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    attachPathTableData.remove(attachPathTableView.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    attachPathTableData.clear();
                });
                attachPathTableView.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void addItemAction(ActionEvent event) {
        tableData.add(new EmailToolTableBean(tableData.size() + 1, isEnabledCheckBox.isSelected(), toEmailTextField.getText(), toEmailNameTextField.getText(), ""));
    }

    @FXML
    void attachPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            attachPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void importToEmailAction(ActionEvent event) {
        emailToolService.importToEmailAction();
    }

    @FXML
    private void saveConfigure(ActionEvent event) throws Exception {
        emailToolService.saveConfigure();
    }

    @FXML
    private void otherSaveConfigureAction(ActionEvent event) throws Exception {
        emailToolService.otherSaveConfigureAction();
    }

    @FXML
    private void loadingConfigureAction(ActionEvent event) {
        emailToolService.loadingConfigureAction();
    }

    @FXML
    private void runQuartzAction(ActionEvent event) throws Exception {
        if ("定时运行".equals(runQuartzButton.getText())) {
            boolean isTrue = emailToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(),
                    intervalSpinner.getValue(), repeatCountSpinner.getValue());
            if (isTrue) {
                runQuartzButton.setText("停止运行");
            }
        } else {
            boolean isTrue = emailToolService.stopQuartzAction();
            if (isTrue) {
                runQuartzButton.setText("定时运行");
            }
        }
    }

    @FXML
    private void runAllAction(ActionEvent event) {
        emailToolService.runAllAction();
    }

    @FXML
    private void addAttachPathAction(ActionEvent event) {
        Map<String, String> map = new HashMap<>();
        map.put("attachPath", attachPathTextField.getText());
        map.put("attachName", attachNameTableColumn.getText());
        map.put("attachDescription", attachDescriptionTableColumn.getText());
        attachPathTableData.add(map);
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) throws Exception {
        emailToolService.stopQuartzAction();
    }
}