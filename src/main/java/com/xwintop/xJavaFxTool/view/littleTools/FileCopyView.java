package com.xwintop.xJavaFxTool.view.littleTools;


import com.xwintop.xJavaFxTool.services.littleTools.FileCopyService.TableBean;

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

public abstract class FileCopyView implements Initializable {
	@FXML
	protected TextField textFieldCopyFileOriginalPath;
	@FXML
	protected TextField textFieldCopyFileTargetPath;
	@FXML
	protected Button buttonChooseOriginalPath;
	@FXML
	protected Button buttonChooseTargetPath;
	@FXML
	protected CheckBox checkBoxIsCopy;
	@FXML
	protected Button buttonCopy;
	@FXML
	protected Spinner<Integer> spinnerCopyNumber;
	@FXML
	protected TableView<TableBean> tableViewMain;
	@FXML
	protected TableColumn<TableBean, String> tableColumnCopyFileOriginalPath;
	@FXML
	protected TableColumn<TableBean, String> tableColumnCopyFileTargetPath;
	@FXML
	protected TableColumn<TableBean, String> tableColumnCopyNumber;
	@FXML
	protected TableColumn<TableBean, Boolean> tableColumnIsCopy;
	@FXML
	protected TableColumn<TableBean, Boolean> tableColumnIsRename;
	@FXML
	protected TableColumn<TableBean, Boolean> tableColumnIsDelete;
	@FXML
	protected CheckBox checkBoxIsDelete;
	@FXML
	protected CheckBox checkBoxIsRename;
	@FXML
	protected Button buttonAddItem;
	@FXML
	protected Button buttonSaveConfigure;
	@FXML
	protected Button buttonDeleteSelectRow;
	@FXML
	protected Button otherSaveConfigureButton;
	@FXML
	protected Button loadingConfigureButton;
	@FXML
	protected Button runQuartzButton;
	@FXML
	protected TextField cronTextField;
	@FXML
	protected ChoiceBox<String> quartzChoiceBox;
	@FXML
	protected AnchorPane simpleScheduleAnchorPane;
	@FXML
	protected Spinner<Integer> intervalSpinner;
	@FXML
	protected Spinner<Integer> repeatCountSpinner;

}
