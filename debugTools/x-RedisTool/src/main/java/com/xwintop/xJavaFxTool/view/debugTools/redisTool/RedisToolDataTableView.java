package com.xwintop.xJavaFxTool.view.debugTools.redisTool;

import com.xwintop.xJavaFxTool.model.RedisToolDataTableBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RedisToolDataTableView implements Initializable {
	@FXML
	protected TableView<RedisToolDataTableBean> dataTableView;
	@FXML
	protected TableColumn<RedisToolDataTableBean,String> dataNameTableColumn;
	@FXML
	protected TableColumn<RedisToolDataTableBean,String> dataTypeTableColumn;
	@FXML
	protected TableColumn<RedisToolDataTableBean,String> dataSizeTableColumn;
	@FXML
	protected TableColumn<RedisToolDataTableBean,String> dataTimeTableColumn;

}