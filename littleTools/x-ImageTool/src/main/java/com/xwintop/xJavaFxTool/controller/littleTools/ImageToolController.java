package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.*;
import com.xwintop.xJavaFxTool.view.littleTools.ImageToolView;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxProgressDialog;
import com.xwintop.xcore.javafx.dialog.ProgressTask;
import com.xwintop.xcore.javafx.helper.ChoiceBoxHelper;
import com.xwintop.xcore.javafx.helper.DropContentHelper;
import com.xwintop.xcore.javafx.helper.MenuHelper;
import com.xwintop.xcore.javafx.helper.TableViewHelper;
import com.xwintop.xcore.javafx.wrapper.ContextMenuWrapper;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 图片工具控制类
 *
 * @author xufeng
 * @author yiding.he@gmail.com
 */

@Getter
@Setter
@Slf4j
public class ImageToolController extends ImageToolView {

    private ImageToolService imageToolService = new ImageToolService(this);

    private ObservableList<ImageInfo> tableData = FXCollections.observableArrayList();//表格数据

    private ChangeListener imageParameterChangedListener = (_ob, _old, _new) -> imageParameterChanged();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        initTable();

        initFormatChoiceBox();
        initResizeModeChoiceBox();

        qualitySlider.setOnMouseReleased(event -> imageParameterChanged());
        rotateSlider.setOnMouseReleased(event -> imageParameterChanged());

