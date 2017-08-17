package com.xwintop.xJavaFxTool.datafx.flow2;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.LinkAction;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@FXMLController("view1.fxml")
public class View1Controller {
 
    @FXML
    @LinkAction(View2Controller.class)
    private Button actionButton;
}
