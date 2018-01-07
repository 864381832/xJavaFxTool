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
        String hsb = String.format("hsl(%d, %d%%, %d%%)",
                (int) (color.getHue()),
                (int) (color.getSaturation() * 100),
                (int) (color.getBrightness() * 100));
        colorCodeConverterToolController.getColorSelect1ColorPicker().setValue(color);
        colorCodeConverterToolController.getColorSelect2ColorPicker().setValue(color);
        colorCodeConverterToolController.getSysTextField().setText(JFXNodeUtils.colorToHex(color));
        colorCodeConverterToolController.getRgbTextField().setText(String.format("rgb(%d,%d,%d)" ,(int)(color.getRed() * 255),(int)(color.getGreen() * 255),(int)(color.getBlue() * 255)));
        colorCodeConverterToolController.getArgbTextField().setText(String.format("#%02X", color.hashCode()));
        colorCodeConverterToolController.getRgbaTextField().setText(String.format("rgba(%d,%d,%d,%.2f)",(int)(color.getRed() * 255),(int)(color.getGreen() * 255),(int)(color.getBlue() * 255),color.getOpacity()));
        colorCodeConverterToolController.getHslTextField().setText(hsb);
        double[] hsv = RGBtoHSV(color.getRed(), color.getGreen(), color.getBlue());
        colorCodeConverterToolController.getHsvTextField().setText(String.format("hsv(%.2f,%.2f,%.2f)", hsv[0], hsv[1], hsv[2]));
    }

    public ColorCodeConverterToolService(ColorCodeConverterToolController colorCodeConverterToolController) {
        this.colorCodeConverterToolController = colorCodeConverterToolController;
    }

    public static double[] RGBtoHSV(double r, double g, double b) {
        double h, s, v;
        double min, max, delta;
        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);
        // V
        v = max;
        delta = max - min;
        // S
        if (max != 0) {
            s = delta / max;
        } else {
            s = 0;
            h = -1;
            return new double[]{h, s, v};
        }
        // H
        if (r == max) {
            h = (g - b) / delta; // between yellow & magenta
        } else if (g == max) {
            h = 2 + (b - r) / delta; // between cyan & yellow
        } else {
            h = 4 + (r - g) / delta; // between magenta & cyan
        }
        h *= 60;    // degrees
        if (h < 0) {
            h += 360;
        }
        if (Double.isNaN(h)) {
            h = 0;
        }
        return new double[]{h, s, v};
    }

    public static Color hsvToRgb(String hsv) {
        String[] hsvs = hsv.substring(4, hsv.length() - 1).split(",");
        return hsvToRgb(Float.parseFloat(hsvs[0]), Float.parseFloat(hsvs[1]), Float.parseFloat(hsvs[2]));
    }

    public static Color hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue / 60);
        float f = hue / 60 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return new Color(value, t, p, 1);
            case 1:
                return new Color(q, value, p, 1);
            case 2:
                return new Color(p, value, t, 1);
            case 3:
                return new Color(p, q, value, 1);
            case 4:
                return new Color(t, p, value, 1);
            case 5:
                return new Color(value, p, q, 1);
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }
}