package com.xwintop.xJavaFxTool.view.debugTools.redisTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RedisToolView implements Initializable {
	@FXML
	protected Button treeLeftButton;
	@FXML
	protected Button treeRightButton;
	@FXML
	protected Button treeUpButton;
	@FXML
	protected Button treeRefurbishButton;
	@FXML
	protected TreeView<String> redisServiceTreeView;
	@FXML
	protected TabPane dataServiceTabPane;
	@FXML
	protected TabPane dataViewTabPane;

}
