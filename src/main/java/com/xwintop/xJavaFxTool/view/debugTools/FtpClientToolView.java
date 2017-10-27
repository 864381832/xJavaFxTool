package com.xwintop.xJavaFxTool.view.debugTools;

import com.xwintop.xJavaFxTool.model.FtpClientToolTableBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FtpClientToolView implements Initializable {
	@FXML
	protected CheckBox isEnabledCheckBox;
	@FXML
	protected TextField localFileTextField;
	@FXML
	protected Button localFileButton;
	@FXML
	protected TextField serverFileTextField;
	@FXML
	protected ChoiceBox<String> typeChoiceBox;
	@FXML
	protected Button addItemButton;
	@FXML
	protected TextField urlTextField;
	@FXML
	protected TextField portTextField;
	@FXML
	protected TextField userNameTextField;
	@FXML
	protected TextField passwordTextField;
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
	protected TableView<FtpClientToolTableBean> tableViewMain;
	@FXML
	protected TableColumn<FtpClientToolTableBean,Boolean> isEnabledTableColumn;
	@FXML
	protected TableColumn<FtpClientToolTableBean,String> localFileTableColumn;
	@FXML
	protected TableColumn<FtpClientToolTableBean,String> serverFileTableColumn;
	@FXML
	protected TableColumn<FtpClientToolTableBean,String> typeTableColumn;
	@FXML
	protected TableColumn<FtpClientToolTableBean,Boolean> manualTableColumn;
	@FXML
	protected TableColumn<FtpClientToolTableBean,String> remarksTableColumn;

}