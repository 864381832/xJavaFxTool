package com.xwintop.xJavaFxTool.datafx.flow2;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.LinkAction;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@FXMLController("view2.fxml")
public class View2Controller {
 
    @FXML
    @LinkAction(View1Controller.class)
    private Button actionButton;
}
