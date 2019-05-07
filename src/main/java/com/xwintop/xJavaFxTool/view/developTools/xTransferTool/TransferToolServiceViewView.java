package com.xwintop.xJavaFxTool.view.developTools.xTransferTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TransferToolServiceViewView implements Initializable {
    @FXML
    protected FlowPane serviceViewFlowPane;
    @FXML
    protected Button saveButton;
    @FXML
    protected TabPane rowTabPane;
}