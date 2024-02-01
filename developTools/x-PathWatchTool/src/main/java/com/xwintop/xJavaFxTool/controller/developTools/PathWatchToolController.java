package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.PathWatchToolService;
import com.xwintop.xJavaFxTool.view.developTools.PathWatchToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: PathWatchToolController
 * @Description: 文件夹监控工具
 * @author: xufeng
 * @date: 2019/4/27 0027 1:06
 */

@Getter
@Setter
@Slf4j
public class PathWatchToolController extends PathWatchToolView {
    private PathWatchToolService pathWatchToolService = new PathWatchToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(watchPathTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void watchPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            watchPathTextField.setText(file.getPath());
        }
    }

    @FXML
    private void watchAction(ActionEvent event) throws Exception {
        if ("监控".equals(watchButton.getText())) {
            pathWatchToolService.watchAction();
            watchButton.setText("停止监控");
        } else {
            pathWatchToolService.stopWatchAction();
            watchButton.setText("监控");
        }
    }
}