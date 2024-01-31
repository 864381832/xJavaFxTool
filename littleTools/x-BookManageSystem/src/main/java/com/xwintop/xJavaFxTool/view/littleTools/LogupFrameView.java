package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LogupFrameView implements Initializable {
    @FXML
    protected TextField userNameTextField;

    @FXML
    protected PasswordField passwordTextField;

    @FXML
    protected Button logupButton;

    @FXML
    protected Label userNameLabel;

    @FXML
    protected Label systemLabel;

    @FXML
    protected Button resetButton;

    @FXML
    protected Label passwordLabel;
}
