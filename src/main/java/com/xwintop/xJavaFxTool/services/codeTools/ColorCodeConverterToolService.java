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

    public void setColorTextField(Color color) {
//        String hex = String.format("#%02X%02X%02X",
//                (int) (newColor.getRed() * 255),
//                (int) (newColor.getGreen() * 255),
//                (int) (newColor.getBlue() * 255));
//        String rgb = String.format("rgba(%d, %d, %d, %f)",
//                (int) (newColor.getRed() * 255),
//                (int) (newColor.getGreen() * 255),
//                (int) (newColor.getBlue() * 255),
//                newColor.getOpacity());
        String hsb = String.format("hsl(%d, %d%%, %d%%)",
                (int) (color.getHue()),
                (int) (color.getSaturation() * 100),
                (int) (color.getBrightness() * 100));
        colorCodeConverterToolController.getColorSelect1ColorPicker().setValue(color);
        colorCodeConverterToolController.getColorSelect2ColorPicker().setValue(color);
        colorCodeConverterToolController.getSysTextField().setText(JFXNodeUtils.colorToHex(color));
        colorCodeConverterToolController.getRgbTextField().setText("rgb(" + (int) (color.getRed() * 255) + "," + (int) (color.getGreen() * 255) + "," + (int) (color.getBlue() * 255) + ")");
        colorCodeConverterToolController.getArgbTextField().setText("" + String.format("#%02X", color.hashCode()));
        colorCodeConverterToolController.getRgbaTextField().setText("rgba(" + (int) (color.getRed() * 255) + "," + (int) (color.getGreen() * 255) + "," + (int) (color.getBlue() * 255) + "," + String.format("%.2f", color.getOpacity()) + ")");
        colorCodeConverterToolController.getHslTextField().setText(hsb);
//        colorCodeConverterToolController.getHsvTextField().setText("hsv(");
    }

    public ColorCodeConverterToolService(ColorCodeConverterToolController colorCodeConverterToolController) {
        this.colorCodeConverterToolController = colorCodeConverterToolController;
    }

}