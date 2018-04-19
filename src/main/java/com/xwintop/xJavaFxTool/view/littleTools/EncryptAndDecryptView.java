package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public abstract class EncryptAndDecryptView implements Initializable {
    @FXML
    protected AnchorPane mainAnchorPane;
    @FXML
    protected TextArea encrptyTextArea;
    @FXML
    protected TextArea decrptyTextArea;
    @FXML
    protected ChoiceBox<String> charsetsBox;
    @FXML
    protected TextField keyTextField;
    @FXML
    protected Button encrptyButton;
    @FXML
    protected Button decrptyButton;
}
