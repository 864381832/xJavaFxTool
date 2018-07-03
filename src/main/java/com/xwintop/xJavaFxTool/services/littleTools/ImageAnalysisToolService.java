package com.xwintop.xJavaFxTool.services.littleTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.littleTools.ImageAnalysisToolController;
import com.xwintop.xJavaFxTool.utils.ImgToolUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: ImageAnalysisToolService
 * @Description: 图片解析工具Service
 * @author: xufeng
 * @date: 2018/6/25 13:35
 */

@Getter
@Setter
@Slf4j
public class ImageAnalysisToolService {
    private ImageAnalysisToolController imageAnalysisToolController;

    //解析atlas文件
    public void analysisAtlasButtonAction() throws Exception {
        String atlasPathTextFieldString = imageAnalysisToolController.getAtlasPathTextField().getText();
        if (StringUtils.isBlank(atlasPathTextFieldString)) {
            TooltipUtil.showToast("atlas路径名不能为空。");
            return;
        }
        if (!"atlas".equals(FilenameUtils.getExtension(atlasPathTextFieldString))) {
            TooltipUtil.showToast("非.atlas文件。");
            return;
        }
        String imagePathTextFieldString = imageAnalysisToolController.getImagePathTextField().getText();
        if (StringUtils.isBlank(imagePathTextFieldString)) {
            String fileName = FilenameUtils.getName(atlasPathTextFieldString);
            String fileBaseName = FilenameUtils.getBaseName(fileName);
            AtomicReference<File> imageFile = new AtomicReference<>();
            new File(atlasPathTextFieldString).getParentFile().listFiles(pathname -> {
                if (!pathname.getName().equals(fileName) && FilenameUtils.getBaseName(pathname.getName()).equals(fileBaseName)) {
                    imageFile.set(pathname);
                    return true;
                }
                return false;
            });
            if (imageFile.get() == null) {
                TooltipUtil.showToast("未找到图片文件。");
                return;
            }
            imagePathTextFieldString = imageFile.get().getAbsolutePath();
        }
        String outputPathTextFieldString = imageAnalysisToolController.getOutputPathTextField().getText();
        if (StringUtils.isBlank(outputPathTextFieldString)) {
            outputPathTextFieldString = FilenameUtils.getFullPath(atlasPathTextFieldString);
        }
        outputPathTextFieldString = StringUtils.appendIfMissing(outputPathTextFieldString, "/", "/", "\\");
        JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(new File(atlasPathTextFieldString)));
        JSONObject jsonObject2 = jsonObject.getJSONObject("frames");
        for (String s : jsonObject2.keySet()) {
            JSONObject jsonObject3 = jsonObject2.getJSONObject(s).getJSONObject("frame");
            try {
                ImgToolUtil imgToolTest = new ImgToolUtil(imagePathTextFieldString);
                imgToolTest.cut(jsonObject3.getInteger("x"), jsonObject3.getInteger("y"), jsonObject3.getInteger("w"), jsonObject3.getInteger("h"));
                imgToolTest.save(outputPathTextFieldString + s);
            } catch (Exception e) {
                log.error("图片解析错误：" + e.getMessage());
            }
        }
    }

    //拆分图片
    public void analysisImageButtonAction() {
        String imagePathTextFieldString = imageAnalysisToolController.getImagePathTextField().getText();
        if (StringUtils.isBlank(imagePathTextFieldString)) {
            TooltipUtil.showToast("图片地址不能为空。");
            return;
        }
        String outputPathTextFieldString = imageAnalysisToolController.getOutputPathTextField().getText();
        if (StringUtils.isBlank(outputPathTextFieldString)) {
            outputPathTextFieldString = FilenameUtils.getFullPath(imagePathTextFieldString);
        }
        outputPathTextFieldString = StringUtils.appendIfMissing(outputPathTextFieldString, "/", "/", "\\");
        String imageName = FilenameUtils.getBaseName(imagePathTextFieldString);
        String imageExtensionName = FilenameUtils.getExtension(imagePathTextFieldString);
        Integer analysisNumber = imageAnalysisToolController.getAnalysisNumberComboBox().getValue();
        String analysisOrientation = imageAnalysisToolController.getAnalysisOrientationComboBox().getValue();
        for (Integer i = 0; i < analysisNumber; i++) {
            try {
                ImgToolUtil imgToolTest = new ImgToolUtil(imagePathTextFieldString);
                if ("水平".equals(analysisOrientation)) {
                    imgToolTest.cut(imgToolTest.width() / analysisNumber * i, 0, imgToolTest.width() / analysisNumber, imgToolTest.height());
                } else {
                    imgToolTest.cut(0, imgToolTest.height() / analysisNumber * i, imgToolTest.width(), imgToolTest.height() / analysisNumber);
                }
                imgToolTest.save(outputPathTextFieldString + imageName + i + "." + imageExtensionName);
            } catch (Exception e) {
                log.error("图片拆分错误：" + e.getMessage());
            }
        }
    }

    public ImageAnalysisToolService(ImageAnalysisToolController imageAnalysisToolController) {
        this.imageAnalysisToolController = imageAnalysisToolController;
    }
}