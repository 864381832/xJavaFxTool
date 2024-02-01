package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: JavaServiceView
 * @Description: java服务安装工具
 * @author: xufeng
 * @date: 2020/9/22 13:20
 */

@Getter
@Setter
public abstract class JavaServiceView implements Initializable {
    @FXML
    protected TextField idTextField;
    @FXML
    protected TextField nameTextField;
    @FXML
    protected TextField descriptionTextField;
    @FXML
    protected TextField executableTextField;
    @FXML
    protected TextField argumentsTextField;
    @FXML
    protected TextField startmodeTextField;
    @FXML
    protected TextField logpathTextField;
    @FXML
    protected TextField logmodeTextField;
    @FXML
    protected TextField jarPathTextField;
    @FXML
    protected Button jarPathButton;

}