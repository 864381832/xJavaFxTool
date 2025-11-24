package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.view.littleTools.ImageAnalysisToolView;
import com.xwintop.xJavaFxTool.services.littleTools.ImageAnalysisToolService;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @ClassName: ImageAnalysisToolController
 * @Description: 图片解析工具Controller
 * @author: xufeng
 * @date: 2018/6/25 13:36
 */

@Getter
@Setter
@Slf4j
public class ImageAnalysisToolController extends ImageAnalysisToolView {
    private ImageAnalysisToolService imageAnalysisToolService = new ImageAnalysisToolService(this);
    private String[] analysisOrientationComboBoxItems = {"水平", "垂直"};//图片拆分方向
    private Integer[] analysisNumberComboBoxItems = {2, 3, 4, 5, 6, 7, 8, 9, 10};//图片拆分块数

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        analysisOrientationComboBox.getItems().addAll(analysisOrientationComboBoxItems);
        analysisOrientationComboBox.getSelectionModel().selectFirst();
        analysisNumberComboBox.getItems().addAll(analysisNumberComboBoxItems);
        analysisNumberComboBox.getSelectionModel().selectFirst();
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(atlasPathTextField, FileChooserUtil.FileType.FILE);
        FileChooserUtil.setOnDrag(imagePathTextField, FileChooserUtil.FileType.FILE);
        FileChooserUtil.setOnDrag(outputPathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void atlasPathButtonAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            atlasPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void imagePathButtonAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            imagePathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void outputPathButtonAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            outputPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void analysisAtlasButtonAction(ActionEvent event) throws Exception {
        imageAnalysisToolService.analysisAtlasButtonAction();
    }

    @FXML
    private void analysisImageButtonAction(ActionEvent event) {
        imageAnalysisToolService.analysisImageButtonAction();
    }
}