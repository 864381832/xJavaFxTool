package com.xwintop.xJavaFxTool.services.developTools;

import cn.hutool.core.date.DateUtil;
import com.xwintop.xJavaFxTool.controller.developTools.ClipboardHistoryToolController;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ClipboardHistoryToolService
 * @Description: 剪贴板历史工具
 * @author: xufeng
 * @date: 2019/6/15 0015 18:36
 */

@Getter
@Setter
@Slf4j
public class ClipboardHistoryToolService {
    private ClipboardHistoryToolController clipboardHistoryToolController;

    public ClipboardHistoryToolService(ClipboardHistoryToolController clipboardHistoryToolController) {
        this.clipboardHistoryToolController = clipboardHistoryToolController;
    }

    public static class ClipboardUtil implements ClipboardOwner {
        public final static ClipboardUtil clipboardUtil = new ClipboardUtil();
        public static List<TextArea> textAreaList = new ArrayList<>();

        private ClipboardUtil() {

        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable oldContents) {
            // 如果不暂停一下，经常会抛出IllegalStateException
            // 猜测是操作系统正在使用系统剪切板，故暂时无法访问
            if (textAreaList.isEmpty()) {
                return;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Transferable contents = clipboard.getContents(null);
//             判断剪贴板中的内容是否支持文本
            try {
                String text = null;
                if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    // 获取剪贴板中的文本内容
                    text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                } else if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    Object object = contents.getTransferData(DataFlavor.imageFlavor);
                    text = object.toString();
                } else if (contents.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
                    Object object = contents.getTransferData(DataFlavor.plainTextFlavor);
                    text = object.toString();
                } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    Object object = contents.getTransferData(DataFlavor.javaFileListFlavor);
                    text = object.toString();
                } else if (contents.isDataFlavorSupported(DataFlavor.selectionHtmlFlavor)) {
                    Object object = contents.getTransferData(DataFlavor.selectionHtmlFlavor);
                    text = object.toString();
                } else if (contents.isDataFlavorSupported(DataFlavor.fragmentHtmlFlavor)) {
                    Object object = contents.getTransferData(DataFlavor.fragmentHtmlFlavor);
                    text = object.toString();
                } else if (contents.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
                    Object object = contents.getTransferData(DataFlavor.allHtmlFlavor);
                    text = object.toString();
                }
                for (TextArea textArea : textAreaList) {
                    textArea.appendText("---------------------------------------\r\n");
                    textArea.appendText(DateUtil.formatDateTime(new Date()) + "\r\n");
                    textArea.appendText(text + "\r\n\r\n");
                }
            } catch (Exception e) {
                log.error("获取剪贴板内容失败", e);
            }
            clipboard.setContents(contents, this);
        }
    }
}