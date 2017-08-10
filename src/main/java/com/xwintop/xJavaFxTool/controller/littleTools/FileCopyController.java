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
import com.xwintop.xcore.util.javafx.FileChooserUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class FileCopyController implements Initializable {
	@FXML
	private TextField textFieldCopyFileOriginalPath;
	@FXML
	private TextField textFieldCopyFileTargetPath;
	@FXML
	private Button buttonChooseOriginalPath;
	@FXML
	private Button buttonChooseTargetPath;
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
	private TableColumn<TableBean, Boolean> tableColumnIsCopy;
	@FXML
	private TableColumn<TableBean, String> tableColumnIsDelete;
	@FXML
	private CheckBox checkBoxIsDelete;
	@FXML
	private Button buttonAddItem;
	@FXML
	private Button buttonSaveConfigure;
	@FXML
	private Button buttonDeleteSelectRow;

	private ObservableList<TableBean> tableData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		try {
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(ConfigureUtil.getConfigurePath("fileCopyConfigure.properties"));
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
		// tableColumnIsCopy.setCellFactory(
		// CheckBoxTableCell.forTableColumn(new Callback<Integer,
		// ObservableValue<Boolean>>() {
		// @Override
		// public ObservableValue<Boolean> call(Integer arg0) {
		// return null;
		// }
		// }));
//		tableColumnIsCopy.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsCopy));
		tableColumnIsCopy.setCellFactory(new Callback<TableColumn<TableBean, Boolean>, TableCell<TableBean, Boolean>>() {
            public TableCell<TableBean, Boolean> call(TableColumn<TableBean, Boolean> param) {
                final CheckBoxButtonTableCell<TableBean, Boolean> cell = new CheckBoxButtonTableCell<>();
                final CheckBox checkbox = (CheckBox) cell.getGraphic();
                checkbox.setOnAction(new EventHandler<ActionEvent>(){
                  @Override
                  public void handle(ActionEvent event) {
                      System.out.println("进来了");
                      TableBean person = tableData.get(cell.getIndex());
                        if(checkbox.isSelected()){
                        	person.setIsCopy("是");
                        }else{
                        	person.setIsCopy("否");
                        }
                    }
                });
                return cell;
            }
});
		tableColumnIsCopy.setOnEditCommit((CellEditEvent<TableBean, Boolean> t) -> {
			t.getRowValue().setIsCopy(""+t.getNewValue());
		});

		tableColumnIsDelete.setCellValueFactory(new PropertyValueFactory<TableBean, String>("isDelete"));
		tableColumnIsDelete.setCellFactory(TextFieldTableCell.<TableBean>forTableColumn());
		tableColumnIsDelete.setOnEditCommit((CellEditEvent<TableBean, String> t) -> {
			t.getRowValue().setIsDelete(t.getNewValue());
		});

		tableViewMain.setItems(tableData);
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(textFieldCopyFileOriginalPath, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(textFieldCopyFileTargetPath, FileChooserUtil.FileType.FOLDER);
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
				spinnerCopyNumber.getValue().toString(), checkBoxIsCopy.isSelected() ? "是" : "否",
				checkBoxIsDelete.isSelected() ? "是" : "否"));
	}
	
	@FXML
	private void deleteSelectRowAction(ActionEvent event) {
		tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		FileUtils.touch(ConfigureUtil.getConfigureFile("fileCopyConfigure.properties"));
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(ConfigureUtil.getConfigurePath("fileCopyConfigure.properties"));
		xmlConfigure.clear();
		for (int i = 0; i < tableData.size(); i++) {
			xmlConfigure.setProperty("tableBean" + i, tableData.get(i).getPropertys());
		}
		xmlConfigure.save();
	}

	@FXML
	private void copyAction(ActionEvent event) throws Exception {
		for (TableBean tableBean : tableData) {
			if ("是".equals(tableBean.getIsCopy())) {
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
				if ("是".equals(tableBean.getIsDelete())) {
					if (fileOriginal.isDirectory()) {
						FileUtils.deleteDirectory(fileOriginal);
					} else {
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

		public TableBean(String propertys) {
			String[] strings = propertys.split(",");
			this.copyFileOriginalPath = new SimpleStringProperty(strings[0]);
			this.copyFileTargetPath = new SimpleStringProperty(strings[1]);
			this.copyNumber = new SimpleStringProperty(strings[2]);
			this.isCopy = new SimpleStringProperty(strings[3]);
			this.isDelete = new SimpleStringProperty(strings[4]);
		}

		public String getPropertys() {
			return copyFileOriginalPath.get() + "," + copyFileTargetPath.get() + "," + copyNumber.get() + ","
					+ isCopy.get() + "," + isDelete.get();
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
	
	public class CheckBoxButtonTableCell<S, T> extends TableCell<S, T> {
	    private final CheckBox chebox;
	    private ObservableValue<T> ov;

	    public CheckBoxButtonTableCell() {
	        this.chebox = new CheckBox();
	        //添加元素
	        setGraphic(chebox);
	    }

	    @Override
	    protected void updateItem(T item, boolean empty) {
	        System.out.println("empty："+empty);
	        super.updateItem(item, empty);
	        if (empty) {
	            //如果此列为空默认不添加元素
	            setText(null);
	            setGraphic(null);
	        } else {
	            //初始化为不选中
	            chebox.setSelected(false);
	            setGraphic(chebox);
	           
	        }
	    }
	}
}
