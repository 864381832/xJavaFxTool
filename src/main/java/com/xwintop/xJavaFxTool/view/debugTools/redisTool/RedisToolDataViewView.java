package com.xwintop.xJavaFxTool.view.debugTools.redisTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RedisToolDataViewView implements Initializable {
	@FXML
	protected Label serverLabel;
	@FXML
	protected Label databaseLabel;

}