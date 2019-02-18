package com.xwintop.xJavaFxTool.view.epmsTools.gatewayConfTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GatewayConfToolView implements Initializable {
    @FXML
    protected TextField configurationPathTextField;
    @FXML
    protected Button treeRefurbishButton;
    @FXML
    protected TreeView<String> configurationTreeView;
    @FXML
    protected TabPane taskConfigTabPane;
    @FXML
    protected TextField hostTextField;
    @FXML
    protected TextField portTextField;
    @FXML
    protected TextField usernameTextField;
    @FXML
    protected TextField passwordTextField;
    @FXML
    protected Button connectButton;

}