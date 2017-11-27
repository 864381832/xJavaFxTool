package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.ImageToolController;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class ImageToolService {
    private ImageToolController imageToolController;

    public void imageCompressionAction() throws IOException {
        int width = imageToolController.getScaleWidthSpinner().getValue();
        int height = imageToolController.getScaleHeightSpinner().getValue();
        String suffixName = imageToolController.getSuffixNameTextField().getText();
        String formatString = imageToolController.getFormatChoiceBox().getValue();
        for(Map<String,String> rowValue : imageToolController.getTableData()){
            File file = new File(rowValue.get("fullPath"));
            String outputFileName = file.getName();
            Thumbnails.Builder<File> builder = Thumbnails.of(file);
            builder.outputQuality(imageToolController.getQualitySlider().getValue()/100.0d);//压缩率
            if(!"原格式".equals(formatString)){//修改格式
                builder.outputFormat(formatString);
                outputFileName = FileUtil.getFileName(file)+"."+formatString;
            }
            if(imageToolController.getIsResizeCheckBox().isSelected()){//缩放大小
                if(imageToolController.getSizeTypeChoiceBoxStrings()[0].equals(imageToolController.getSizeTypeChoiceBox().getValue())){
                    builder.size(width,height);
                }else if(imageToolController.getSizeTypeChoiceBoxStrings()[1].equals(imageToolController.getSizeTypeChoiceBox().getValue())){
                    builder.scale(width/100.0d,height/100.0d);
                }
                if(!imageToolController.getKeepAspectRatioCheckBox().isSelected()){
                    builder.keepAspectRatio(false);
                }
            }else{
                builder.scale(1);
            }
            if(imageToolController.getSameFolderAsInput().isSelected()){//输出路径为原路径
                if(StringUtils.isEmpty(suffixName)){
                    TooltipUtil.showToast("保存在同目录下需添加前缀名。");
                }else{
                    outputFileName = outputFileName.split("\\.")[0]+suffixName+"."+outputFileName.split("\\.")[1];
                    builder.toFile(file.getParent()+"/"+outputFileName);
                }
            }else{
                String outputPath = imageToolController.getOutputFolderTextField().getText();
                if(StringUtils.isEmpty(outputPath)){
                    TooltipUtil.showToast("请选择需要保存的路径。");
                }else{
                    builder.toFile(outputPath+"/"+outputFileName);
                }
            }
        }
    }

    public BufferedImage getThumbnailsBuilder(String fullPath){//获取压缩工具builder
        int width = imageToolController.getScaleWidthSpinner().getValue();
        int height = imageToolController.getScaleHeightSpinner().getValue();
        File file = new File(fullPath);
        Thumbnails.Builder<File> builder = Thumbnails.of(file);
        builder.outputQuality(imageToolController.getQualitySlider().getValue()/100.0d);//压缩率
        if(imageToolController.getIsResizeCheckBox().isSelected()){//缩放大小
            if(imageToolController.getSizeTypeChoiceBoxStrings()[0].equals(imageToolController.getSizeTypeChoiceBox().getValue())){
                builder.size(width,height);
            }else if(imageToolController.getSizeTypeChoiceBoxStrings()[1].equals(imageToolController.getSizeTypeChoiceBox().getValue())){
                builder.scale(width/100.0d,height/100.0d);
            }
            if(!imageToolController.getKeepAspectRatioCheckBox().isSelected()){
                builder.keepAspectRatio(false);
            }
        }else{
            builder.scale(1);
        }
        try {
            return builder.asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ImageToolService(ImageToolController imageToolController) {
        this.imageToolController = imageToolController;
    }
}