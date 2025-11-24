package com.xwintop.xJavaFxTool.view.codeTools;

import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXColorPicker;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

/**
 * @ClassName: ColorCodeConverterToolView
 * @Description: 颜色代码转换工具
 * @author: xufeng
 * @date: 2018/1/4 16:43
 */
@Getter
@Setter
public abstract class ColorCodeConverterToolView implements Initializable {
    @FXML
    protected TextField sysTextField;
    @FXML
    protected TextField rgbTextField;
    @FXML
    protected TextField argbTextField;
    @FXML
    protected TextField rgbaTextField;
    @FXML
    protected TextField hslTextField;
    @FXML
    protected TextField hsvTextField;
    @FXML
    protected Button sysCopyButton;
    @FXML
    protected Button rgbCopyButton;
    @FXML
    protected Button argbCopyButton;
    @FXML
    protected Button rgbaCopyButton;
    @FXML
    protected Button hslCopyButton;
    @FXML
    protected Button hsvCopyButton;
    @FXML
    protected JFXColorPicker colorSelect1ColorPicker;
    @FXML
    protected ColorPicker colorSelect2ColorPicker;

}