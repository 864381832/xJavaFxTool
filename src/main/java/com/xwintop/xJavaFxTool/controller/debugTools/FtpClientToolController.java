package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.model.FtpClientToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.FtpClientToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.FtpClientToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;

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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
/**
 * @ClassName: FtpClientToolController
 * @Description: Ftp客户端工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:35
 */
@Getter
@Setter
@Log4j
public class FtpClientToolController extends FtpClientToolView {
	private FtpClientToolService ftpClientToolService = new FtpClientToolService(this);
	private ObservableList<FtpClientToolTableBean> tableData = FXCollections.observableArrayList();
	private String[] quartzChoiceBoxStrings = new String[] { "简单表达式", "Cron表达式" };
	private String[] typeChoiceBoxStrings = new String[] { "上传", "下载","删除文件","删除文件夹" };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		ftpClientToolService.loadingConfigure();
		isEnabledTableColumn
				.setCellValueFactory(new PropertyValueFactory<FtpClientToolTableBean, Boolean>("isEnabled"));
		isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));

		localFileTableColumn.setCellValueFactory(new PropertyValueFactory<FtpClientToolTableBean, String>("localFile"));
		localFileTableColumn.setCellFactory(TextFieldTableCell.<FtpClientToolTableBean>forTableColumn());
		localFileTableColumn.setOnEditCommit((CellEditEvent<FtpClientToolTableBean, String> t) -> {
			t.getRowValue().setLocalFile(t.getNewValue());
		});

		serverFileTableColumn
				.setCellValueFactory(new PropertyValueFactory<FtpClientToolTableBean, String>("serverFile"));
		serverFileTableColumn.setCellFactory(TextFieldTableCell.<FtpClientToolTableBean>forTableColumn());
		serverFileTableColumn.setOnEditCommit((CellEditEvent<FtpClientToolTableBean, String> t) -> {
			t.getRowValue().setServerFile(t.getNewValue());
		});

		typeTableColumn.setCellValueFactory(new PropertyValueFactory<FtpClientToolTableBean, String>("type"));
		typeTableColumn.setCellFactory(
				new Callback<TableColumn<FtpClientToolTableBean, String>, TableCell<FtpClientToolTableBean, String>>() {
					@Override
					public TableCell<FtpClientToolTableBean, String> call(
							TableColumn<FtpClientToolTableBean, String> param) {
						TableCell<FtpClientToolTableBean, String> cell = new TableCell<FtpClientToolTableBean, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null);
								this.setGraphic(null);
								if (!empty) {
									ChoiceBox<String> choiceBox = new ChoiceBox<String>();
									choiceBox.getItems().addAll(new String[] { "上传", "下载","删除文件","删除文件夹" });
									choiceBox.setValue(tableData.get(this.getIndex()).getType());
									choiceBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
										tableData.get(this.getIndex()).setType(newVal);
									});
									this.setGraphic(choiceBox);
								}
							}
						};
						return cell;
					}
				});

		manualTableColumn.setCellFactory((col) -> {
			TableCell<FtpClientToolTableBean, Boolean> cell = new TableCell<FtpClientToolTableBean, Boolean>() {
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						Button delBtn = new Button("运行");
						this.setContentDisplay(ContentDisplay.CENTER);
						this.setGraphic(delBtn);
						delBtn.setOnMouseClicked((me) -> {
							FtpClientToolTableBean ftpClientToolTableBean = tableData.get(this.getIndex());
							ftpClientToolService.runAction(ftpClientToolTableBean);
						});
					}
				}
			};
			return cell;
		});

		remarksTableColumn.setCellValueFactory(new PropertyValueFactory<FtpClientToolTableBean, String>("remarks"));
		remarksTableColumn.setCellFactory(TextFieldTableCell.<FtpClientToolTableBean>forTableColumn());
		remarksTableColumn.setOnEditCommit((CellEditEvent<FtpClientToolTableBean, String> t) -> {
			t.getRowValue().setRemarks(t.getNewValue());
		});

		tableViewMain.setItems(tableData);

		quartzChoiceBox.getItems().addAll(quartzChoiceBoxStrings);
		quartzChoiceBox.setValue(quartzChoiceBoxStrings[0]);

		typeChoiceBox.getItems().addAll(typeChoiceBoxStrings);
		typeChoiceBox.setValue(typeChoiceBoxStrings[0]);
		JavaFxViewUtil.setSpinnerValueFactory(intervalSpinner, 60, Integer.MAX_VALUE);
		JavaFxViewUtil.setSpinnerValueFactory(repeatCountSpinner, -1, Integer.MAX_VALUE);
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(localFileTextField, FileChooserUtil.FileType.FILE);
		tableData.addListener((Change<? extends FtpClientToolTableBean> tableBean) -> {
			try {
				saveConfigure(null);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		});
		tableViewMain.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_Copy = new MenuItem("复制选中行");
				menu_Copy.setOnAction(event1 -> {
					FtpClientToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
					FtpClientToolTableBean tableBean2 = new FtpClientToolTableBean(tableBean.getPropertys());
					tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(), tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
					tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				tableViewMain.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
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

	private void initService() {
	}

	@FXML
	private void chooseLocalFileAction(ActionEvent event) {
		File file = FileChooserUtil.chooseFile();
		if (file != null) {
			localFileTextField.setText(file.getPath());
		}
	}

	@FXML
	private void addItemAction(ActionEvent event) {
		tableData.add(new FtpClientToolTableBean(isEnabledCheckBox.isSelected(), localFileTextField.getText(),
				serverFileTextField.getText(), typeChoiceBox.getValue(), " "));
	}

	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		ftpClientToolService.saveConfigure();
	}

	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		ftpClientToolService.otherSaveConfigureAction();
	}

	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		ftpClientToolService.loadingConfigureAction();
	}

	@FXML
	private void runQuartzAction(ActionEvent event) throws Exception {
		if ("定时运行".equals(runQuartzButton.getText())) {
			boolean isTrue = ftpClientToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(),
					intervalSpinner.getValue(), repeatCountSpinner.getValue());
			if (isTrue) {
				runQuartzButton.setText("停止运行");
			}
		} else {
			boolean isTrue = ftpClientToolService.stopQuartzAction();
			if (isTrue) {
				runQuartzButton.setText("定时运行");
			}
		}
	}

	@FXML
	private void runAllAction(ActionEvent event) {
		ftpClientToolService.runAllAction();
	}

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) throws Exception {
        ftpClientToolService.stopQuartzAction();
    }
}