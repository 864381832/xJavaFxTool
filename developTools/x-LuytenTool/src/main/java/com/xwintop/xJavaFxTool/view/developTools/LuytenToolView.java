package com.xwintop.xJavaFxTool.view.developTools;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LuytenToolView implements Initializable {
    @FXML
    protected SwingNode luytenSwingNode;
}