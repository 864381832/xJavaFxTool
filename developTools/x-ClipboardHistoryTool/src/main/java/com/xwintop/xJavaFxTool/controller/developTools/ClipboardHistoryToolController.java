package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.ClipboardHistoryToolService;
import com.xwintop.xJavaFxTool.view.developTools.ClipboardHistoryToolView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: ClipboardHistoryToolController
 * @Description: 剪贴板历史工具
 * @author: xufeng
 * @date: 2019/6/15 0015 18:35
 */

@Getter
@Setter
@Slf4j
public class ClipboardHistoryToolController extends ClipboardHistoryToolView {
    private ClipboardHistoryToolService clipboardHistoryToolService = new ClipboardHistoryToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
        if (ClipboardHistoryToolService.ClipboardUtil.textAreaList.isEmpty()) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(clipboard.getContents(null), ClipboardHistoryToolService.ClipboardUtil.clipboardUtil);
        }
        ClipboardHistoryToolService.ClipboardUtil.textAreaList.add(clipboardHistory);
    }

    private void initService() {
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) {
        log.info("移除剪贴板历史监听");
        ClipboardHistoryToolService.ClipboardUtil.textAreaList.remove(clipboardHistory);
    }
}