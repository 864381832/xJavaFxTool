package com.xwintop.xJavaFxTool.controller.debugTools;

import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.model.CmdToolTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.CmdToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.debugTools.CmdToolView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

@Getter
@Setter
@Log4j
public class CmdToolController extends CmdToolView {
	private CmdToolService cmdToolService = new CmdToolService(this);
	private ObservableList<CmdToolTableBean> tableData = FXCollections.observableArrayList();
	private String[] quartzChoiceBoxStrings = new String[] { "简单表达式", "Cron表达式" };
	private String[] typeChoiceBoxStrings = new String[] { "命令行", "脚本文件" };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		cmdToolService.loadingConfigure();
		orderTableColumn.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, String>("order"));
		orderTableColumn.setCellFactory(TextFieldTableCell.<CmdToolTableBean>forTableColumn());
		orderTableColumn.setOnEditCommit((CellEditEvent<CmdToolTableBean, String> t) -> {
			t.getRowValue().setOrder(t.getNewValue());
		});

		isEnabledTableColumn.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, Boolean>("isEnabled"));
		isEnabledTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEnabledTableColumn));

		scriptTableColumn.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, String>("script"));
		scriptTableColumn.setCellFactory(TextFieldTableCell.<CmdToolTableBean>forTableColumn());
		scriptTableColumn.setOnEditCommit((CellEditEvent<CmdToolTableBean, String> t) -> {
			t.getRowValue().setScript(t.getNewValue());
		});
		
		viewScriptTableColumn.setCellFactory((col) -> {
			TableCell<CmdToolTableBean, Boolean> cell = new TableCell<CmdToolTableBean, Boolean>() {
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						Button delBtn = new Button("查看");
						this.setContentDisplay(ContentDisplay.CENTER);
						this.setGraphic(delBtn);
						delBtn.setOnMouseClicked((me) -> {
							CmdToolTableBean cmdToolTableBean = tableData.get(this.getIndex());
							cmdToolService.showScriptAction(cmdToolTableBean);
						});
					}
				}
			};
			return cell;
		});

		typeTableColumn.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, String>("type"));
		typeTableColumn.setCellFactory(
				new Callback<TableColumn<CmdToolTableBean, String>, TableCell<CmdToolTableBean, String>>() {
					@Override
					public TableCell<CmdToolTableBean, String> call(TableColumn<CmdToolTableBean, String> param) {
						TableCell<CmdToolTableBean, String> cell = new TableCell<CmdToolTableBean, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null);
								this.setGraphic(null);
								if (!empty) {
									ChoiceBox<String> choiceBox = new ChoiceBox<String>();
									choiceBox.getItems().addAll(new String[] { "命令行", "脚本文件" });
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

		parameterTableColumn.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, String>("parameter"));
		parameterTableColumn.setCellFactory(TextFieldTableCell.<CmdToolTableBean>forTableColumn());
		parameterTableColumn.setOnEditCommit((CellEditEvent<CmdToolTableBean, String> t) -> {
			t.getRowValue().setParameter(t.getNewValue());
		});

		manualTableColumn.setCellFactory((col) -> {
			TableCell<CmdToolTableBean, Boolean> cell = new TableCell<CmdToolTableBean, Boolean>() {
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						Button delBtn = new Button("执行");
						this.setContentDisplay(ContentDisplay.CENTER);
						this.setGraphic(delBtn);
						delBtn.setOnMouseClicked((me) -> {
							CmdToolTableBean cmdToolTableBean = tableData.get(this.getIndex());
							cmdToolService.runAction(cmdToolTableBean);
						});
					}
				}
			};
			return cell;
		});

		isRunAfterActivateTableColumn
				.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, Boolean>("isRunAfterActivate"));
		isRunAfterActivateTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isRunAfterActivateTableColumn));

		runAfterActivateTableColumn
				.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, String>("runAfterActivate"));
		runAfterActivateTableColumn.setCellFactory(TextFieldTableCell.<CmdToolTableBean>forTableColumn());
		runAfterActivateTableColumn.setOnEditCommit((CellEditEvent<CmdToolTableBean, String> t) -> {
			t.getRowValue().setRunAfterActivate(t.getNewValue());
		});

		remarksTableColumn.setCellValueFactory(new PropertyValueFactory<CmdToolTableBean, String>("remarks"));
		remarksTableColumn.setCellFactory(TextFieldTableCell.<CmdToolTableBean>forTableColumn());
		remarksTableColumn.setOnEditCommit((CellEditEvent<CmdToolTableBean, String> t) -> {
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
		tableData.addListener((Change<? extends CmdToolTableBean> tableBean) -> {
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
					CmdToolTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
					CmdToolTableBean tableBean2 = new CmdToolTableBean(tableBean.getPropertys());
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
	private void addItemAction(ActionEvent event) {
		String id = "x"+String.valueOf(System.currentTimeMillis()).substring(7);
		tableData.add(new CmdToolTableBean(id, isEnabledCheckBox.isSelected(),
				scriptTextField.getText(), typeChoiceBox.getValue(), parameterTextField.getText(), false, " ", " "));
	}

	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		cmdToolService.saveConfigure();
	}

	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		cmdToolService.otherSaveConfigureAction();
	}

	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		cmdToolService.loadingConfigureAction();
	}

	@FXML
	private void runQuartzAction(ActionEvent event) throws Exception {
		if("定时运行".equals(runQuartzButton.getText())){
			boolean isTrue = cmdToolService.runQuartzAction(quartzChoiceBox.getValue(), cronTextField.getText(), intervalSpinner.getValue(),
					repeatCountSpinner.getValue());
			if(isTrue){
				runQuartzButton.setText("停止运行");
			}
		}else{
			boolean isTrue = cmdToolService.stopQuartzAction();
			if(isTrue){
				runQuartzButton.setText("定时运行");
			}
		}
	}

	@FXML
	private void runAllAction(ActionEvent event) {
		cmdToolService.runAllAction();
	}
}