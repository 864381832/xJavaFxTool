package com.xwintop.xJavaFxTool.services.codeTools;

import com.jfoenix.utils.JFXNodeUtils;
import com.xwintop.xJavaFxTool.controller.codeTools.ColorCodeConverterToolController;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.Locale;

/**
 * @ClassName: ColorCodeConverterToolService
 * @Description: 颜色代码转换工具
 * @author: xufeng
 * @date: 2018/1/4 16:43
 */
@Getter
@Setter
@Log4j
public class ColorCodeConverterToolService {
    private ColorCodeConverterToolController colorCodeConverterToolController;

    public void setColorTextField(Color color){
        colorCodeConverterToolController.getSysTextField().setText(JFXNodeUtils.colorToHex(color));
        colorCodeConverterToolController.getRgbTextField().setText("rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")");
        colorCodeConverterToolController.getArgbTextField().setText("");
        colorCodeConverterToolController.getRgbaTextField().setText("rgba("+color.getRed()+","+color.getGreen()+","+color.getBlue()+","+color.getOpacity()+")");
        colorCodeConverterToolController.getHslTextField().setText("hsl(");
        colorCodeConverterToolController.getHsvTextField().setText("hsv(");
    }

    public ColorCodeConverterToolService(ColorCodeConverterToolController colorCodeConverterToolController) {
        this.colorCodeConverterToolController = colorCodeConverterToolController;
    }

}