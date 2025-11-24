package com.xwintop.xJavaFxTool.view.debugTools;

import com.xwintop.xJavaFxTool.model.FtpServerTableBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FtpServerView implements Initializable {
	@FXML
	protected TextField userNameTextField;
	@FXML
	protected TextField passwordTextField;
	@FXML
	protected TextField homeDirectoryTextField;
	@FXML
	protected Button chooseHomeDirectoryButton;
	@FXML
	protected CheckBox downFileCheckBox;
	@FXML
	protected CheckBox upFileCheckBox;
	@FXML
	protected CheckBox deleteFileCheckBox;
	@FXML
	protected Button buttonAddItem;
	@FXML
	protected TextField portTextField;
	@FXML
	protected CheckBox anonymousLoginEnabledCheckBox;
	@FXML
	protected TextField anonymousLoginEnabledTextField;
	@FXML
	protected Button anonymousLoginEnabledButton;
	@FXML
	protected Spinner<Integer> maxConnectCountSpinner;
	@FXML
	protected Button startButton;
	@FXML
	protected TableView<FtpServerTableBean> tableViewMain;
	@FXML
	protected TableColumn<FtpServerTableBean, Boolean> isEnabledTableColumn;
	@FXML
	protected TableColumn<FtpServerTableBean, String> userNameTableColumn;
	@FXML
	protected TableColumn<FtpServerTableBean, String> passwordTableColumn;
	@FXML
	protected TableColumn<FtpServerTableBean, String> homeDirectoryTableColumn;
	@FXML
	protected TableColumn<FtpServerTableBean, Boolean> downFIleTableColumn;
	@FXML
	protected TableColumn<FtpServerTableBean, Boolean> upFileTableColumn;
	@FXML
	protected TableColumn<FtpServerTableBean, Boolean> deleteFileTableColumn;

}