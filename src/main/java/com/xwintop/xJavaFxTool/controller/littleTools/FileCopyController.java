package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.services.littleTools.FileCopyService;
import com.xwintop.xJavaFxTool.services.littleTools.FileCopyService.TableBean;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileCopyView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;

public class FileCopyController extends FileCopyView {
	private FileCopyService fileCopyService = new FileCopyService();
	private ObservableList<TableBean> tableData = FXCollections.observableArrayList();
	private String[] quartzChoiceBoxStrings = new String[] { "简单表达式", "Cron表达式" };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		fileCopyService.setTableData(tableData);
		fileCopyService.loadingConfigure();
		JavaFxViewUtil.setSpinnerValueFactory(spinnerCopyNumber, 1, Integer.MAX_VALUE);
		tableColumnCopyFileOriginalPath
				.setCellValueFactory(new PropertyValueFactory<TableBean, String>("copyFileOriginalPath"));
		tableColumnCopyFileOriginalPath.setCellFactory(TextFieldTableCell.<TableBean>forTableColumn());
		tableColumnCopyFileOriginalPath.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setCopyFileOriginalPath(t.getNewValue());
			// ((TableBean)
			// t.getTableView().getItems().get(t.getTablePosition().getRow()))
			// .setCopyFileOriginalPath(t.getNewValue());
		});

		tableColumnCopyFileTargetPath
				.setCellValueFactory(new PropertyValueFactory<TableBean, String>("copyFileTargetPath"));
		tableColumnCopyFileTargetPath.setCellFactory(TextFieldTableCell.<TableBean>forTableColumn());
		tableColumnCopyFileTargetPath.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setCopyFileTargetPath(t.getNewValue());
		});

		tableColumnCopyNumber.setCellValueFactory(new PropertyValueFactory<TableBean, String>("copyNumber"));
		tableColumnCopyNumber.setCellFactory(TextFieldTableCell.<TableBean>forTableColumn());
		tableColumnCopyNumber.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setCopyNumber(t.getNewValue());
		});

		tableColumnIsCopy.setCellValueFactory(new PropertyValueFactory<TableBean, Boolean>("isCopy"));
		tableColumnIsCopy.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsCopy));
		tableColumnIsRename.setCellValueFactory(new PropertyValueFactory<TableBean, Boolean>("isRename"));
		tableColumnIsRename.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsRename));
		tableColumnIsDelete.setCellValueFactory(new PropertyValueFactory<TableBean, Boolean>("isDelete"));
		tableColumnIsDelete.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsDelete));
		tableViewMain.setItems(tableData);

		quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
		quartzChoiceBox.setValue(quartzChoiceBoxStrings[0]);
		JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 1, Integer.MAX_VALUE);
		JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(textFieldCopyFileOriginalPath, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(textFieldCopyFileTargetPath, FileChooserUtil.FileType.FOLDER);
		tableData.addListener((Change<? extends TableBean> tableBean)->{
			try {
				saveConfigure(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		tableViewMain.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_Copy = new MenuItem("复制选中行");
				menu_Copy.setOnAction(event1 -> {
					TableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
					TableBean tableBean2= new FileCopyService().new TableBean(tableBean.getPropertys());
					tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(),tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					deleteSelectRowAction(null);
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				tableViewMain.setContextMenu(new ContextMenu(menu_Copy,menu_Remove, menu_RemoveAll));
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

	@FXML
	private void chooseOriginalPathAction(ActionEvent event) {
		File file = FileChooserUtil.chooseFile();
		if (file != null) {
			textFieldCopyFileOriginalPath.setText(file.getPath());
		}
	}

	@FXML
	private void chooseTargetPathAction(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			textFieldCopyFileTargetPath.setText(file.getPath());
		}
	}

	@FXML
	private void addItemAction(ActionEvent event) {
		tableData.add(new FileCopyService().new TableBean(textFieldCopyFileOriginalPath.getText(),
				textFieldCopyFileTargetPath.getText(), spinnerCopyNumber.getValue().toString(),
				checkBoxIsCopy.isSelected(),checkBoxIsRename.isSelected(), checkBoxIsDelete.isSelected()));
	}

	@FXML
	private void deleteSelectRowAction(ActionEvent event) {
		tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		fileCopyService.saveConfigure();
	}

	@FXML
	private void copyAction(ActionEvent event) throws Exception {
		fileCopyService.copyAction();
	}

	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		fileCopyService.otherSaveConfigureAction();
	}

	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		fileCopyService.loadingConfigureAction();
	}

	@FXML
	private void runQuartzAction(ActionEvent event) throws Exception {
		if("定时运行".equals(runQuartzButton.getText())){
			boolean isTrue = fileCopyService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(), intervalSpinner.getValue(),
					repeatCountSpinner.getValue());
			if(isTrue){
				runQuartzButton.setText("停止运行");
			}
		}else{
			boolean isTrue = fileCopyService.stopQuartzAction();
			if(isTrue){
				runQuartzButton.setText("定时运行");
			}
		}
	}

}
