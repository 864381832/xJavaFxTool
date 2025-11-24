package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.FileCompressToolService;
import com.xwintop.xJavaFxTool.view.littleTools.FileCompressToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.apache.commons.compress.archivers.ArchiveStreamFactory.*;
import static org.apache.commons.compress.compressors.CompressorStreamFactory.*;

/**
 * @ClassName: FileCompressToolController
 * @Description: 文件解压缩工具
 * @author: xufeng
 * @date: 2019/10/26 0026 19:17
 */

@Getter
@Setter
@Slf4j
public class FileCompressToolController extends FileCompressToolView {
    private FileCompressToolService fileCompressToolService = new FileCompressToolService(this);
    private String[] fileTypeChoiceBoxStrings = new String[]{"AUTO", AR, ZIP, TAR, JAR, CPIO, SEVEN_Z, GZIP, BZIP2, XZ, LZMA, PACK200, DEFLATE, SNAPPY_FRAMED, LZ4_BLOCK, LZ4_FRAMED, ZSTANDARD, "RAR"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        fileTypeChoiceBox.getItems().addAll(fileTypeChoiceBoxStrings);
        fileTypeChoiceBox.getSelectionModel().select(2);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(selectFileTextField, FileChooserUtil.FileType.FILE);
        FileChooserUtil.setOnDrag(saveFilePathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void selectFileAction(ActionEvent event) {
        List<File> files = null;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("请选择文件");
            fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
            files = fileChooser.showOpenMultipleDialog(null);
        } catch (NullPointerException e) {
            log.error("选择文件错误", e);
        }
        if (files != null) {
            List<String> strings = new ArrayList<>();
            for (File file : files) {
                strings.add(file.getPath());
            }
            selectFileTextField.setText(String.join("|", strings));
        }
    }

    @FXML
    private void selectFolderAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            selectFileTextField.setText(file.getPath());
        }
    }

    @FXML
    private void saveFilePathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            saveFilePathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void compressAction(ActionEvent event) {
        try {
            fileCompressToolService.compressAction();
        } catch (Exception e) {
            log.error("操作失败：", e);
            TooltipUtil.showToast("操作失败！" + e.getMessage());
        }
    }

}