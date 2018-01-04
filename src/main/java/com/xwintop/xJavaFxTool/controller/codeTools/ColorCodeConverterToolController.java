package com.xwintop.xJavaFxTool.controller.codeTools;

import com.jfoenix.skins.JFXColorPickerSkin;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xJavaFxTool.view.codeTools.ColorCodeConverterToolView;
import com.xwintop.xJavaFxTool.services.codeTools.ColorCodeConverterToolService;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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
    }

    private void initEvent() {
        colorSelect1ColorPicker.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            String s = ((Label)((JFXColorPickerSkin)colorSelect1ColorPicker.getSkin()).getDisplayNode()).getText();
            System.out.println(s);
            colorSelect2ColorPicker.setValue(newValue);
            colorCodeConverterToolService.setColorTextField(newValue);
        });
        colorSelect2ColorPicker.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            colorSelect1ColorPicker.setValue(newValue);
        });
    }

    private void initService() {
    }

    @FXML
    private void sysCopyAction(ActionEvent event) {
        XJavaFxSystemUtil.setSystemClipboardContents(sysTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void rgbCopyAction(ActionEvent event) {
        XJavaFxSystemUtil.setSystemClipboardContents(rgbTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void argbCopyAction(ActionEvent event) {
        XJavaFxSystemUtil.setSystemClipboardContents(argbTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void rgbaCopyAction(ActionEvent event) {
        XJavaFxSystemUtil.setSystemClipboardContents(rgbaTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void hslCopyAction(ActionEvent event) {
        XJavaFxSystemUtil.setSystemClipboardContents(hslTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }

    @FXML
    private void hsvCopyAction(ActionEvent event) {
        XJavaFxSystemUtil.setSystemClipboardContents(hsvTextField.getText());
        TooltipUtil.showToast("复制成功！！");
    }
}