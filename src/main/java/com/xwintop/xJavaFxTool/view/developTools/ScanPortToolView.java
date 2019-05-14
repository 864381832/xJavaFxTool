package com.xwintop.xJavaFxTool.view.developTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ScanPortToolView implements Initializable {
    @FXML
    protected TextField hostTextField;
    @FXML
    protected Button scanButton;
    @FXML
    protected TextField diyPortTextField;
    @FXML
    protected FlowPane commonPortFlowPane;
    @FXML
    protected TextArea connectLogTextArea;

}