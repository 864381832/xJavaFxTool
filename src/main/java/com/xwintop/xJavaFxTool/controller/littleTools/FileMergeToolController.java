package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.FileMergeToolService;
import com.xwintop.xJavaFxTool.view.littleTools.FileMergeToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
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

/**
 * @ClassName: FileMergeToolController
 * @Description: 文件合并工具
 * @author: xufeng
 * @date: 2019/6/11 17:16
 */

@Getter
@Setter
@Slf4j
public class FileMergeToolController extends FileMergeToolView {
    private FileMergeToolService fileMergeToolService = new FileMergeToolService(this);
    private String[] fileTypeChoiceBoxStrings = new String[]{"Excel", "CSV", "文件"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        fileTypeChoiceBox.getItems().addAll(fileTypeChoiceBoxStrings);
        fileTypeChoiceBox.getSelectionModel().select(0);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(selectFileTextField, FileChooserUtil.FileType.FOLDER);
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
    private void mergeAction(ActionEvent event) {
        TooltipUtil.showToast("正在解析请稍后...");
        new Thread(() -> {
            try {
                fileMergeToolService.mergeAction();
                log.info("解析完成。");
                Platform.runLater(() -> {
                    TooltipUtil.showToast("解析完成。");
                });
            } catch (Exception e) {
                log.error("解析失败：", e);
            }
        }).start();
    }
}