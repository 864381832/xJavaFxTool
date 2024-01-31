package com.xwintop.xJavaFxTool.view.littleTools;

import com.xwintop.xJavaFxTool.beans.BookBeanTableData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BookManageFrameView implements Initializable {
    @FXML
    protected TextField idTextField;

    @FXML
    protected Button alterButton;

    @FXML
    protected RadioButton maleRadioButton;

    @FXML
    protected RadioButton femaleRadioButton;

    @FXML
    protected TextField bookAuthorTextField2;

    @FXML
    protected ComboBox bookTypeComboBox2;

    @FXML
    protected TableColumn<BookBeanTableData, String> idTableColumn;

    @FXML
    protected TableColumn<BookBeanTableData, String> authorSexTableColumn;

    @FXML
    protected TableColumn<BookBeanTableData, String> bookPriceTableColumn;

    @FXML
    protected ComboBox<?> bookTypeComboBox;

    @FXML
    protected Button checkButton;

    @FXML
    protected Button resetButton;

    @FXML
    protected Button resetButton2;

    @FXML
    protected TableColumn<BookBeanTableData, String> bookAuthorTableColumn;

    @FXML
    protected TableView<BookBeanTableData> bookManageTableView;

    @FXML
    protected TextArea bookDescriptionTextArea;

    @FXML
    protected TextField bookAuthorTextField;

    @FXML
    protected TableColumn<BookBeanTableData, String> bookNameTableColumn;

    @FXML
    protected TableColumn<BookBeanTableData, String> bookDescriptionTableColumn;

    @FXML
    protected TextField bookNameTextField2;

    @FXML
    protected TextField priceTextField;

    @FXML
    protected Button delteButton;

    @FXML
    protected TextField bookNameTextField;

    @FXML
    protected TableColumn<BookBeanTableData, String> bookTypeTableColumn;
}
