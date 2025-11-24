package com.xwintop.xJavaFxTool.view.assistTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: DecompilerWxApkgToolView
 * @Description: 微信小程序反编译工具
 * @author: xufeng
 * @date: 2018/7/4 14:44
 */

@Getter
@Setter
public abstract class DecompilerWxApkgToolView implements Initializable {
    @FXML
    protected TextField packageFileTextField;
    @FXML
    protected Button packageFileButton;
    @FXML
    protected TextField decompilePathTextField;
    @FXML
    protected Button decompilePathButton;
    @FXML
    protected Button decompileButton;

}