package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BookTypeAddFrameView implements Initializable {
    @FXML
    protected TextField bookTypeNameTextField;

    @FXML
    protected Button addButton;

    @FXML
    protected TextArea bookTypeDescriptionTextArea;

    @FXML
    protected Button resetButton;
}
