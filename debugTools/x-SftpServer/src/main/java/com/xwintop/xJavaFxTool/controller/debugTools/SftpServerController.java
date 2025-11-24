package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.model.SftpServerTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.SftpServerService;
import com.xwintop.xJavaFxTool.view.debugTools.SftpServerView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: FtpServerController
 * @Description: Ftp服务器
 * @author: xufeng
 * @date: 2019/4/25 0025 23:22
 */

@Getter
@Setter
@Slf4j
public class SftpServerController extends SftpServerView {
	private SftpServerService sftpServerService = new SftpServerService(this);
	private ObservableList<SftpServerTableBean> tableData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		sftpServerService.loadingConfigure();
		userNameTableColumn.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, String>("userName"));
		userNameTableColumn.setCellFactory(TextFieldTableCell.<SftpServerTableBean>forTableColumn());
		userNameTableColumn.setOnEditCommit((CellEditEvent<SftpServerTableBean, String> t) -> {
			t.getRowValue().setUserName(t.getNewValue());
		});

		passwordTableColumn.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, String>("password"));
		passwordTableColumn.setCellFactory(TextFieldTableCell.<SftpServerTableBean>forTableColumn());
		passwordTableColumn.setOnEditCommit((CellEditEvent<SftpServerTableBean, String> t) -> {
			t.getRowValue().setPassword(t.getNewValue());
		});

		homeDirectoryTableColumn
				.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, String>("homeDirectory"));
		homeDirectoryTableColumn.setCellFactory(TextFieldTableCell.<SftpServerTableBean>forTableColumn());
		homeDirectoryTableColumn.setOnEditCommit((CellEditEvent<SftpServerTableBean, String> t) -> {
			t.getRowValue().setHomeDirectory(t.getNewValue());
		});

		downFIleTableColumn.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, Boolean>("downFIle"));
		downFIleTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(downFIleTableColumn));
		upFileTableColumn.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, Boolean>("upFile"));
		upFileTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(upFileTableColumn));
		deleteFileTableColumn.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, Boolean>("deleteFile"));
		deleteFileTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(deleteFileTableColumn));
		isEnabledTableColumn.setCellValueFactory(new PropertyValueFactory<SftpServerTableBean, Boolean>("isEnabled"));
		isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));
		tableViewMain.setItems(tableData);

		JavaFxViewUtil.setSpinnerValueFactory(maxConnectCountSpinner, 1, Integer.MAX_VALUE, 1000);
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(homeDirectoryTextField, FileChooserUtil.FileType.FOLDER);
		FileChooserUtil.setOnDrag(anonymousLoginEnabledTextField, FileChooserUtil.FileType.FOLDER);
		tableData.addListener((Change<? extends SftpServerTableBean> tableBean)->{
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
					SftpServerTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
					SftpServerTableBean tableBean2 = new SftpServerTableBean(tableBean.getPropertys());
					tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(), tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					deleteSelectRowAction(null);
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				tableViewMain.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
			}
		});
		anonymousLoginEnabledCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue){
					anonymousLoginEnabledTextField.setDisable(false);
					anonymousLoginEnabledButton.setDisable(false);
				}else{
					anonymousLoginEnabledTextField.setDisable(true);
					anonymousLoginEnabledButton.setDisable(true);
				}
			}
		});
	}

	private void initService() {
	}
	
	@FXML
	private void chooseHomeDirectoryAction(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			homeDirectoryTextField.setText(file.getPath());
		}
	}

	@FXML
	private void anonymousLoginEnabledAction(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			anonymousLoginEnabledTextField.setText(file.getPath());
		}
	}

	@FXML
	private void addItemAction(ActionEvent event) {
		tableData.add(new SftpServerTableBean(true,userNameTextField.getText(), passwordTextField.getText(),
				homeDirectoryTextField.getText(), downFileCheckBox.isSelected(), upFileCheckBox.isSelected(),
				deleteFileCheckBox.isSelected()));
	}

	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		sftpServerService.saveConfigure();
	}

	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		sftpServerService.otherSaveConfigureAction();
	}

	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		sftpServerService.loadingConfigureAction();
	}

	@FXML
	private void deleteSelectRowAction(ActionEvent event) {
		tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void startAction(ActionEvent event) throws Exception{
		if ("启动".equals(startButton.getText())) {
			try {
				boolean isTrue = sftpServerService.runFtpServerAction();
				if (isTrue) {
					startButton.setText("停止");
				}
				TooltipUtil.showToast("SftpServer启动成功。");
			} catch (Exception e) {
				TooltipUtil.showToast("启动失败：" + e.getMessage());
				log.error("启动失败：" + e.getMessage());
			}
		} else {
			boolean isTrue = sftpServerService.stopFtpServerAction();
			if (isTrue) {
				startButton.setText("启动");
			}
		}
	}

	/**
	 * 父控件被移除前调用
	 */
	public void onCloseRequest(Event event) throws Exception {
		sftpServerService.stopFtpServerAction();
	}
}