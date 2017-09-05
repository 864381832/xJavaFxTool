package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.model.ActiveMqToolTableBean;
import com.xwintop.xJavaFxTool.model.RedisToolDataTableBean;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolDataTableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class RedisToolDataTableController extends RedisToolDataTableView {
	private ObservableList<RedisToolDataTableBean> tableData = FXCollections.observableArrayList();
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		dataNameTableColumn.setCellValueFactory(new PropertyValueFactory<RedisToolDataTableBean, String>("name"));
		dataNameTableColumn.setCellFactory(TextFieldTableCell.<RedisToolDataTableBean>forTableColumn());
		dataNameTableColumn.setOnEditCommit((CellEditEvent<RedisToolDataTableBean, String> t) -> {
			t.getRowValue().setName(t.getNewValue());
		});
		dataTypeTableColumn.setCellValueFactory(new PropertyValueFactory<RedisToolDataTableBean, String>("type"));
		dataTypeTableColumn.setCellFactory(TextFieldTableCell.<RedisToolDataTableBean>forTableColumn());
		dataTypeTableColumn.setOnEditCommit((CellEditEvent<RedisToolDataTableBean, String> t) -> {
			t.getRowValue().setType(t.getNewValue());
		});
		dataSizeTableColumn.setCellValueFactory(new PropertyValueFactory<RedisToolDataTableBean, String>("size"));
		dataSizeTableColumn.setCellFactory(TextFieldTableCell.<RedisToolDataTableBean>forTableColumn());
		dataSizeTableColumn.setOnEditCommit((CellEditEvent<RedisToolDataTableBean, String> t) -> {
			t.getRowValue().setSize(t.getNewValue());
		});
		dataTimeTableColumn.setCellValueFactory(new PropertyValueFactory<RedisToolDataTableBean, String>("time"));
		dataTimeTableColumn.setCellFactory(TextFieldTableCell.<RedisToolDataTableBean>forTableColumn());
		dataTimeTableColumn.setOnEditCommit((CellEditEvent<RedisToolDataTableBean, String> t) -> {
			t.getRowValue().setTime(t.getNewValue());
		});
		dataTableView.setItems(tableData);
	}

	private void initEvent() {
		dataTableView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_Copy = new MenuItem("复制选中行");
				menu_Copy.setOnAction(event1 -> {
//					RedisToolDataTableBean tableBean = dataTableView.getSelectionModel().getSelectedItem();
//					RedisToolDataTableBean tableBean2 = new RedisToolDataTableBean(tableBean.getPropertys());
//					tableData.add(dataTableView.getSelectionModel().getSelectedIndex(), tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				dataTableView.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
			}
		});
	}

	private void initService() {
	}
}