package com.xwintop.xJavaFxTool.view.littleTools;

import com.xwintop.xJavaFxTool.model.EmailToolTableBean;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXComboBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

import java.util.Map;

/**
 * @ClassName: EmailToolView
 * @Description: 邮件发送工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:03
 */

@Getter
@Setter
public abstract class EmailToolView implements Initializable {
    @FXML
    protected CheckBox isEnabledCheckBox;
    @FXML
    protected TextField toEmailTextField;
    @FXML
    protected TextField toEmailNameTextField;
    @FXML
    protected Button addItemButton;
    @FXML
    protected Button importToEmailButton;
    @FXML
    protected JFXComboBox<String> hostNameComboBox;
    @FXML
    protected TextField portTextField;
    @FXML
    protected TextField userNameTextField;
    @FXML
    protected PasswordField passwordPasswordField;
    @FXML
    protected CheckBox sslCheckBox;
    @FXML
    protected ChoiceBox<String> quartzChoiceBox;
    @FXML
    protected TextField cronTextField;
    @FXML
    protected AnchorPane simpleScheduleAnchorPane;
    @FXML
    protected Spinner<Integer> intervalSpinner;
    @FXML
    protected Spinner<Integer> repeatCountSpinner;
    @FXML
    protected Button runQuartzButton;
    @FXML
    protected Button runAllButton;
    @FXML
    protected CheckBox sentSeparatelyCheckBox;
    @FXML
    protected TableView<EmailToolTableBean> tableViewMain;
    @FXML
    protected TableColumn<EmailToolTableBean, Integer> orderTableColumn;
    @FXML
    protected TableColumn<EmailToolTableBean, Boolean> isEnabledTableColumn;
    @FXML
    protected TableColumn<EmailToolTableBean, String> toEmailTableColumn;
    @FXML
    protected TableColumn<EmailToolTableBean, String> toEmailNameTableColumn;
    @FXML
    protected TableColumn<EmailToolTableBean, Boolean> manualTableColumn;
    @FXML
    protected TableColumn<EmailToolTableBean, String> sendStatusTableColumn;
    @FXML
    protected TextField SubjectTextField;
    @FXML
    protected HTMLEditor msgHtmlEditor;
    @FXML
    protected CheckBox attachCheckBox;
    @FXML
    protected TextField attachPathTextField;
    @FXML
    protected Button attachPathButton;
    @FXML
    protected Button addAttachPathButton;
    @FXML
    protected TableView<Map<String,String>> attachPathTableView;
    @FXML
    protected TableColumn<Map<String,String>,String> attachPathTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> attachNameTableColumn;
    @FXML
    protected TableColumn<Map<String,String>,String> attachDescriptionTableColumn;

}