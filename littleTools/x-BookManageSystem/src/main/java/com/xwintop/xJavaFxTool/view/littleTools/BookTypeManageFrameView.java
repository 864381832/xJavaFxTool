package com.xwintop.xJavaFxTool.view.littleTools;

import com.xwintop.xJavaFxTool.beans.BookTypeBeanTableData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BookTypeManageFrameView implements Initializable {
    @FXML
    protected TextField idTextField;

    @FXML
    protected Button alterButton;

    @FXML
    protected TableColumn<BookTypeBeanTableData, String> idTableColumn;

    @FXML
    protected Button deleteButton;

    @FXML
    protected TableView<BookTypeBeanTableData> bookTypeManageTableView;

    @FXML
    protected TextField bookTypeNameTextField;

    @FXML
    protected TableColumn<BookTypeBeanTableData, String> bookTypeNameColumn;

    @FXML
    protected TextField bookTypeNameTextField2;

    @FXML
    protected TextArea descriptionTextArea;

    @FXML
    protected TableColumn<BookTypeBeanTableData, String> bookTypeDescriptionTableColumn;
}
