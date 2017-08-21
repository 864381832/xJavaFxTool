package com.xwintop.xJavaFxTool.view.littleTools;


import com.xwintop.xJavaFxTool.model.FileCopyTableBean;

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
	protected TableView<FileCopyTableBean> tableViewMain;
	@FXML
	protected TableColumn<FileCopyTableBean, String> tableColumnCopyFileOriginalPath;
	@FXML
	protected TableColumn<FileCopyTableBean, String> tableColumnCopyFileTargetPath;
	@FXML
	protected TableColumn<FileCopyTableBean, String> tableColumnCopyNumber;
	@FXML
	protected TableColumn<FileCopyTableBean, Boolean> tableColumnIsCopy;
	@FXML
	protected TableColumn<FileCopyTableBean, Boolean> tableColumnIsRename;
	@FXML
	protected TableColumn<FileCopyTableBean, Boolean> tableColumnIsDelete;
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
