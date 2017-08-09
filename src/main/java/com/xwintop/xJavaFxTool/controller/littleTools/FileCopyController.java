package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.xwintop.xJavaFxTool.controller.littleTools.FileCopyController.TableBean;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class FileCopyController implements Initializable {
	@FXML
	private TextField textFieldCopyFileOriginalPath;
	@FXML
	private TextField textFieldCopyFileTargetPath;
	@FXML
	private CheckBox checkBoxIsCopy;
	@FXML
	private Button buttonCopy;
	@FXML
	private Spinner<Integer> spinnerCopyNumber;
	@FXML
	private TableView<TableBean> tableViewMain;
	@FXML
	private TableColumn<TableBean, String> tableColumnCopyFileOriginalPath;
	@FXML
	private TableColumn<TableBean, String> tableColumnCopyFileTargetPath;
	@FXML
	private TableColumn<TableBean, String> tableColumnCopyNumber;
	@FXML
	private TableColumn<TableBean, String> tableColumnIsCopy;
	@FXML
	private TableColumn<TableBean, String> tableColumnIsDelete;
	@FXML
	private CheckBox checkBoxIsDelete;
	@FXML
	private Button buttonAddItem;
	@FXML
	private Button buttonSaveConfigure;

	private ObservableList<TableBean> tableData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		JavaFxViewUtil.setSpinnerValueFactory(spinnerCopyNumber, 1, Integer.MAX_VALUE);
		tableColumnCopyFileOriginalPath
				.setCellValueFactory(new PropertyValueFactory<TableBean, String>("copyFileOriginalPath"));
		tableColumnCopyFileOriginalPath.setCellFactory(TextFieldTableCell.<TableBean>forTableColumn());
		tableColumnCopyFileOriginalPath.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setCopyFileOriginalPath(t.getNewValue());
			// ((TableBean) t.getTableView().getItems().get(t.getTablePosition().getRow()))
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

		tableColumnIsCopy.setCellValueFactory(new PropertyValueFactory<TableBean, String>("isCopy"));
		// tableColumnIsCopy.setCellFactory(
		// CheckBoxTableCell.forTableColumn(new Callback<Integer,
		// ObservableValue<Boolean>>() {
		// @Override
		// public ObservableValue<Boolean> call(Integer arg0) {
		// return null;
		// }
		// }));
		tableColumnIsCopy.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setIsCopy(t.getNewValue());
		});

		tableColumnIsDelete.setCellValueFactory(new PropertyValueFactory<TableBean, String>("isDelete"));
		tableColumnIsDelete.setCellFactory(TextFieldTableCell.<TableBean>forTableColumn());
		tableColumnIsDelete.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setIsDelete(t.getNewValue());
		});

		tableViewMain.setItems(tableData);
	}

	private void initEvent() {
	}

	@FXML
	private void addItemAction(ActionEvent event) {
		tableData.add(new TableBean(textFieldCopyFileOriginalPath.getText(), textFieldCopyFileTargetPath.getText(),
				spinnerCopyNumber.getValue().toString(), checkBoxIsCopy.isSelected() ? "是" : "否",
				checkBoxIsDelete.isSelected() ? "是" : "否"));
	}

	@FXML
	private void saveConfigure(ActionEvent event) {
	}

	@FXML
	private void copyAction(ActionEvent event) throws Exception {
		for (TableBean tableBean : tableData) {
			if("是".equals(tableBean.getIsCopy())) {
				int number = Integer.parseInt(tableBean.getCopyNumber());
				File fileOriginal = new File(tableBean.getCopyFileOriginalPath());
				File fileTarget = new File(tableBean.getCopyFileTargetPath());
				for(int i=0;i<number;i++) {
					if(fileOriginal.isDirectory()) {
						FileUtils.copyDirectory(fileOriginal, fileTarget,false);
					}else {
						FileUtils.copyFileToDirectory(fileOriginal, fileTarget);
					}
				}
				if("是".equals(tableBean.getIsDelete())) {
					if(fileOriginal.isDirectory()) {
						FileUtils.deleteDirectory(fileOriginal);
					}else {
						FileUtils.deleteQuietly(fileOriginal);
					}
				}
			}
		}
	}

	public static class TableBean {
		private SimpleStringProperty copyFileOriginalPath;
		private SimpleStringProperty copyFileTargetPath;
		private SimpleStringProperty copyNumber;
		private SimpleStringProperty isCopy;
		private SimpleStringProperty isDelete;

		public TableBean(String copyFileOriginalPath, String copyFileTargetPath, String copyNumber, String isCopy,
				String isDelete) {
			super();
			this.copyFileOriginalPath = new SimpleStringProperty(copyFileOriginalPath);
			this.copyFileTargetPath = new SimpleStringProperty(copyFileTargetPath);
			this.copyNumber = new SimpleStringProperty(copyNumber);
			this.isCopy = new SimpleStringProperty(isCopy);
			this.isDelete = new SimpleStringProperty(isDelete);
		}

		public String getCopyFileOriginalPath() {
			return copyFileOriginalPath.get();
		}

		public void setCopyFileOriginalPath(String copyFileOriginalPath) {
			this.copyFileOriginalPath.set(copyFileOriginalPath);
		}

		public String getCopyFileTargetPath() {
			return copyFileTargetPath.get();
		}

		public void setCopyFileTargetPath(String copyFileTargetPath) {
			this.copyFileTargetPath.set(copyFileTargetPath);
		}

		public String getCopyNumber() {
			return copyNumber.get();
		}

		public void setCopyNumber(String copyNumber) {
			this.copyNumber.set(copyNumber);
		}

		public String getIsCopy() {
			return isCopy.get();
		}

		public void setIsCopy(String isCopy) {
			this.isCopy.set(isCopy);
		}

		public String getIsDelete() {
			return isDelete.get();
		}

		public void setIsDelete(String isDelete) {
			this.isDelete.set(isDelete);
		}

	}
}
