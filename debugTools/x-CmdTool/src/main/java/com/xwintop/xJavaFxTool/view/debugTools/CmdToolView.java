package com.xwintop.xJavaFxTool.view.debugTools;

import com.xwintop.xJavaFxTool.model.CmdToolTableBean;

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
public abstract class CmdToolView implements Initializable {
	@FXML
	protected CheckBox isEnabledCheckBox;
	@FXML
	protected TextField scriptTextField;
	@FXML
	protected ChoiceBox<String> typeChoiceBox;
	@FXML
	protected TextField parameterTextField;
	@FXML
	protected Button addItemButton;
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
	protected TableView<CmdToolTableBean> tableViewMain;
	@FXML
	protected TableColumn<CmdToolTableBean,String> orderTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,Boolean> isEnabledTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,String> scriptTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,Boolean> viewScriptTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,String> typeTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,String> parameterTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,Boolean> manualTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean, Boolean> isRunAfterActivateTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,String> runAfterActivateTableColumn;
	@FXML
	protected TableColumn<CmdToolTableBean,String> remarksTableColumn;

}