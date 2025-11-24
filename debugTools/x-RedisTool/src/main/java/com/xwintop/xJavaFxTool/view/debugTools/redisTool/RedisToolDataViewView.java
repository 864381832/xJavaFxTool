package com.xwintop.xJavaFxTool.view.debugTools.redisTool;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RedisToolDataViewView implements Initializable {
	@FXML
	protected Label serverLabel;
	@FXML
	protected Label databaseLabel;
	@FXML
	protected CheckBox overdueCheckBox;
	@FXML
	protected TextField overdueTimeTextField;
	@FXML
	protected Button overdueEnterButton;
	@FXML
	protected Button overdueReloadButton;
	@FXML
	protected TextField redisKeyTextField;
	@FXML
	protected HBox valueMapHBox;
	@FXML
	protected HBox valueStringHBox;
	@FXML
	protected HBox valueListHBox;
	@FXML
	protected TableView<Map<String,String>> valueMapTableView;
	@FXML
	protected TableColumn<Map<String,String>,String> valueMapKeyTableColumn;
	@FXML
	protected TableColumn<Map<String,String>,String> valueMapValueTableColumn;
	@FXML
	protected Button valueMapAddButton;
	@FXML
	protected Button valueMapDeleteButton;
	@FXML
	protected Button valueMapEnterButton;
	@FXML
	protected Button valueMapCancelButton;
	@FXML
	protected Button valueMapReloadButton;
	@FXML
	protected Button valueMapShowButton;
	@FXML
	protected TextArea valueStringTextArea;
	@FXML
	protected Button valueStringEnterButton;
	@FXML
	protected Button valueStringCancelButton;
	@FXML
	protected Button valueStringReloadButton;
	@FXML
	protected Button valueStringShowButton;
	@FXML
	protected TableView<Map<String,String>> valueListTableView;
	@FXML
	protected TableColumn<Map<String,String>,String> valueListValueTableColumn;
	@FXML
	protected Button valueListInsertHeadButton;
	@FXML
	protected Button valueListAppendTailButton;
	@FXML
	protected Button valueListDeleteHeadButton;
	@FXML
	protected Button valueListDeleteTailButton;
	@FXML
	protected Button valueListDeleteButton;
	@FXML
	protected Button valueListEnterButton;
	@FXML
	protected Button valueListCancelButton;
	@FXML
	protected Button valueListReloadButton;
	@FXML
	protected Button valueListShowButton;

}