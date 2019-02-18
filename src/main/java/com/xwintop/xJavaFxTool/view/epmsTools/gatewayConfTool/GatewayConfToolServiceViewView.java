package com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GatewayConfToolServiceViewView implements Initializable {
    @FXML
    protected FlowPane serviceViewFlowPane;
    @FXML
    protected Button saveButton;

}