package com.xwintop.xJavaFxTool.controller.epmsTools;

import cn.hutool.core.util.RandomUtil;
import com.xwintop.xJavaFxTool.services.epmsTools.DxpMsgToolService;
import com.xwintop.xJavaFxTool.view.epmsTools.DxpMsgToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: DxpMsgToolController
 * @Description: dxp报文解析工具
 * @author: xufeng
 * @date: 2019/12/20 11:13
 */

@Getter
@Setter
@Slf4j
public class DxpMsgToolController extends DxpMsgToolView {
    private DxpMsgToolService dxpMsgToolService = new DxpMsgToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        fileNameTextField.setText(RandomUtil.randomString(32));
        copMsgIdTextField.setText(RandomUtil.randomString(32));
        msgTypeTextField.setText("INV");
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(savePathTextField, FileChooserUtil.FileType.FOLDER);
        dataTextArea.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dataTextArea) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });
        dataTextArea.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    try {
                        File file = dragboard.getFiles().get(0);
                        if (file != null) {
                            if (file.isFile()) {
                                dxpMsgToolService.parserDxpMsg(FileUtils.readFileToByteArray(file));
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析错误：", e);
                    }
                }
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void createAction(ActionEvent event) {
        try {
            dxpMsgToolService.createAction();
            TooltipUtil.showToast("生成完成！");
        } catch (Exception e) {
            log.error("生成出错：", e);
            TooltipUtil.showToast("生成出错了：" + e.getMessage());
        }
    }

    @FXML
    private void parserAction(ActionEvent event) {
        try {
            dxpMsgToolService.parserDxpMsg(dataTextArea.getText().getBytes());
            TooltipUtil.showToast("解析完成！");
        } catch (Exception e) {
            log.error("解析出错：", e);
            TooltipUtil.showToast("解析出错了：" + e.getMessage());
        }
    }
}