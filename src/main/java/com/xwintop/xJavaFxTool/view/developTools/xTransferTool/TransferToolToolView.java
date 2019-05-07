package com.xwintop.xJavaFxTool.view.developTools.xTransferTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TransferToolToolView implements Initializable {
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
    protected PasswordField passwordTextField;
    @FXML
    protected Button connectButton;

}