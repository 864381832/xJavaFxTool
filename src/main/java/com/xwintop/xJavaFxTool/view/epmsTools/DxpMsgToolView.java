package com.xwintop.xJavaFxTool.view.epmsTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: DxpMsgToolView
 * @Description: dxp报文解析工具
 * @author: xufeng
 * @date: 2019/12/20 10:10
 */

@Getter
@Setter
public abstract class DxpMsgToolView implements Initializable {
    @FXML
    protected TextField copMsgIdTextField;
    @FXML
    protected TextField fileNameTextField;
    @FXML
    protected TextField msgTypeTextField;
    @FXML
    protected TextField creatTimeTextField;
    @FXML
    protected TextArea dataTextArea;
    @FXML
    protected TextField savePathTextField;
    @FXML
    protected Button createButton;
    @FXML
    protected Button parserButton;
}