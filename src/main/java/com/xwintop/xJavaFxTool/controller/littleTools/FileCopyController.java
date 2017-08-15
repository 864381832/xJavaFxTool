package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileCopyView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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

	private ObservableList<TableBean> tableData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		try {
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(
					ConfigureUtil.getConfigurePath("fileCopyConfigure.properties"));
			xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
				@Override
				public void accept(String t) {
					tableData.add(new TableBean(xmlConfigure.getString(t)));
				}
			});
		} catch (Exception e) {
		}
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
		tableColumnIsDelete.setCellValueFactory(new PropertyValueFactory<TableBean, Boolean>("isDelete"));
		tableColumnIsDelete.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsDelete));
		tableViewMain.setItems(tableData);
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(textFieldCopyFileOriginalPath, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(textFieldCopyFileTargetPath, FileChooserUtil.FileType.FOLDER);
		tableViewMain.setOnMouseClicked(event->{
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                	deleteSelectRowAction(null);
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                		tableData.clear();
                });
                tableViewMain.setContextMenu(new ContextMenu(menu_Remove,menu_RemoveAll));
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
		tableData.add(new TableBean(textFieldCopyFileOriginalPath.getText(), textFieldCopyFileTargetPath.getText(),
				spinnerCopyNumber.getValue().toString(), checkBoxIsCopy.isSelected(), checkBoxIsDelete.isSelected()));
	}

	@FXML
	private void deleteSelectRowAction(ActionEvent event) {
		tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		FileUtils.touch(ConfigureUtil.getConfigureFile("fileCopyConfigure.properties"));
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(
				ConfigureUtil.getConfigurePath("fileCopyConfigure.properties"));
		xmlConfigure.clear();
		for (int i = 0; i < tableData.size(); i++) {
			xmlConfigure.setProperty("tableBean" + i, tableData.get(i).getPropertys());
		}
		xmlConfigure.save();
	}

	@FXML
	private void copyAction(ActionEvent event) throws Exception {
		for (TableBean tableBean : tableData) {
			if (tableBean.getIsCopy()) {
				int number = Integer.parseInt(tableBean.getCopyNumber());
				File fileOriginal = new File(tableBean.getCopyFileOriginalPath());
				File fileTarget = new File(tableBean.getCopyFileTargetPath());
				for (int i = 0; i < number; i++) {
					if (fileOriginal.isDirectory()) {
						if (number == 1) {
							FileUtils.copyDirectory(fileOriginal, fileTarget, false);
						} else {
							Collection<File> files = FileUtils.listFiles(fileOriginal, null, false);
							for (File file : files) {
								FileUtils.copyFile(file, new File(fileTarget.getPath(), i + file.getName()));
							}
						}
					} else {
						if (number == 1) {
							FileUtils.copyFileToDirectory(fileOriginal, fileTarget);
						} else {
							FileUtils.copyFile(fileOriginal,
									new File(fileTarget.getPath(), i + fileOriginal.getName()));
						}
					}
				}
				if (tableBean.getIsDelete()) {
					if (fileOriginal.isDirectory()) {
						FileUtils.deleteDirectory(fileOriginal);
					} else {
						FileUtils.deleteQuietly(fileOriginal);
					}
				}
			}
		}
	}

	public class TableBean {
		private SimpleStringProperty copyFileOriginalPath;
		private SimpleStringProperty copyFileTargetPath;
		private SimpleStringProperty copyNumber;
		private SimpleBooleanProperty isCopy;
		private SimpleBooleanProperty isDelete;

		public TableBean(String copyFileOriginalPath, String copyFileTargetPath, String copyNumber, Boolean isCopy,
				Boolean isDelete) {
			super();
			this.copyFileOriginalPath = new SimpleStringProperty(copyFileOriginalPath);
			this.copyFileTargetPath = new SimpleStringProperty(copyFileTargetPath);
			this.copyNumber = new SimpleStringProperty(copyNumber);
			this.isCopy = new SimpleBooleanProperty(isCopy);
			this.isDelete = new SimpleBooleanProperty(isDelete);
		}

		public TableBean(String propertys) {
			String[] strings = propertys.split("__");
			this.copyFileOriginalPath = new SimpleStringProperty(strings[0]);
			this.copyFileTargetPath = new SimpleStringProperty(strings[1]);
			this.copyNumber = new SimpleStringProperty(strings[2]);
			this.isCopy = new SimpleBooleanProperty(Boolean.valueOf(strings[3]));
			this.isDelete = new SimpleBooleanProperty(Boolean.valueOf(strings[4]));
		}

		public String getPropertys() {
			return copyFileOriginalPath.get() + "__" + copyFileTargetPath.get() + "__" + copyNumber.get() + "__"
					+ isCopy.get() + "__" + isDelete.get();
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

		public BooleanProperty isCopyProperty() {
			return isCopy;
		}

		public Boolean getIsCopy() {
			return isCopy.get();
		}

		public void setIsCopy(Boolean isCopy) {
			this.isCopy.set(isCopy);
		}

		public BooleanProperty isDeleteProperty() {
			return isDelete;
		}

		public Boolean getIsDelete() {
			return isDelete.get();
		}

		public void setIsDelete(Boolean isDelete) {
			this.isDelete.set(isDelete);
		}
	}

}
