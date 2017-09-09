package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.model.RedisToolDataTableBean;
import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolDataTableService;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolDataTableView;
import com.xwintop.xcore.util.RedisUtil;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView.TableViewSelectionModel;
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
	private RedisToolDataTableService redisToolDataTableService = new RedisToolDataTableService(this);
	private RedisToolController redisToolController;
	private RedisUtil redisUtil;
	private ObservableList<RedisToolDataTableBean> tableData = FXCollections.observableArrayList();

	public static FXMLLoader getFXMLLoader() {
		FXMLLoader fXMLLoader = new FXMLLoader(
				IndexController.class.getResource("/fxml/debugTools/redisTool/RedisToolDataTable.fxml"));
		return fXMLLoader;
	}

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
					// RedisToolDataTableBean tableBean =
					// dataTableView.getSelectionModel().getSelectedItem();
					// RedisToolDataTableBean tableBean2 = new
					// RedisToolDataTableBean(tableBean.getPropertys());
					// tableData.add(dataTableView.getSelectionModel().getSelectedIndex(),
					// tableBean2);
				});
				MenuItem menu_Remove = new MenuItem("删除选中行");
				menu_Remove.setOnAction(event1 -> {
				});
				MenuItem menu_RemoveAll = new MenuItem("删除所有");
				menu_RemoveAll.setOnAction(event1 -> {
					tableData.clear();
				});
				dataTableView.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
			} else if (event.getButton() == MouseButton.PRIMARY) {
				RedisToolDataTableBean redisToolDataTableBean = dataTableView.getSelectionModel().getSelectedItem();
				System.out.println(redisToolDataTableBean.getName());
				redisToolDataTableService.addRedisToolDataViewTab(redisToolDataTableBean.getName());
			}
		});
	}

	private void initService() {
	}

	public void setData(RedisToolController redisToolController, RedisUtil redisUtil) {
		this.redisToolController = redisToolController;
		this.redisUtil = redisUtil;
		tableData.clear();
		Set<String> nodekeys = redisUtil.getListKeys();
		for (String key : nodekeys) {
			System.out.println(key);
			String type = redisUtil.getValueType(key);
			String size = redisUtil.getSize(key).toString();
			Long timeLong = redisUtil.getDeadline(key);
			String time = timeLong == -1 ? "永久" : timeLong.toString();
			RedisToolDataTableBean redisToolDataTableBean = new RedisToolDataTableBean(key, type, size, time);
			tableData.add(redisToolDataTableBean);
		}
	}
}