package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.model.EmailToolTableBean;
import com.xwintop.xJavaFxTool.model.SmsToolTableBean;
import com.xwintop.xJavaFxTool.services.littleTools.SmsToolService;
import com.xwintop.xJavaFxTool.view.littleTools.SmsToolView;
import com.xwintop.xcore.util.HttpClientUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: SmsToolController
 * @Description: 短信群发工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:26
 */

@Getter
@Setter
@Log4j
public class SmsToolController extends SmsToolView {
    private SmsToolService smsToolService = new SmsToolService(this);
    private ObservableList<SmsToolTableBean> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        orderTableColumn.setCellValueFactory(new PropertyValueFactory<SmsToolTableBean, Integer>("order"));

        isEnabledTableColumn
                .setCellValueFactory(new PropertyValueFactory<SmsToolTableBean, Boolean>("isEnabled"));
        isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));

        toPhoneTableColumn.setCellValueFactory(new PropertyValueFactory<SmsToolTableBean, String>("toPhone"));
        toPhoneTableColumn.setCellFactory(TextFieldTableCell.<SmsToolTableBean>forTableColumn());
        toPhoneTableColumn.setOnEditCommit((CellEditEvent<SmsToolTableBean, String> t) -> {
            t.getRowValue().setToPhone(t.getNewValue());
        });

        toPhoneNameTableColumn
                .setCellValueFactory(new PropertyValueFactory<SmsToolTableBean, String>("toPhoneName"));
        toPhoneNameTableColumn.setCellFactory(TextFieldTableCell.<SmsToolTableBean>forTableColumn());
        toPhoneNameTableColumn.setOnEditCommit((CellEditEvent<SmsToolTableBean, String> t) -> {
            t.getRowValue().setToPhoneName(t.getNewValue());
        });

        manualTableColumn.setCellFactory((col) -> {
            TableCell<SmsToolTableBean, Boolean> cell = new TableCell<SmsToolTableBean, Boolean>() {
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
                            SmsToolTableBean smsToolTableBean = tableData.get(this.getIndex());
                            smsToolService.runAction(smsToolTableBean);
                        });
                    }
                }
            };
            return cell;
        });

        sendStatusTableColumn.setCellValueFactory(new PropertyValueFactory<SmsToolTableBean, String>("sendStatus"));
        sendStatusTableColumn.setCellFactory(TextFieldTableCell.<SmsToolTableBean>forTableColumn());
        sendStatusTableColumn.setOnEditCommit((CellEditEvent<SmsToolTableBean, String> t) -> {
            t.getRowValue().setSendStatus(t.getNewValue());
        });

        tableViewMain.setItems(tableData);

        mainTabPane.getSelectionModel().select(2);
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void addItemAction(ActionEvent event) {
        tableData.add(new SmsToolTableBean(tableData.size() + 1, isEnabledCheckBox.isSelected(), toPhoneTextField.getText(), toPhoneNameTextField.getText(), ""));
    }

    @FXML
    private void importToPhoneAction(ActionEvent event) {
        smsToolService.importToPhoneAction();
    }

    @FXML
    private void cmccSendAction(ActionEvent event) {
        smsToolService.runAllAction();
    }

    @FXML
    private void open189SendAction(ActionEvent event) {
        smsToolService.runAllAction();
    }

    @FXML
    private void tencentSendAction(ActionEvent event) {
        smsToolService.runAllAction();
    }

    @FXML
    private void aliyunSendAction(ActionEvent event) {
        smsToolService.runAllAction();
    }

    @FXML
    private void monyunSendAction(ActionEvent event) {
        smsToolService.runAllAction();
    }

    @FXML
    private void registerAliyunAction(ActionEvent event) {
        HttpClientUtil.openBrowseURL("https://promotion.aliyun.com/ntms/act/ambassador/sharetouser.html?userCode=vt7aiwvr&utm_source=vt7aiwvr");
    }

    @FXML
    private void registerCmccAction(ActionEvent event) {
        HttpClientUtil.openBrowseURL("http://www.openservice.com.cn");
    }

    @FXML
    private void registerOpen189Action(ActionEvent event) {
        HttpClientUtil.openBrowseURL("http://open.189.cn");
    }

    @FXML
    private void registerTencentAction(ActionEvent event) {
        HttpClientUtil.openBrowseURL("https://cloud.tencent.com/redirect.php?redirect=1005&cps_key=f312970b0318dd604cd33405d2c6d697");
    }

    @FXML
    private void registerMonyunAction(ActionEvent event) {
        HttpClientUtil.openBrowseURL("http://www.monyun.cn/?rsdlx7ba");
    }
}