package com.xwintop.xJavaFxTool.view;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class IndexView implements Initializable {

    protected ResourceBundle bundle;

    @FXML
    protected Button myButton;

    @FXML
    protected TextField myTextField;

    @FXML
    protected TabPane tabPaneMain;

    @FXML
    protected MenuBar mainMenuBar;
    @FXML
    protected CheckMenuItem singleWindowBootCheckMenuItem;

    @FXML
    protected Menu fileMenu;

    @FXML
    protected Menu moreToolsMenu;

    @FXML
    protected Menu helpMenu;

    @FXML
    protected WebView tongjiWebView;
}
