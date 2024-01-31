package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class MainFrameView implements Initializable {
    @FXML
    protected MenuItem bookManageMenuItem;

    @FXML
    protected AnchorPane mainFrameAnchorPane;

    @FXML
    protected MenuItem bookAddMenuItem;

    @FXML
    protected MenuItem exitMenuItem;

    @FXML
    protected MenuItem bookTypeManageMenuItem;

    @FXML
    protected MenuItem aboutSoftMenuItem;

    @FXML
    protected MenuItem bookTypeAddMenuItem;
}
