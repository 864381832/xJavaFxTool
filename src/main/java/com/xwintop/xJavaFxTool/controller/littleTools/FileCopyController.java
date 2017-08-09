package com.xwintop.xJavaFxTool.controller.littleTools;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private Spinner spinnerCopyNumber;
	@FXML
	private TableView tableViewMain;
	@FXML
	private TableColumn tableColumnCopyFileOriginalPath;
	@FXML
	private TableColumn tableColumnCopyFileTargetPath;
	@FXML
	private TableColumn tableColumnCopyNumber;
	@FXML
	private TableColumn tableColumnIsCopy;
	@FXML
	private TableColumn tableColumnIsDelete;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		tableColumnCopyFileOriginalPath.setCellValueFactory(new PropertyValueFactory<>("CopyFileOriginalPath"));
		tableColumnCopyFileTargetPath.setCellValueFactory(new PropertyValueFactory<>("CopyFileTargetPath"));
		tableColumnCopyNumber.setCellValueFactory(new PropertyValueFactory<>("CopyNumber"));
		tableColumnIsCopy.setCellValueFactory(new PropertyValueFactory<>("IsCopy"));
		tableColumnIsDelete.setCellValueFactory(new PropertyValueFactory<>("IsDelete"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CopyFileOriginalPath", "1");
		map.put("CopyFileTargetPath", "1");
		map.put("CopyNumber", "1");
		map.put("IsCopy", "2");
		map.put("IsDelete", "1");
		ObservableList<Map> data = FXCollections.observableArrayList(map);
		
		tableViewMain.setItems(data);
	}

	private void initEvent() {
	}

	@FXML
	private void copyAction(ActionEvent event) {
	}
}
