package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.ExcelSplitToolService;
import com.xwintop.xJavaFxTool.view.littleTools.ExcelSplitToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ExcelSplitToolController
 * @Description: Excel拆分工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:18
 */

@Getter
@Setter
@Slf4j
public class ExcelSplitToolController extends ExcelSplitToolView {
    private ExcelSplitToolService excelSplitToolService = new ExcelSplitToolService(this);
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
        JavaFxViewUtil.setSpinnerValueFactory(splitType1Spinner, 1, Integer.MAX_VALUE, 3);
        JavaFxViewUtil.setSpinnerValueFactory(splitType2Spinner, 1, Integer.MAX_VALUE, 10);
    }

    private void initEvent() {
//        selectFileTextField.setText("C:\\Users\\5FDSJ068\\Desktop\\test\\file.csv");
        FileChooserUtil.setOnDrag(selectFileTextField, FileChooserUtil.FileType.FILE);
        FileChooserUtil.setOnDrag(saveFilePathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void selectFileAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
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
    private void splitAction(ActionEvent event) {
        TooltipUtil.showToast("正在解析请稍后...");
        new Thread(() -> {
            try {
                excelSplitToolService.splitAction();
                log.info("解析完成。");
            } catch (Exception e) {
//                TooltipUtil.showToast("解析失败：" + e.getMessage());
                log.error("解析失败：", e);
            }
        }).start();
//        TooltipUtil.showToast("解析完成！");
    }
}