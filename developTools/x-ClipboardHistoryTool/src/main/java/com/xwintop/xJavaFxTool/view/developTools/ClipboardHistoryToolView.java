package com.xwintop.xJavaFxTool.view.developTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: ClipboardHistoryToolView
 * @Description: 剪贴板历史工具
 * @author: xufeng
 * @date: 2019/6/15 0015 18:35
 */

@Getter
@Setter
public abstract class ClipboardHistoryToolView implements Initializable {
    @FXML
    protected TextArea clipboardHistory;

}