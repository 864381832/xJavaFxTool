package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BookAddFrameView implements Initializable {
    @FXML
    protected TextField bookAuthorTextField;

    @FXML
    protected RadioButton femaleRadioButton;

    @FXML
    protected TextField bookPriceTextField;

    @FXML
    protected ComboBox<?> bookTypeComboBox;

    @FXML
    protected RadioButton maleRadioButton;

    @FXML
    protected Button addButton;

    @FXML
    protected Button resetButton;

    @FXML
    protected TextField bookNameTextField;

    @FXML
    protected TextArea bookDescriptionTextArea;
}
