package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.ImageToolController;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxProgressDialog;
import com.xwintop.xcore.javafx.dialog.ProgressTask;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ImageToolService
 *
 * @author xufeng
 * @author yiding.he@gmail.com
 * @since 2017/12/28 0028 23:28
 */
@Getter
@Setter
@Slf4j
public class ImageToolService {

    private ImageToolController imageToolController;

    public void processImages(ImageParameters parameters, boolean outputToFile) {
        List<ImageInfo> imageInfos = new ArrayList<>(imageToolController.getTableData());
        processImages(imageInfos, parameters, outputToFile);
    }

    public void processNewImages(ImageParameters parameters, boolean outputToFile) {
        List<ImageInfo> imageInfos = imageToolController.getTableData().stream()
            .filter(i -> i.getCompressedSize() == 0)
            .collect(Collectors.toList());
        processImages(imageInfos, parameters, outputToFile);
    }

    private void processImages(List<ImageInfo> imageInfos, ImageParameters parameters, boolean outputToFile) {
        FxProgressDialog fxProgressDialog = FxProgressDialog.create(FxApp.primaryStage, new ProgressTask() {
            @Override
            protected void execute() throws Exception {
                int total = imageInfos.size();
                for (int i = 0; i < total; i++) {
                    ImageInfo imageInfo = imageInfos.get(i);
                    processImage(imageInfo, parameters, outputToFile);
                    updateProgress(i + 1, total);
                }
            }
        }, "正在处理图片...");

        fxProgressDialog.setShowAsPercentage(false);
        fxProgressDialog.show();
    }

    public void processImage(ImageInfo imageInfo, ImageParameters parameters, boolean outputToFile) throws IOException {
        String filePath = imageInfo.getImageFilePath();
        File file = new File(filePath);
        String outputFileName = FilenameUtils.getName(filePath);
        String baseName = FilenameUtils.getBaseName(filePath);
        String ext = StringUtils.defaultIfBlank(parameters.getFormat().getExt(), FilenameUtils.getExtension(filePath));
        // 创建 builder
        Thumbnails.Builder<File> builder = Thumbnails.of(file);
        builder.outputFormat(ext);//输出格式
//        if (parameters.getFormat() == OutputFormats.JPEG) {
            builder.outputQuality(parameters.getJpegQuality() / 100.0);
//        }
        // 如果要调整图片大小
        if (parameters.isResizeImage()) {
            if (parameters.getResizeMode() == ResizeMode.Pixel) {
                resizeByPixel(parameters, builder, imageInfo);
            } else if (parameters.getResizeMode() == ResizeMode.Percentage) {
                resizeByPercentage(parameters, builder);
            }
            if (!parameters.isKeepRatio()) {
                builder.keepAspectRatio(false);
            }
        } else {
            builder.scale(1);
        }
        if (parameters.getRotate() > 0) {
            builder.rotate(parameters.getRotate());//设置旋转角度
        }
        // 生成处理结果图片
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        builder.toOutputStream(bos);
        imageInfo.setCompressedSize(bos.size());

        // 如果该图片刚好已经是选中的，则更新预览
        if (imageToolController.getSelectedImage() == imageInfo) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            Image image = new Image(bis);
            ImageView imageView = imageToolController.getOutputImageView();
            imageView.setImage(image);
            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());
        }

        // 如果需要输出到文件
        if (outputToFile) {
            boolean saveToOriginalFolder = imageToolController.getSameFolderAsInput().isSelected();
            outputFileName = baseName + parameters.getOutputFileNameSuffix() + "." + ext;
            if (saveToOriginalFolder) {
                if (StringUtils.isEmpty(parameters.getOutputFileNameSuffix())) {
                    TooltipUtil.showToast("保存在同目录下需添加前缀名。");
                } else {
                    FileUtils.writeByteArrayToFile(new File(file.getParent(), outputFileName), bos.toByteArray());
                }
            } else {
                String outputPath = imageToolController.getOutputFolderTextField().getText();
                FileUtils.writeByteArrayToFile(new File(StringUtils.defaultIfEmpty(outputPath, file.getParent()), outputFileName), bos.toByteArray());
            }
        }
    }

    private void resizeByPercentage(ImageParameters parameters, Builder<File> builder) {
        int targetWidth = defaultIfZero(parameters.getTargetWidth(), 100);
        int targetHeight = defaultIfZero(parameters.getTargetHeight(), 100);
        builder.scale(targetWidth / 100.0d, targetHeight / 100.0d);
    }

    private void resizeByPixel(ImageParameters parameters, Builder<File> builder, ImageInfo imageInfo) {
        int targetWidth = defaultIfZero(parameters.getTargetWidth(), imageInfo.getWidth());
        int targetHeight = defaultIfZero(parameters.getTargetHeight(), imageInfo.getHeight());
        builder.size(targetWidth, targetHeight);
    }

    private int defaultIfZero(int n, int def) {
        return n == 0 ? def : n;
    }

    public ImageToolService(ImageToolController imageToolController) {
        this.imageToolController = imageToolController;
    }
}