        // 当下面这些属性变化时触发重新计算图片大小
        addToImageParameterChangedListener(
            formatChoiceBox.valueProperty(),
            isResizeCheckBox.selectedProperty(),
            resizeModeChoiceBox.valueProperty(),
            keepAspectRatioCheckBox.selectedProperty(),
            scaleWidthSpinner.valueProperty(),
            scaleHeightSpinner.valueProperty()
        );
    }

    private void addToImageParameterChangedListener(ObservableValue<?>... observableValues) {
        for (ObservableValue<?> observableValue : observableValues) {
            observableValue.addListener(imageParameterChangedListener);
        }
    }

    private void initResizeModeChoiceBox() {
        ChoiceBoxHelper.setContentDisplay(resizeModeChoiceBox, ResizeMode.class, ResizeMode::getName);
        resizeModeChoiceBox.getSelectionModel().selectFirst();
        JavaFxViewUtil.setSpinnerValueFactory(scaleWidthSpinner, 0, 9999);
        JavaFxViewUtil.setSpinnerValueFactory(scaleHeightSpinner, 0, 9999);
        resizeModeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == ResizeMode.Pixel) {
                JavaFxViewUtil.setSpinnerValueFactory(scaleWidthSpinner, 0, 9999);
                JavaFxViewUtil.setSpinnerValueFactory(scaleHeightSpinner, 0, 9999);
            } else {
                JavaFxViewUtil.setSpinnerValueFactory(scaleWidthSpinner, 0, 100);
                JavaFxViewUtil.setSpinnerValueFactory(scaleHeightSpinner, 0, 100);
            }
        });
        BooleanBinding isResizeDisabled = isResizeCheckBox.selectedProperty().not();
        resizeModeChoiceBox.disableProperty().bind(isResizeDisabled);
        keepAspectRatioCheckBox.disableProperty().bind(isResizeDisabled);
        scaleWidthSpinner.disableProperty().bind(isResizeDisabled);
        scaleHeightSpinner.disableProperty().bind(isResizeDisabled);
    }

    private void imageParameterChanged() {
        imageToolService.processImages(collectParameters(), false);
    }

    private void initFormatChoiceBox() {
        ChoiceBoxHelper.setContentDisplay(
            formatChoiceBox, OutputFormats.class,
            f -> f.getName() + (f.getExt() == null ? "" : ("(*." + f.getExt() + ")"))
        );
        formatChoiceBox.getSelectionModel().selectFirst();
    }

    public ImageInfo getSelectedImage() {
        return tableViewMain.getSelectionModel().getSelectedItem();
    }

    private void initTable() {
        TableViewHelper.of(tableViewMain)
            .addStrPropertyColumn("文件", ImageInfo::imageFileNameProperty)
            .addStrPropertyColumn("原始大小", ImageInfo::originalSizeTextProperty)
            .addStrPropertyColumn("压缩后大小", ImageInfo::compressedSizeTextProperty)
            .addStrPropertyColumn("压缩比率", ImageInfo::compressionRateProperty)
            .addStrPropertyColumn("图像尺寸", ImageInfo::imageSizeProperty)
            .addStrPropertyColumn("文件路径", ImageInfo::imageFilePathProperty)
        ;

        tableViewMain.setItems(tableData);
        DropContentHelper.accept(tableViewMain, Clipboard::hasFiles,
            (_tableView, _dragboard) -> {
                loadImageFiles(_dragboard.getFiles());
                imageToolService.processNewImages(collectParameters(), false);
            });
    }

    private void initEvent() {
        TableViewHelper.of(tableViewMain)
            .setOnItemChange((_old, _new) -> {
                updateOriginPreview();
                updateResultPreview();
            });

        ContextMenuWrapper.of(MenuHelper.contextMenu(
            MenuHelper.menuItem("移除选中行", this::removeSelectedImage),
            MenuHelper.menuItem("清空列表", this::removeAllImages)
        )).wrap(tableViewMain);

        FileChooserUtil.setOnDrag(outputFolderTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void removeSelectedImage() {
        ImageInfo selectedImage = getSelectedImage();
        if (selectedImage == null) {
            return;
        }
        tableData.remove(selectedImage);
    }

    private void removeAllImages() {
        tableData.clear();
        originalImageView.setImage(null);
        outputImageView.setImage(null);
    }

    private void updateOriginPreview() {
        ImageInfo imageInfo = getSelectedImage();
        if (imageInfo == null) {
            return;
        }
        try {
            Image image = SwingFXUtils.toFXImage(ImageIO.read(new File(imageInfo.getImageFilePath())), null);
            originalImageView.setImage(image);
            originalImageView.setFitWidth(image.getWidth());
            originalImageView.setFitHeight(image.getHeight());
        } catch (Exception e) {
            log.error("读取源图片失败", e);
            TooltipUtil.showToast("读取源图片失败:" + e.getMessage());
        }
    }

    private void updateResultPreview() {
        try {
            ImageInfo selectedImage = getSelectedImage();
            if (selectedImage != null) {
                imageToolService.processImage(selectedImage, collectParameters(), false);
            }
        } catch (IOException e) {
            TooltipUtil.showToast("刷新预览失败：" + e.getMessage());
        }
    }

    @FXML
    private void addImageAction() throws Exception {
        File file = FileChooserUtil.chooseFile((ExtensionFilter[]) null);
        if (file != null) {
            onImageFileAdd(file);
        }
    }

    private void onImageFileAdd(File file) throws IOException {
        ImageInfo imageInfo = createImageInfo(file);
        tableData.add(imageInfo);
        imageToolService.processImage(imageInfo, collectParameters(), false);
    }

    @FXML
    private void openFolderAction() {
        File folder = FileChooserUtil.chooseDirectory();
        if (folder != null) {
            loadImageFiles(folder);
            imageToolService.processImages(collectParameters(), false);
        }
    }

    private ImageParameters collectParameters() {
        final ImageParameters parameters = new ImageParameters();
        parameters.setTargetWidth(scaleWidthSpinner.getValue());
        parameters.setTargetHeight(scaleHeightSpinner.getValue());
        parameters.setKeepRatio(keepAspectRatioCheckBox.isSelected());
        parameters.setFormat(formatChoiceBox.getValue());
        parameters.setJpegQuality(qualitySlider.getValue());
        parameters.setOutputFileNameSuffix(suffixNameTextField.getText());
        parameters.setResizeImage(isResizeCheckBox.isSelected());
        parameters.setResizeMode(resizeModeChoiceBox.getValue());
        parameters.setRotate(rotateSlider.getValue());
        return parameters;
    }

    private void loadImageFiles(File folder) {
        loadImageFiles(Collections.singletonList(folder));
    }

    private void loadImageFiles(List<File> imageFiles) {
        final List<File> finalImageFiles = scanImageFiles(imageFiles);
        log.info("image count: {}", finalImageFiles.size());
        FxProgressDialog fxProgressDialog = FxProgressDialog.create(FxApp.primaryStage, new ProgressTask() {
            @Override
            protected void execute() {
                List<ImageInfo> imageInfos = new ArrayList<>();
                int size = finalImageFiles.size();
                for (int i = 0; i < size; i++) {
                    File file = finalImageFiles.get(i);
                    imageInfos.add(createImageInfo(file));
                    updateProgress(i + 1, size);
                }
                tableData.addAll(imageInfos);
            }
        }, "正在扫描文件...");

        // 这里用 showProgressAndWait 是因为后面还有一个后台服务，而且必须等这个执行完毕
        fxProgressDialog.setShowAsPercentage(false);
        fxProgressDialog.showAndWait();
    }

    /**
     * 扫描文件或文件夹，列出所有的文件
     */
    private List<File> scanImageFiles(List<File> filesAndFolders) {
        List<File> files = new ArrayList<>();
        try {
            for (File filesAndFolder : filesAndFolders) {
                if (filesAndFolder.isDirectory()) {
                    File[] childFiles = filesAndFolder.listFiles();
                    if (childFiles != null) {
                        files.addAll(scanImageFiles(Arrays.asList(childFiles)));
                    }
                } else if (AcceptableFormats.isAcceptable(filesAndFolder)) {
                    files.add(filesAndFolder);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return files;
    }

    private ImageInfo createImageInfo(File file) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageFilePath(file.getAbsolutePath());
        imageInfo.setOriginalSize((int) file.length());
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            imageInfo.setWidth(bufferedImage.getWidth());
            imageInfo.setHeight(bufferedImage.getHeight());
        } catch (IOException e) {
            log.error("图片读取失败", e);
        }
        return imageInfo;
    }

    @FXML
    private void imageCompressionAction() {
        try {
            imageToolService.processImages(collectParameters(), true);
            TooltipUtil.showToast("图片压缩完成！");
        } catch (Exception e) {
            TooltipUtil.showToast("图片压缩失败：" + e.getMessage());
        }
    }

    @FXML
    private void refreshPreviewAction() {
        try {
            imageToolService.processImages(collectParameters(), false);
            tableViewMain.refresh();
            TooltipUtil.showToast("刷新预览完成！");
        } catch (Exception e) {
            TooltipUtil.showToast("刷新预览失败：" + e.getMessage());
        }
    }

    @FXML
    private void openOutputFolderAction() {
        try {
            String outputPath = outputFolderTextField.getText();
            if (StringUtils.isEmpty(outputPath)) {
                List<String> paths = new ArrayList<>();
                for (ImageInfo rowValue : tableData) {
                    File file = new File(rowValue.getImageFilePath());
                    String path = file.getParent();
                    if (!paths.contains(path)) {
                        paths.add(path);
                        JavaFxSystemUtil.openDirectory(path);
                    }
                }
            } else {
                JavaFxSystemUtil.openDirectory(outputPath);
            }
            TooltipUtil.showToast("图片压缩完成：");
        } catch (Exception e) {
            TooltipUtil.showToast("图片压缩失败：" + e.getMessage());
        }
    }

    @FXML
    private void outputFolderAction() {
        File folder = FileChooserUtil.chooseDirectory();
        if (folder != null) {
            outputFolderTextField.setText(folder.getPath());
        }
    }
}
