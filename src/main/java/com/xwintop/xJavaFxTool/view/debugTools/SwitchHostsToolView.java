package com.xwintop.xJavaFxTool.view.debugTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SwitchHostsToolView implements Initializable {
    @FXML
    protected Button addButton;
    @FXML
    protected Button reloadButton;
    @FXML
    protected Button editButton;
    @FXML
    protected Button deleteButton;
    @FXML
    protected TreeView<String> hostFileTreeView;
    @FXML
    protected TextArea hostTextArea;

}