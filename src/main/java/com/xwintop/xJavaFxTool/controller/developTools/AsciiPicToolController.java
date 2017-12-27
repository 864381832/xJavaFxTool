package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.view.developTools.AsciiPicToolView;
import com.xwintop.xJavaFxTool.services.developTools.AsciiPicToolService;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @ClassName: AsciiPicToolController
 * @Description: 图片转ascii
 * @author: xufeng
 * @date: 2017/12/24 0024 23:18
 */
@Getter
@Setter
@Log4j
public class AsciiPicToolController extends AsciiPicToolView {
    private AsciiPicToolService asciiPicToolService = new AsciiPicToolService(this);
    private String[] imageSize = new String[]{"不压缩", "60*60", "120*120", "256*256", "512*512"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        imageSizeComboBox.getItems().addAll(imageSize);
        imageSizeComboBox.getSelectionModel().select(0);
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(filePathTextField, FileChooserUtil.FileType.FILE);
        filePathTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            imageImageView.setImage(new Image("file:" + newValue));
            imageImageView.setFitWidth(imageImageView.getImage().getWidth());
            imageImageView.setFitHeight(imageImageView.getImage().getHeight());
        });
        codeTextArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            asciiPicToolService.buildBase64ToImage(newValue);
        });
    }

    private void initService() {
    }

    @FXML
    private void filePathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            filePathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void buildBannerAction(ActionEvent event) {
        asciiPicToolService.buildBannerAction();
    }

    @FXML
    private void buildBase64Action(ActionEvent event) {
        asciiPicToolService.buildBase64Action();
    }

    @FXML
    private void saveImageAction(ActionEvent event) {
        asciiPicToolService.saveImageAction();
    }
}