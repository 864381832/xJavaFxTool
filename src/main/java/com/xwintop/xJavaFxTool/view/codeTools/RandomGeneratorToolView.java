package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: RandomGeneratorToolView
 * @Description: 随机数生成工具
 * @author: xufeng
 * @date: 2019/6/15 0015 0:53
 */

@Getter
@Setter
public abstract class RandomGeneratorToolView implements Initializable {
    @FXML
    protected TextField uuidResult;
    @FXML
    protected TextField floor;
    @FXML
    protected TextField ceil;
    @FXML
    protected TextField ignoreRange;
    @FXML
    protected TextField precision;
    @FXML
    protected TextField numberResult;
    @FXML
    protected TextField emailResult;
    @FXML
    protected TextField lowerCaseLength;
    @FXML
    protected TextField lowerCaseResult;
    @FXML
    protected TextField upperCaseLength;
    @FXML
    protected TextField upperCaseResult;
    @FXML
    protected TextField letterLength;
    @FXML
    protected TextField letterResult;
    @FXML
    protected TextField stringLength;
    @FXML
    protected TextField stringResult;
    @FXML
    protected TextField textLength;
    @FXML
    protected TextField textResult;

}