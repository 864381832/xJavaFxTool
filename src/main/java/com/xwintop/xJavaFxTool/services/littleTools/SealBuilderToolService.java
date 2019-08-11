package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.SealBuilderToolController;
import com.xwintop.xJavaFxTool.utils.ImgToolUtil;
import com.xwintop.xJavaFxTool.utils.SealUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;

@Getter
@Setter
@Slf4j
public class SealBuilderToolService {
    private SealBuilderToolController sealBuilderToolController;

    public SealBuilderToolService(SealBuilderToolController sealBuilderToolController) {
        this.sealBuilderToolController = sealBuilderToolController;
    }

    public void builderAction() throws Exception {
        SealUtil.SealCircle borderCircle = new SealUtil.SealCircle();
        borderCircle.setLineSize(sealBuilderToolController.getLineSizeSpinner().getValue());
        borderCircle.setCircleWidth(sealBuilderToolController.getCircleWidthSpinner().getValue());
        borderCircle.setCircleHeight(sealBuilderToolController.getCircleHeightSpinner().getValue());
        SealUtil.SealFont mainFont = new SealUtil.SealFont();
        mainFont.setFontText(sealBuilderToolController.getContentTextField().getText().trim());
        mainFont.setFontFamily(sealBuilderToolController.getFontFamilyChoiceBox().getValue());
        mainFont.setFontSize(sealBuilderToolController.getFontSizeSpinner().getValue());
        mainFont.setFontSpace(sealBuilderToolController.getFontSpaceSpinner().getValue());
        mainFont.setMarginSize(sealBuilderToolController.getMarginSizeSpinner().getValue());
        mainFont.setIsBold(sealBuilderToolController.getIsBoldCheckBox().isSelected());
        SealUtil.SealFont viceFont = new SealUtil.SealFont();
        viceFont.setFontText(sealBuilderToolController.getContentTextField1().getText().trim());
        viceFont.setFontFamily(sealBuilderToolController.getFontFamilyChoiceBox1().getValue());
        viceFont.setFontSize(sealBuilderToolController.getFontSizeSpinner1().getValue());
        viceFont.setFontSpace(sealBuilderToolController.getFontSpaceSpinner1().getValue());
        viceFont.setMarginSize(sealBuilderToolController.getMarginSizeSpinner1().getValue());
        viceFont.setIsBold(sealBuilderToolController.getIsBoldCheckBox1().isSelected());
        SealUtil.SealFont titleFont = new SealUtil.SealFont();
        titleFont.setFontText(sealBuilderToolController.getContentTextField2().getText().trim());
        titleFont.setFontFamily(sealBuilderToolController.getFontFamilyChoiceBox2().getValue());
        titleFont.setFontSize(sealBuilderToolController.getFontSizeSpinner2().getValue());
        titleFont.setFontSpace(sealBuilderToolController.getFontSpaceSpinner2().getValue());
        titleFont.setMarginSize(sealBuilderToolController.getMarginSizeSpinner2().getValue());
        titleFont.setIsBold(sealBuilderToolController.getIsBoldCheckBox2().isSelected());
        SealUtil.SealFont centerFont = new SealUtil.SealFont();
        centerFont.setFontText(sealBuilderToolController.getContentTextField3().getText().trim());
        centerFont.setFontFamily(sealBuilderToolController.getFontFamilyChoiceBox3().getValue());
        centerFont.setFontSize(sealBuilderToolController.getFontSizeSpinner3().getValue());
        centerFont.setFontSpace(sealBuilderToolController.getFontSpaceSpinner3().getValue());
        centerFont.setMarginSize(sealBuilderToolController.getMarginSizeSpinner3().getValue());
        centerFont.setIsBold(sealBuilderToolController.getIsBoldCheckBox3().isSelected());

        BufferedImage bufferedImage = SealUtil.builder()
                .size(sealBuilderToolController.getImageSizeSpinner().getValue())
                .color(ImgToolUtil.getAwtColor(sealBuilderToolController.getOnColorColorPicker().getValue()))
                .borderCircle(borderCircle)
                .mainFont(mainFont)
                .centerFont(centerFont)
                .titleFont(titleFont)
                .build()
                .draw();
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        sealBuilderToolController.getCodeImageView().setImage(image);
    }
}