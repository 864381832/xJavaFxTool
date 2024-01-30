package com.xwintop.xJavaFxTool.view.developTools;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
/**
 * @ClassName: JsonConvertToolView
 * @Description: Json转换工具
 * @author: xufeng
 * @date: 2018/2/5 17:04
 */

@Getter
@Setter
public abstract class JsonConvertToolView implements Initializable {
    @FXML
    protected TextArea jsonTextArea;
    @FXML
    protected TextArea afterTextArea;

}