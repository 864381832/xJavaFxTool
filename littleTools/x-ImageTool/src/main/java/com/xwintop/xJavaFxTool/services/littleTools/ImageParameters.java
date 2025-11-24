package com.xwintop.xJavaFxTool.services.littleTools;

import lombok.Data;

/**
 * 图像处理参数
 */
@Data
public class ImageParameters {

    /**
     * 是否调整大小
     */
    private boolean resizeImage;

    /**
     * 调整大小的方式
     */
    private ResizeMode resizeMode;

    /**
     * 目标图片宽度
     */
    private int targetWidth;

    /**
     * 目标图片高度
     */
    private int targetHeight;

    /**
     * 是否保持图片比例。
     * 如果为 false 则可能导致图片变形；
     * 如果为 true 则可能生成的图片目标高度和宽度只有一个能满足
     */
    private boolean keepRatio;

    /**
     * 输出格式
     */
    private OutputFormats format;

    /**
     * JPG 图片质量
     */
    private double jpegQuality;

    /**
     * 输出图片文件名后缀
     */
    private String outputFileNameSuffix;

    private double rotate;//图片旋转角度
}
