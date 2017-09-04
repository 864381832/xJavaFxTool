package com.xwintop.xJavaFxTool.view.debugTools.redisTool;

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
	protected TableView dataTableView;
	@FXML
	protected TableColumn dataNameTableColumn;
	@FXML
	protected TableColumn dataTypeTableColumn;
	@FXML
	protected TableColumn dataSizeTableColumn;
	@FXML
	protected TableColumn dataTimeTableColumn;

}