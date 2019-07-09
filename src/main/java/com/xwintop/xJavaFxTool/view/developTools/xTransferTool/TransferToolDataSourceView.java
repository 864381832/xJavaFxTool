package com.xwintop.xJavaFxTool.view.developTools.xTransferTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: TransferToolDataSourceView
 * @Description: 数据源配置
 * @author: xufeng
 * @date: 2019/6/28 18:00
 */

@Getter
@Setter
public abstract class TransferToolDataSourceView implements Initializable {
    @FXML
    protected SplitPane dataSourceSplitPane;
    @FXML
    protected FlowPane dataSourceFlowPane;
    @FXML
    protected Button saveButton;
    @FXML
    protected Button viewButton;
}
