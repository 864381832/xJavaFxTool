package com.xwintop.xJavaFxTool.view.littleTools;

import com.xwintop.xJavaFxTool.model.SmsToolTableBean;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SmsToolView implements Initializable {
    @FXML
    protected CheckBox isEnabledCheckBox;
    @FXML
    protected TextField toPhoneTextField;
    @FXML
    protected TextField toPhoneNameTextField;
    @FXML
    protected Button addItemButton;
    @FXML
    protected Button importToPhoneButton;
    @FXML
    protected TableView<SmsToolTableBean> tableViewMain;
    @FXML
    protected TableColumn<SmsToolTableBean, Integer> orderTableColumn;
    @FXML
    protected TableColumn<SmsToolTableBean, Boolean> isEnabledTableColumn;
    @FXML
    protected TableColumn<SmsToolTableBean, String> toPhoneTableColumn;
    @FXML
    protected TableColumn<SmsToolTableBean, String> toPhoneNameTableColumn;
    @FXML
    protected TableColumn<SmsToolTableBean, Boolean> manualTableColumn;
    @FXML
    protected TableColumn<SmsToolTableBean, String> sendStatusTableColumn;
    @FXML
    protected TabPane mainTabPane;
    @FXML
    protected TextField cmccUrlTextField;
    @FXML
    protected TextField cmccApiKeyTextField;
    @FXML
    protected TextField cmccSecretKeyTextField;
    @FXML
    protected CheckBox cmccNeedReceiptCheckBox;
    @FXML
    protected TextField cmccReceiptNotificationUrlTextField;
    @FXML
    protected TextArea cmccMessageTextArea;
    @FXML
    protected Button cmccSendButton;
    @FXML
    protected TextField open189UrlTextField;
    @FXML
    protected TextField open189AppIdTextField;
    @FXML
    protected TextField open189AppSecretTextField;
    @FXML
    protected TextField open189TemplateIdTextField;
    @FXML
    protected TextArea open189TemplateParamTextArea;
    @FXML
    protected Button open189SendButton;
    @FXML
    protected TextField tencentAppidTextField;
    @FXML
    protected TextField tencentAppkeyTextField;
    @FXML
    protected CheckBox tencentIsParamCheckBox;
    @FXML
    protected TextField tencentMsgTextField;
    @FXML
    protected TextArea tencentParamsTextArea;
    @FXML
    protected Button tencentSendButton;
    @FXML
    protected TextField aliyunAccessKeyIdTextField;
    @FXML
    protected TextField aliyunAccessKeySecretTextField;
    @FXML
    protected TextField aliyunSignNameTextField;
    @FXML
    protected TextField aliyunTemplateCodeTextField;
    @FXML
    protected TextArea aliyunTemplateParamTextArea;
    @FXML
    protected Button aliyunSendButton;
    @FXML
    protected TextField monyunUrlTextField;
    @FXML
    protected TextField monyunUseridTextField;
    @FXML
    protected TextField monyunPwdTextField;
    @FXML
    protected TextField monyunApikeyTextField;
    @FXML
    protected TextArea monyunContentTextArea;
    @FXML
    protected TextField monyunSvrtypeTextField;
    @FXML
    protected TextField monyunExnoTextField;
    @FXML
    protected Button monyunSendButton;

}