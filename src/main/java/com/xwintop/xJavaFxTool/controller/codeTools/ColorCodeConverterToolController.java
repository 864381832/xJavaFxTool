package com.xwintop.xJavaFxTool.controller.codeTools;

import com.jfoenix.skins.JFXColorPickerSkin;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import com.xwintop.xJavaFxTool.services.codeTools.ColorCodeConverterToolService;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xJavaFxTool.view.codeTools.ColorCodeConverterToolView;
import com.xwintop.xcore.util.javafx.ClipboardUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ColorCodeConverterToolController
 * @Description: 颜色代码转换工具
 * @author: xufeng
 * @date: 2018/1/4 16:43
 */
@Getter
@Setter
@Log4j
public class ColorCodeConverterToolController extends ColorCodeConverterToolView {
    private ColorCodeConverterToolService colorCodeConverterToolService = new ColorCodeConverterToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        colorCodeConverterToolService.setColorTextField(colorSelect1ColorPicker.getValue());
    }

    private void initEvent() {
//        colorSelect1ColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
//            colorCodeConverterToolService.setColorTextField(newValue);
//        });
//        colorSelect2ColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
//            colorCodeConverterToolService.setColorTextField(newValue);
//        });
        colorSelect1ColorPicker.setOnAction((value)->{
            colorCodeConverterToolService.setColorTextField(colorSelect1ColorPicker.getValue());
        });
        colorSelect2ColorPicker.setOnAction((value)->{
            colorCodeConverterToolService.setColorTextField(colorSelect2ColorPicker.getValue());
        });
        sysTextField.setOnKeyReleased((event) -> {
            colorCodeConverterToolService.setColorTextField(Color.valueOf(((TextField)event.getSource()).getText()));
        });
        rgbTextField.setOnKeyReleased((event) -> {
            colorCodeConverterToolService.setColorTextField(Color.valueOf(((TextField)event.getSource()).getText()));
        });
        argbTextField.setOnKeyReleased((event) -> {
            colorCodeConverterToolService.setColorTextField(Color.valueOf(((TextField)event.getSource()).getText()));
        });
        rgbaTextField.setOnKeyReleased((event) -> {
            colorCodeConverterToolService.setColorTextField(Color.valueOf(((TextField)event.getSource()).getText()));
        });
        hslTextField.setOnKeyReleased((event) -> {
            colorCodeConverterToolService.setColorTextField(Color.valueOf(((TextField)event.getSource()).getText()));
        });
        hsvTextField.setOnKeyReleased((event) -> {
            colorCodeConverterToolService.setColorTextField(ColorCodeConverterToolService.hsvToRgb(((TextField)event.getSource()).getText()));
        });

//        sysTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            colorCodeConverterToolService.setColorTextField(Color.valueOf(newValue));
//        });
//        rgbTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            colorCodeConverterToolService.setColorTextField(Color.valueOf(newValue));
//        });
////        argbTextField.textProperty().addListener((observable, oldValue, newValue) -> {
////            colorCodeConverterToolService.setColorTextField(Color.valueOf(newValue));
////        });
//        rgbaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            colorCodeConverterToolService.setColorTextField(Color.valueOf(newValue));
//        });
//        hslTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            colorCodeConverterToolService.setColorTextField(Color.valueOf(newValue));
//        });
////        hsvTextField.textProperty().addListener((observable, oldValue, newValue) -> {
////            colorCodeConverterToolService.setColorTextField(Color.valueOf(newValue));
////        });
    }

    private void initService() {
    }

    @FXML
    private void sysCopyAction(ActionEvent event) {
        ClipboardUtil.setStr(sysTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void rgbCopyAction(ActionEvent event) {
        ClipboardUtil.setStr(rgbTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void argbCopyAction(ActionEvent event) {
        ClipboardUtil.setStr(argbTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void rgbaCopyAction(ActionEvent event) {
        ClipboardUtil.setStr(rgbaTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void hslCopyAction(ActionEvent event) {
        ClipboardUtil.setStr(hslTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void hsvCopyAction(ActionEvent event) {
        ClipboardUtil.setStr(hsvTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }
}