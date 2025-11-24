package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * @ClassName: CharacterConverterView
 * @Description: 编码转换工具
 * @author: xufeng
 * @date: 2018/1/25 14:54
 */
public abstract class CharacterConverterView implements Initializable {
    @FXML
    protected AnchorPane mainAnchorPane;
    @FXML
    protected TextField encodeTextField;
    @FXML
    protected Button encodeButton;
    @FXML
    protected ChoiceBox<String> codeTypesBox;
    @FXML
    protected ChoiceBox<String> prefixsBox;
    @FXML
    protected ChoiceBox<String> lowUpCaseBox;
    @FXML
    protected Button clearButton;
    @FXML
    protected VBox mainVBox;
}
