package com.xwintop.xJavaFxTool.view.debugTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;
import org.fxmisc.richtext.CodeArea;

/**
 * @ClassName: SwitchHostsToolView
 * @Description: 切换Hosts工具
 * @author: xufeng
 * @date: 2018/1/31 15:18
 */

@Getter
@Setter
public abstract class SwitchHostsToolView implements Initializable {
    @FXML
    protected Button addButton;
    @FXML
    protected Button reloadButton;
    @FXML
    protected Button editButton;
    @FXML
    protected Button deleteButton;
    @FXML
    protected TreeView<String> hostFileTreeView;
    @FXML
//    protected TextArea hostTextArea;
    protected CodeArea hostTextArea;

}